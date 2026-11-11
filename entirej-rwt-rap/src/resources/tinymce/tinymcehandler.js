var TINYMCEEDITOR_BASEPATH = "rwt-resources/tinymceeditor/";

(function() {
    'use strict';
    rap.registerTypeHandler("eclipsesource.TinymceEditor", {
        factory: function(properties) { return new eclipsesource.TinymceEditor(properties); },
        destructor: "destroy", properties: ["text", "font", "enable", "focus"]
    });
    if (!window.eclipsesource) { window.eclipsesource = {}; }
    eclipsesource.TinymceEditor = function(properties) {
        ensureOverlayStyle();
        bindAll(this, ["layout", "onSend", "onRender", "setEditorSetup", "setViewEditorSetup",
            "onFullscreenStateChanged", "onPrePaste"]);
        this.parent = rap.getObject(properties.parent);
        this.inline = properties.inline;
        this.profile = properties.profile;
        this.contentCss = properties.contentCss;
        this.configObj = properties.configObj;
        this.removeToolbar = properties.removeToolbar;
        this.supportTable = properties.supportTable;
        this.pasteAsText = properties.pasteAsText;
        this.basePath = properties.basePath || TINYMCEEDITOR_BASEPATH;
        this.ready = false;
        this.viewReady = false;
        this.destroyed = false;
        this.readonly = false;
        this._text = "";
        this._focus = false;
        this.element = createElement();
        this.parent.append(this.element);
        if (this.removeToolbar) {
            this.elementReadonly = createElement();
            this.elementReadonly.style.visibility = "hidden";
            // Preserve the legacy readonly layer inside its RAP parent stacking context.
            this.elementReadonly.style.zIndex = '2147483647';
            this.parent.append(this.elementReadonly);
        }
        this.parent.addListener("Resize", this.layout);
        rap.on("render", this.onRender);
    };
    eclipsesource.TinymceEditor.prototype = {
        setEditorSetup: function(editor) { this.setupEditor(editor, false); },
        setViewEditorSetup: function(editor) { this.setupEditor(editor, true); },
        setupEditor: function(editor, view) {
            var self = this;
            if (view) { this.editorView = editor; } else { this.editor = editor; }
            editor.on('init', function() {
                if (self.destroyed) {
                    // A pending initialization must not revive a disposed RAP widget.
                    editor._ejRemoved = false;
                    self.removeEditor(editor);
                    return;
                }
                if (view) {
                    self.viewReady = true;
                    editor.mode.set('readonly');
                    var container = editor.getContainer();
                    container.style.border = '0';
                    container.style.boxShadow = 'none';
                } else {
                    self.ready = true;
                    editor.addCommand('mceFullScreen', self.onFullscreenStateChanged);
                }
                self.applyText(editor);
                self.applyEnabled();
                self.layout();
                if (!view && self._focus && !self.readonly) { editor.focus(); }
            });
            if (!view) {
                // Preserve the RAP fullscreen-dialog contract instead of entering DOM/browser fullscreen.
                editor.ui.registry.addButton('ejfullscreen', {
                    icon: 'fullscreen', tooltip: 'Fullscreen', onAction: this.onFullscreenStateChanged
                });
            }
            if (this.destroyed) { this.removeEditor(editor); }
        },
        onFullscreenStateChanged: function() {
            if (!this.destroyed) { rap.getRemoteObject(this).call('fullScreen', {}); }
        },
        onPrePaste: function(plugin, args) {
            if (!this.supportTable && args.content != null) {
                args.content = args.content.replace(/<\/?table[^>]*>/gi, '')
                    .replace(/<\/?tbody[^>]*>/gi, '').replace(/<\/?tr[^>]*>/gi, '<br>')
                    .replace(/<\/?td[^>]*>/gi, ' ');
            }
        },
        editorOptions: function(target, view) {
            var plugins = ['advlist', 'autolink', 'link', 'lists', 'preview', 'searchreplace',
                'visualblocks', 'insertdatetime', 'wordcount'];
            if (this.supportTable) { plugins.push('table'); }
            var base = this.basePath.replace(/\/$/, '');
            var options = {
                target: target, menubar: !view, inline: view ? false : !!this.inline,
                license_key: 'gpl', base_url: base, suffix: '.min',
                model: 'dom', theme: 'silver', icons: 'default', icons_url: base + '/icons/default/icons.min.js',
                branding: false, promotion: false, resize: false, skin: 'oxide',
                skin_url: base + '/skins/ui/oxide', content_css: base + '/skins/content/default/content.min.css',
                min_height: 0, min_width: 0, height: Math.max(1, this.parent.getClientArea()[3]),
                paste_as_text: this.pasteAsText,
                paste_preprocess: this.onPrePaste, removed_menuitems: 'newdocument',
                plugins: view ? [] : plugins, content_style: this.contentCss,
                font_size_formats: "8px 10px 11px 12px 13px 14px 18px 20px 22px 24px 36px",
                toolbar: view ? false : 'undo redo | blocks | fontsize bold italic backcolor forecolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | removeformat | ejfullscreen',
                setup: view ? this.setViewEditorSetup : this.setEditorSetup
            };
            if (view) { options.statusbar = false; options.readonly = true; }
            var custom = this.configObj;
            ['style_formats', 'visualblocks_default_state', 'end_container_on_empty_block', 'formats'].forEach(function(key) {
                if (custom && Object.prototype.hasOwnProperty.call(custom, key)) { options[key] = custom[key]; }
            });
            // Existing application config files can keep the v4 spelling; the v8 spelling takes precedence.
            if (custom && Object.prototype.hasOwnProperty.call(custom, 'font_size_formats')) {
                options.font_size_formats = custom.font_size_formats;
            } else if (custom && Object.prototype.hasOwnProperty.call(custom, 'fontsize_formats')) {
                options.font_size_formats = custom.fontsize_formats;
            }
            return options;
        },
        onRender: function() {
            if (this.destroyed || !this.element.parentNode) { return; }
            rap.off("render", this.onRender);
            var target = document.createElement('div');
            this.element.appendChild(target);
            tinymce.init(this.editorOptions(target, false));
            if (this.removeToolbar) {
                var viewTarget = document.createElement('div');
                this.elementReadonly.appendChild(viewTarget);
                tinymce.init(this.editorOptions(viewTarget, true));
            }
            this.applyEnabled();
            rap.on("send", this.onSend);
        },
        applyText: function(editor) {
            editor.setContent(this._text);
            editor.setDirty(false);
        },
        onSend: function() {
            if (!this.destroyed && this.ready && this.editor.isDirty()) {
                this._text = this.editor.getContent();
                rap.getRemoteObject(this).set("text", this._text);
                this.editor.setDirty(false);
            }
        },
        setText: function(text) {
            if (this.destroyed) { return; }
            this._text = text == null ? '' : text;
            if (this.ready) { this.applyText(this.editor); }
            if (this.viewReady) { this.applyText(this.editorView); }
        },
        setFocus: function(focus) {
            this._focus = focus;
            if (!this.destroyed && focus && this.ready && !this.readonly) { this.editor.focus(); }
        },
        setFont: function(font) {
            // Preserve the protocol property; content CSS/config still controls formatting.
            this._font = font;
        },
        setEnable: function(enable) {
            if (this.destroyed) { return; }
            if (!enable && this.ready) { this._text = this.editor.getContent(); }
            this.readonly = !enable;
            if (this.viewReady) { this.applyText(this.editorView); }
            this.applyEnabled();
        },
        applyEnabled: function() {
            if (this.destroyed) { return; }
            if (this.ready) { this.editor.mode.set(this.readonly ? 'readonly' : 'design'); }
            this.element.style.visibility = this.removeToolbar && this.readonly ? 'hidden' : 'visible';
            if (this.elementReadonly) { this.elementReadonly.style.visibility = this.readonly ? 'visible' : 'hidden'; }
        },
        removeEditor: function(editor) {
            if (editor && !editor._ejRemoved) {
                editor._ejRemoved = true;
                editor.remove();
            }
        },
        destroy: function() {
            if (this.destroyed) { return; }
            this.destroyed = true;
            this.ready = false;
            this.viewReady = false;
            rap.off("render", this.onRender);
            rap.off("send", this.onSend);
            this.parent.removeListener("Resize", this.layout);
            this.removeEditor(this.editor);
            this.removeEditor(this.editorView);
            [this.element, this.elementReadonly].forEach(function(el) {
                if (el && el.parentNode) { el.parentNode.removeChild(el); }
            });
        },
        layout: function() {
            if (this.destroyed) { return; }
            var area = this.parent.getClientArea();
            [this.element, this.elementReadonly].forEach(function(el) {
                if (el) { el.style.left = area[0] + 'px'; el.style.top = area[1] + 'px'; }
            });
            if (this.viewReady) { this.resizeEditor(this.editorView, area[2], area[3]); }
            if (this.ready && !this.inline) {
                this.resizeEditor(this.editor, area[2], area[3]);
            }
        },
        resizeEditor: function(editor, width, height) {
            // TinyMCE 8 uses a flex layout; the container owns the entire editor height.
            var container = editor.getContainer();
            container.style.width = Math.max(0, width - 2) + 'px';
            container.style.height = Math.max(0, height - 2) + 'px';
            editor.dispatch('ResizeEditor');
        }
    };
    function ensureOverlayStyle() {
        var id = 'ej-tinymce-rap-overlay-style';
        if (document.getElementById(id)) { return; }
        var style = document.createElement('style');
        style.id = id;
        // RAP shells start at 100000. Preserve the old editor's 1000000 popup
        // layer without modifying vendor code or raising the whole RAP widget.
        // Dialog/menu sinks must also sit above a body-level inline toolbar.
        style.textContent = '.tox.tox-tinymce-inline { z-index: 1000000; }' +
            '.tox.tox-tinymce-aux { z-index: 1000001; }';
        document.head.appendChild(style);
    }
    function createElement() {
        var el = document.createElement('div');
        el.style.height = '100%'; el.style.width = '100%'; el.style.overflow = 'auto';
        el.style.position = 'absolute'; el.style.top = '0px'; el.style.left = '0px';
        return el;
    }
    function bindAll(context, names) {
        names.forEach(function(name) {
            var method = context[name];
            context[name] = function() { return method.apply(context, arguments); };
        });
    }
}());
