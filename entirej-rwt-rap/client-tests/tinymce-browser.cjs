// Local integration fixture: real bundled TinyMCE + RAP protocol/lifecycle stub.
// Requires selenium-webdriver on NODE_PATH and compatible Chrome/ChromeDriver.
const assert = require('node:assert/strict');
const fs = require('node:fs');
const http = require('node:http');
const path = require('node:path');
const { Builder, By, logging } = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const root = path.resolve(__dirname, '..');
const resourceRoot = process.env.TINYMCE_TEST_RESOURCES || path.join(root, 'src/resources/tinymce');
const java = fs.readFileSync(path.join(root, 'src/org/entirej/applicationframework/rwt/component/EJRWTTinymceEditor.java'), 'utf8');
const assets = new Set();
for (const match of java.matchAll(/RESOURCE_FILES(?:_SUB)?\s*=\s*\{([\s\S]*?)\};/g)) {
  for (const name of match[1].matchAll(/"([^\"]+)"/g)) assets.add(name[1]);
}
assert.ok(assets.has('skins/ui/oxide/skin.min.css'));
const prefix = '/fixture/nested/rap/rwt-resources/tinymceeditor/';
const requests = [];
const types = { '.js': 'application/javascript', '.css': 'text/css', '.woff': 'font/woff',
  '.ttf': 'font/ttf', '.eot': 'application/vnd.ms-fontobject', '.svg': 'image/svg+xml', '.gif': 'image/gif' };
const page = `<!doctype html><html><head><meta charset="utf-8"><title>TinyMCE RAP fixture</title></head><body>
<div id="rap-modal-blocker" style="position:fixed;inset:0;z-index:100009;background:rgba(0,0,0,.03)"></div>
<script>
window.pageErrors=[];addEventListener('error',e=>pageErrors.push(e.message));
window.listeners={render:new Set(),send:new Set()};window.parents={};window.remoteCalls=[];
window.rap={registerTypeHandler(){},getObject(id){return parents[id]},getRemoteObject(){return {
set(name,value){remoteCalls.push({name,value})},call(name,args){remoteCalls.push({name,args})}}},
on(name,fn){listeners[name].add(fn)},off(name,fn){listeners[name].delete(fn)}};
window.emit=name=>Array.from(listeners[name]).forEach(fn=>fn());
window.createWidget=function(options){
 const id='parent-'+Object.keys(parents).length;const node=document.createElement('div');
 node.style.cssText='position:relative;z-index:100010;width:520px;height:360px;margin:8px;border:1px solid #ccc';document.body.appendChild(node);
 parents[id]={node,events:new Set(),append(el){node.appendChild(el)},addListener(n,fn){this.events.add(fn)},
 removeListener(n,fn){this.events.delete(fn)},getClientArea(){return [0,0,node.clientWidth,node.clientHeight]}};
 return new eclipsesource.TinymceEditor(Object.assign({parent:id,inline:false,removeToolbar:true,supportTable:true,
 contentCss:'body { font-family: Arial; }',configObj:{},basePath:'${prefix}'},options));
};
</script><script src="${prefix}tinymce.min.js"></script><script src="${prefix}tinymcehandler.js"></script></body></html>`;
const server = http.createServer((req, res) => {
  const url = new URL(req.url, 'http://localhost');
  if (url.pathname.endsWith('/editor.html')) { res.writeHead(200, { 'Content-Type': 'text/html' }); res.end(page); return; }
  if (url.pathname === '/favicon.ico') { res.writeHead(204); res.end(); return; }
  const relative = url.pathname.startsWith(prefix) ? url.pathname.slice(prefix.length) : '';
  const file = path.join(resourceRoot, relative);
  const found = assets.has(relative) && fs.existsSync(file);
  const type = found ? types[path.extname(relative)] || 'application/octet-stream' : 'text/html';
  requests.push({ path: url.pathname, status: found ? 200 : 404, type });
  res.writeHead(found ? 200 : 404, { 'Content-Type': type });
  res.end(found ? fs.readFileSync(file) : '<html>Resource not registered</html>');
});

