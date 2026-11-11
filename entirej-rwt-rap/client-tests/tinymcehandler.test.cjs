const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const { test } = require('node:test');

function fixture(overrides = {}) {
  function element() {
    return { style: {}, children: [], parentNode: null,
      appendChild(child) { child.parentNode = this; this.children.push(child); },
      removeChild(child) { this.children = this.children.filter(x => x !== child); child.parentNode = null; },
      cloneNode() { return element(); }, getElementsByClassName() { return []; } };
  }
  const parent = { children: [], events: new Map(), append(el) { this.children.push(el); el.parentNode = this; },
    removeChild(el) { this.children = this.children.filter(x => x !== el); el.parentNode = null; },
    addListener(name, fn) { this.events.set(name, fn); }, removeListener(name) { this.events.delete(name); },
    getClientArea() { return [0, 0, 500, 300]; } };
  const listeners = new Map();
  const editors = [];
  const calls = [];
  const remote = { set(name, value) { calls.push([name, value]); }, call() {} };
  const head = element();
  const sandbox = { console, document: { createElement: element, head,
    getElementById(id) { return head.children.find(child => child.id === id) || null; } }, window: { setTimeout(fn) { fn(); } },
    rap: { registerTypeHandler() {}, getObject() { return parent; }, getRemoteObject() { return remote; },
      on(name, fn) { listeners.set(name, fn); }, off(name) { listeners.delete(name); } },
    tinymce: { baseURL: '', init(options) {
      const events = {};
      const editor = { options, initialized: false, content: '', dirty: false, removed: 0, events,
        on(name, fn) { events[name] = fn; }, addCommand() {}, getContainer() { return element(); },
        ui: { registry: { addButton() {} } }, dispatch() {},
        setContent(value) { assert.ok(this.initialized, 'content before init'); this.content = value; },
        getContent() { return this.content; }, isDirty() { return this.dirty; }, setDirty(value) { this.dirty = value; },
        focus() { this.focused = true; }, remove() { this.removed++; }, destroy() { this.removed++; },
        theme: { resizeTo(w, h) { assert.ok(Number.isFinite(w) && Number.isFinite(h)); } } };
      editor.mode = { set(value) { assert.ok(editor.initialized, 'mode before init'); editor.activeMode = value; }, get() { return editor.activeMode; } };
      editors.push(editor); options.setup(editor); return Promise.resolve([editor]);
    } }
  };
  sandbox.window.window = sandbox.window;
  sandbox.eclipsesource = {};
  sandbox.window.eclipsesource = sandbox.eclipsesource;
  vm.createContext(sandbox);
  vm.runInContext(fs.readFileSync(path.join(__dirname, '../src/resources/tinymce/tinymcehandler.js'), 'utf8'), sandbox);
  const widget = new sandbox.eclipsesource.TinymceEditor({ parent: 'parent', inline: false,
    removeToolbar: true, supportTable: true, configObj: {}, ...overrides });
  function render() { listeners.get('render')(); }
  function ready(index) { const e = editors[index]; e.initialized = true; e.events.init({ target: e }); }
  return { widget, editors, parent, listeners, calls, render, ready, head,
    createWidget() { return new sandbox.eclipsesource.TinymceEditor({ parent: 'parent', removeToolbar: false }); } };
}

test('readonly mode uses a skin that is actually shipped', () => {
  const f = fixture(); f.render();
  assert.equal(f.editors[1].options.skin, 'oxide');
  assert.equal(f.editors[1].options.license_key, 'gpl');
});
test('initial text and disabled state survive the main editor initializing first', () => {
  const f = fixture(); f.widget.setText('<p>first</p>'); f.widget.setEnable(false); f.render();
  f.ready(0); f.ready(1);
  assert.equal(f.editors[0].content, '<p>first</p>'); assert.equal(f.editors[1].content, '<p>first</p>');
  assert.equal(f.editors[0].mode.get(), 'readonly');
});
test('readonly editor may initialize before the main editor', () => {
  const f = fixture(); f.widget.setText('reverse'); f.render(); f.ready(1); f.ready(0);
  assert.equal(f.editors[0].content, 'reverse'); assert.equal(f.editors[1].content, 'reverse');
});
test('queued empty text clears an earlier value', () => {
  const f = fixture(); f.widget.setText('old'); f.widget.setText(''); f.render(); f.ready(0); f.ready(1);
  assert.equal(f.editors[0].content, ''); assert.equal(f.editors[1].content, '');
});
test('disable/enable and RAP send retain current content', () => {
  const f = fixture(); f.render(); f.ready(0); f.ready(1);
  f.editors[0].content = '<p>typed</p>'; f.editors[0].dirty = true;
  f.widget.setEnable(false); assert.equal(f.editors[1].content, '<p>typed</p>');
  f.widget.onSend(); assert.equal(f.calls[0][1], '<p>typed</p>');
  f.widget.setEnable(true); assert.equal(f.editors[0].mode.get(), 'design');
});
test('disposal removes both editors, DOM nodes and listeners', () => {
  const f = fixture(); f.render(); f.ready(0); f.ready(1); f.widget.destroy(); f.widget.destroy();
  assert.ok(f.editors.every(e => e.removed === 1)); assert.equal(f.parent.children.length, 0);
  assert.equal(f.parent.events.size, 0); assert.equal(f.listeners.size, 0);
});
test('disposal before render unregisters callbacks', () => {
  const f = fixture(); f.widget.destroy(); assert.equal(f.listeners.size, 0); assert.equal(f.parent.events.size, 0);
});
test('late init after disposal cannot revive an editor', () => {
  const f = fixture(); f.render(); f.widget.destroy(); f.ready(0); f.ready(1);
  assert.equal(f.parent.children.length, 0); assert.equal(f.listeners.size, 0); assert.equal(f.widget.ready, false);
});
test('inline table policy and explicit false custom settings are preserved', () => {
  const f = fixture({ inline: true, supportTable: true, configObj: { visualblocks_default_state: false } }); f.render();
  assert.ok(f.editors[0].options.plugins.join(' ').split(/\s+/).includes('table'));
  assert.equal(f.editors[0].options.visualblocks_default_state, false);
});
test('focus requested before initialization is applied once ready', () => {
  const f = fixture({ removeToolbar: false }); f.widget.setFocus(true); f.render(); f.ready(0);
  assert.equal(f.editors[0].focused, true);
});

test('modern self-hosted assets and GPL selection stay within the RAP base URL', () => {
  const f = fixture({ basePath: '/nested/rap/rwt-resources/tinymceeditor/' }); f.render();
  const o = f.editors[0].options;
  assert.equal(o.license_key, 'gpl'); assert.equal(o.base_url, '/nested/rap/rwt-resources/tinymceeditor');
  assert.equal(o.skin_url, o.base_url + '/skins/ui/oxide');
  assert.equal(o.icons_url, o.base_url + '/icons/default/icons.min.js');
  assert.equal(o.model, 'dom'); assert.equal(o.theme, 'silver'); assert.equal(o.icons, 'default');
  assert.ok(!o.plugins.includes('paste')); assert.ok(!o.plugins.includes('fullscreen'));
  assert.equal(o.valid_elements, undefined); assert.equal(o.xss_sanitization, undefined);
});

test('legacy font size configuration is translated and modern spelling takes precedence', () => {
  const old = fixture({ configObj: { fontsize_formats: '10px 20px' } }); old.render();
  assert.equal(old.editors[0].options.font_size_formats, '10px 20px');
  const modern = fixture({ configObj: { fontsize_formats: '10px', font_size_formats: '12px' } }); modern.render();
  assert.equal(modern.editors[0].options.font_size_formats, '12px');
});

test('RAP popup layering is installed once across multiple editor instances', () => {
  const f = fixture(); f.createWidget();
  assert.equal(f.head.children.length, 1);
  assert.equal(f.head.children[0].id, 'ej-tinymce-rap-overlay-style');
  assert.match(f.head.children[0].textContent, /\.tox\.tox-tinymce-inline\s*\{ z-index: 1000000;/);
  assert.match(f.head.children[0].textContent, /\.tox\.tox-tinymce-aux\s*\{ z-index: 1000001;/);
  assert.equal(f.widget.elementReadonly.style.zIndex, '2147483647');
  assert.equal(f.widget.element.style.zIndex, undefined, 'do not raise the entire RAP editor');
});