(async () => {
  let driver;
  try {
    await new Promise(resolve => server.listen(0, '127.0.0.1', resolve));
    const options = new chrome.Options().setChromeBinaryPath(process.env.TINYMCE_TEST_CHROME || 'C:/tools/chromium-runtime-win/chrome.exe')
      .addArguments('--headless=new', '--disable-gpu', '--no-sandbox', '--disable-background-networking', '--window-size=1100,900');
    const logs = new logging.Preferences(); logs.setLevel(logging.Type.BROWSER, logging.Level.ALL);
    options.setLoggingPrefs(logs);
    const service = new chrome.ServiceBuilder(process.env.TINYMCE_TEST_CHROMEDRIVER || 'C:/tools/chromium-runtime-win/chromedriver.exe');
    driver = await new Builder().forBrowser('chrome').setChromeOptions(options).setChromeService(service).build();
    const js = (script, ...args) => driver.executeScript(script, ...args);
    const assertFrontmost = async (selector) => {
      const result = await js(`const nodes=Array.from(document.querySelectorAll(arguments[0]));
        const node=nodes.find(n=>{const r=n.getBoundingClientRect();return r.width>0&&r.height>0&&getComputedStyle(n).visibility!=='hidden'});
        if(!node)return {visible:false};
        const r=node.getBoundingClientRect(),top=document.elementFromPoint(r.x+r.width/2,r.y+r.height/2);
        return {visible:true,frontmost:!!top&&(node===top||node.contains(top)),top:top&&top.className};`, selector);
      assert.ok(result.visible && result.frontmost, selector + ': ' + JSON.stringify(result));
    };
    await driver.get(`http://127.0.0.1:${server.address().port}/fixture/nested/rap/editor.html`);
    await js("window.first=createWidget({}); first.setText('<p>First &amp; \u00fc</p>'); first.setEnable(false); emit('render');");
    await driver.wait(() => js('return first.ready && first.viewReady'), 20000);
    assert.ok((await js('return first.editor.getBody().textContent')).includes('First & \u00fc'));
    assert.ok((await js('return first.editorView.getBody().textContent')).includes('First & \u00fc'));
    assert.equal(await js('return first.elementReadonly.style.visibility'), 'visible');
    fs.writeFileSync(path.join(root, 'target/tinymce-readonly-browser.png'), Buffer.from(await driver.takeScreenshot(), 'base64'));
    await js("first.setText('');"); assert.equal(await js('return first.editorView.getContent()'), '');
    await js("first.setEnable(true); first.setText('<p>Updated</p>'); first.editor.insertContent('<p>typed</p>'); first.editor.setDirty(true); emit('send');");
    assert.ok((await js("return remoteCalls.filter(c=>c.name==='text').pop().value")).includes('typed'));
    await driver.findElement(By.css('.tox-menubar button[role="menuitem"]')).click();
    await driver.wait(() => js('return !!document.querySelector(".tox-menu")'), 5000);
    await assertFrontmost('.tox-menu');
    fs.writeFileSync(path.join(root, 'target/tinymce-rap-menu-browser.png'), Buffer.from(await driver.takeScreenshot(), 'base64'));
    await driver.findElement(By.css('.tox-menubar button[role="menuitem"]')).click();
    await js("first.editor.execCommand('mceLink');");
    await driver.wait(() => js('return !!document.querySelector(".tox-dialog")'), 5000);
    await assertFrontmost('.tox-dialog');
    fs.writeFileSync(path.join(root, 'target/tinymce-rap-dialog-browser.png'), Buffer.from(await driver.takeScreenshot(), 'base64'));
    await js('first.editor.windowManager.close();');
    await js("first.editor.execCommand('mceFullScreen'); first.parent.node.style.height='280px'; first.layout();");
    assert.ok(await js("return remoteCalls.some(c=>c.name==='fullScreen')"));
    await js("window.second=createWidget({inline:true,removeToolbar:false,supportTable:true}); second.setText('<p>Inline</p>'); emit('render');");
    await driver.wait(() => js('return second.ready'), 15000);
    await js('second.editor.focus();');
    await driver.wait(() => js('return second.editor.getContainer().getBoundingClientRect().height>0'), 5000);
    await assertFrontmost('.tox-tinymce-inline');
    await (await js('return second.editor.getContainer().querySelector(".tox-menubar button[role=menuitem]")')).click();
    await driver.wait(() => js('return !!document.querySelector(".tox-menu")'), 5000);
    await assertFrontmost('.tox-menu');
    await (await js('return second.editor.getContainer().querySelector(".tox-menubar button[role=menuitem]")')).click();
    assert.ok(await js("return !!second.editor.plugins.table"));
    assert.ok((await js('return first.editor.getContent()')).includes('typed'));
    await js(`second.setText('<p><strong>Bold</strong> <span style="font-size: 20px; color: #ff0000;">Colour</span></p><table><tbody><tr><td>Cell</td></tr></tbody></table>');`);
    assert.ok(await js("return !!second.editor.getBody().querySelector('strong') && !!second.editor.getBody().querySelector('td')"));
    assert.equal(await js("return second.editor.getBody().querySelector('span').style.fontSize"), '20px');
    await js(`second.setText('<p onclick="alert(1)">Safe</p><script>window.badScript=true;<\\/script>');`);
    assert.ok(await js("return !second.editor.getBody().querySelector('script,[onclick]') && !window.badScript"));
    // Check real keyboard editing on the enabled iframe, not just programmatic content updates.
    await driver.switchTo().frame(await js('return first.editor.iframeElement'));
    await driver.findElement(By.css('body')).sendKeys('KEYBOARD');
    await driver.switchTo().defaultContent();
    assert.ok((await js('return first.editor.getContent()')).includes('KEYBOARD'));
    assert.equal(await js('return tinymce.majorVersion+"."+tinymce.minorVersion'), '8.10.2');
    assert.ok(await js("return performance.getEntriesByType('resource').every(r=>r.name.startsWith(location.origin)||r.name.startsWith('data:'))"));
    fs.writeFileSync(path.join(root, 'target/tinymce-browser.png'), Buffer.from(await driver.takeScreenshot(), 'base64'));
    await js('first.destroy(); second.destroy();');
    await driver.wait(() => js('return tinymce.get().length===0'), 10000);
    assert.equal(await js('return listeners.send.size+listeners.render.size'), 0);
    assert.equal(await js('return Object.values(parents).reduce((n,p)=>n+p.events.size,0)'), 0);
    await js("window.noView=createWidget({removeToolbar:false,supportTable:false}); noView.setEnable(false); noView.setText('<p>Disabled</p>'); emit('render');");
    await driver.wait(() => js('return noView.ready'), 10000);
    assert.equal(await js('return noView.editor.mode.get()'), 'readonly');
    // Modern readonly permits selection/copy without permitting user edits.
    await driver.switchTo().frame(await js('return noView.editor.iframeElement'));
    await driver.findElement(By.css('body')).sendKeys('BLOCKED');
    await driver.switchTo().defaultContent();
    assert.equal(await js('return noView.editor.getBody().textContent'), 'Disabled');
    await js("noView.setEnable(true); noView.setText('<p>Enabled</p>'); noView.destroy();");
    await js("window.early=createWidget({}); early.setText('never shown'); emit('render'); early.destroy();");
    await driver.wait(() => js('return tinymce.get().length===0'), 10000);
    // Wait a resource/event turn, not an arbitrary multi-second initialization sleep.
    await driver.executeAsyncScript('requestAnimationFrame(()=>requestAnimationFrame(arguments[arguments.length-1]));');
    assert.equal(await js('return tinymce.get().length'), 0);
    assert.deepEqual(await js('return pageErrors'), []);
    const consoleLog = await driver.manage().logs().get(logging.Type.BROWSER);
    const severe = consoleLog.filter(x => x.level.name === 'SEVERE');
    assert.deepEqual(severe, [], JSON.stringify(severe));
    assert.equal(requests.filter(r => r.status !== 200).length, 0, JSON.stringify(requests));
    assert.equal(requests.filter(r => r.path.includes('borderless')).length, 0);
    assert.ok(requests.some(r => r.path.endsWith('/skin.min.css') && r.type === 'text/css'));
    const result = { browser: (await driver.getCapabilities()).get('browserVersion'),
      resourceRoot,
      tinyMce: await js('return tinymce.majorVersion+"."+tinymce.minorVersion'),
      checks: ['first set', 'clear', 'readonly toggle', 'RAP send', 'fullscreen callback', 'resize',
        'inline table support', 'two instances', 'dispose', 'dispose during init', 'disabled mode blocks typing', 'registered asset URLs/MIME',
        'formatted HTML/table roundtrip', 'unsafe HTML sanitization', 'enabled keyboard editing', 'self-hosted resources only',
        'menu above RAP modal layer', 'dialog above RAP modal layer', 'inline toolbar above RAP modal layer', 'inline menu above RAP modal layer'],
      pageErrors: [], unexpectedSevereConsoleErrors: [], consoleWarnings: consoleLog.filter(x=>x.level.name==='WARNING').map(x=>x.message), requests };
    fs.writeFileSync(path.join(root, 'target/tinymce-browser-report.json'), JSON.stringify(result, null, 2));
    console.log(JSON.stringify(result, null, 2));
  } finally {
    if (driver) await driver.quit();
    server.close();
  }
})().catch(error => { console.error(error); process.exitCode = 1; });
