var CKEDITOR_BASEPATH = "rwt-resources/ckeditor/";

(function() {
	'use strict';

	rap.registerTypeHandler("eclipsesource.CKEditor", {

		factory : function(properties) {
			return new eclipsesource.CKEditor(properties);
		},

		destructor : "destroy",

		properties : [ "text", "font", "enable" ]

	});

	if (!window.eclipsesource) {
		window.eclipsesource = {};
	}

	eclipsesource.CKEditor = function(properties) {
		bindAll(this, [ "layout", "onReady", "onSend", "onRender" ]);
		this.parent = rap.getObject(properties.parent);
		this.inline = properties.inline;
		this.profile = properties.profile;
		this.removeToolbar = properties.removeToolbar;
		this.element = document.createElement("div");

		this.element.style.height = '100%';
		this.element.style.overflow = 'auto';
		this.element.style.position = 'absolute';
		this.element.style.bottom = '0px';
		this.element.style.top = '0px';
		this.element.style.left = '0px';
		this.element.style.right = '0px';
		// .style.visibility = "hidden"
		
		if(this.removeToolbar)
		{
			this.elementReadonly = document.createElement("div");

			this.elementReadonly.style.height = '100%';
			this.elementReadonly.style.overflow = 'auto';
			this.elementReadonly.style.position = 'absolute';
			this.elementReadonly.style.bottom = '0px';
			this.elementReadonly.style.top = '0px';
			this.elementReadonly.style.left = '0px';
			this.elementReadonly.style.right = '0px';
			this.elementReadonly.style.visibility = "hidden";// visible

			this.parent.append(this.elementReadonly);
			
		}
		

		
		 
		if (this.inline)
			this.element.setAttribute('contenteditable', true)
		this.parent.append(this.element);
		this.parent.addListener("Resize", this.layout);
		rap.on("render", this.onRender);
	};

	eclipsesource.CKEditor.prototype = {

		ready : false,

		onReady : function() {
			// TODO [tb] : on IE 7/8 the iframe and body has to be made
			// transparent explicitly
			this.ready = true;
			this.layout();
			if (this._text) {
				this.setText(this._text);
				delete this._text;
			}
			if (this._font) {
				this.setFont(this._font);
				delete this._font;
			}
			if (this._enable != undefined) {
				this.setEnable(this._enable);
				delete this._enable;
			}
		},

		onRender : function() {

			if (this.element.parentNode) {
				rap.off("render", this.onRender);
				var toolbarGroupsProfile = [];
				var removeButtonsProfile = '';

				if (this.profile == 'Basic') {
					toolbarGroupsProfile = [
							{
								name : 'document',
								groups : [ 'mode', 'document', 'doctools' ]
							},
							{
								name : 'clipboard',
								groups : [ 'clipboard', 'undo' ]
							},
							{
								name : 'editing',
								groups : [ 'find', 'selection', 'spellchecker',
										'editing' ]
							},
							{
								name : 'forms',
								groups : [ 'forms' ]
							},
							{
								name : 'basicstyles',
								groups : [ 'basicstyles', 'cleanup' ]
							},
							{
								name : 'paragraph',
								groups : [ 'list', 'indent', 'blocks', 'align',
										'bidi', 'paragraph' ]
							}, {
								name : 'links',
								groups : [ 'links' ]
							}, {
								name : 'insert',
								groups : [ 'insert' ]
							}, {
								name : 'styles',
								groups : [ 'styles' ]
							}, {
								name : 'colors',
								groups : [ 'colors' ]
							}, {
								name : 'tools',
								groups : [ 'tools' ]
							}, {
								name : 'others',
								groups : [ 'others' ]
							}, {
								name : 'about',
								groups : [ 'about' ]
							} ];

					removeButtonsProfile = 'Source,Save,NewPage,Scayt,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,BidiLtr,BidiRtl,Language,CreateDiv,Anchor,Unlink,Link,Flash,Smiley,Iframe,About,Preview,Print,Templates,Find,Subscript,Superscript,RemoveFormat,Image,Table,HorizontalRule,SpecialChar,PageBreak,JustifyLeft,JustifyCenter,JustifyRight,JustifyBlock,Outdent,Indent,Replace,Maximize,ShowBlocks,PasteFromWord,PasteText,Cut,Copy,Paste,Redo,Undo,Blockquote';

				} else if (this.profile == 'Standard') {
					toolbarGroupsProfile = [
							{
								name : 'document',
								groups : [ 'mode', 'document', 'doctools' ]
							},
							{
								name : 'clipboard',
								groups : [ 'clipboard', 'undo' ]
							},
							{
								name : 'editing',
								groups : [ 'find', 'selection', 'spellchecker',
										'editing' ]
							},
							{
								name : 'forms',
								groups : [ 'forms' ]
							},
							{
								name : 'basicstyles',
								groups : [ 'basicstyles', 'cleanup' ]
							},
							{
								name : 'paragraph',
								groups : [ 'list', 'indent', 'blocks', 'align',
										'bidi', 'paragraph' ]
							}, {
								name : 'links',
								groups : [ 'links' ]
							}, {
								name : 'insert',
								groups : [ 'insert' ]
							}, {
								name : 'styles',
								groups : [ 'styles' ]
							}, {
								name : 'colors',
								groups : [ 'colors' ]
							}, {
								name : 'tools',
								groups : [ 'tools' ]
							}, {
								name : 'others',
								groups : [ 'others' ]
							}, {
								name : 'about',
								groups : [ 'about' ]
							} ];

					removeButtonsProfile = 'Source,Save,NewPage,Scayt,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,BidiLtr,BidiRtl,Language,CreateDiv,Anchor,Unlink,Link,Flash,Smiley,Iframe,About,Preview,Print,RemoveFormat,Image,HorizontalRule,PageBreak,Maximize,ShowBlocks,Redo';

				} else {
					toolbarGroupsProfile = [
							{
								name : 'document',
								groups : [ 'mode', 'document', 'doctools' ]
							},
							{
								name : 'clipboard',
								groups : [ 'clipboard', 'undo' ]
							},
							{
								name : 'editing',
								groups : [ 'find', 'selection', 'spellchecker',
										'editing' ]
							},
							{
								name : 'forms',
								groups : [ 'forms' ]
							},
							{
								name : 'basicstyles',
								groups : [ 'basicstyles', 'cleanup' ]
							},
							{
								name : 'paragraph',
								groups : [ 'list', 'indent', 'blocks', 'align',
										'bidi', 'paragraph' ]
							}, {
								name : 'links',
								groups : [ 'links' ]
							}, {
								name : 'insert',
								groups : [ 'insert' ]
							}, '/', {
								name : 'styles',
								groups : [ 'styles' ]
							}, {
								name : 'colors',
								groups : [ 'colors' ]
							}, {
								name : 'tools',
								groups : [ 'tools' ]
							}, {
								name : 'others',
								groups : [ 'others' ]
							}, {
								name : 'about',
								groups : [ 'about' ]
							} ];

					removeButtonsProfile = 'Source,Save,NewPage,Scayt,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,BidiLtr,BidiRtl,Language,CreateDiv,Anchor,Unlink,Link,Flash,Smiley,Iframe,About';

				}

				

				if (this.inline) {
					this.editor = CKEDITOR
							.inline(
									this.element,

									{
										toolbarGroups : toolbarGroupsProfile,
										removeButtons : removeButtonsProfile,
										baseFloatZIndex : 3000000,
										title : false,
										removePlugins : 'floating-tools,wsc,forms,image,chartabout,a11yhelp,bidi,dialogadvtab,div,elementspath,filebrowser,flash,iframe,language,newpage,save,scayt,smiley,resize,sourcearea'

									});
				} else {
					// config.floatingtools = 'Basic';
					// config.floatingtools_Basic =config.toolbarGroups;
					// config.toolbarGroups = []
					// // config.extraPlugins= 'floating-tools'
					this.editor = CKEDITOR
							.appendTo(
									this.element,

									{
										toolbarGroups : toolbarGroupsProfile,
										removeButtons : removeButtonsProfile,
										baseFloatZIndex : 3000000,
										title : false,
										removePlugins : 'wsc,forms,image,chart,floating-tools,about,a11yhelp,bidi,dialogadvtab,div,elementspath,filebrowser,flash,iframe,language,newpage,save,scayt,smiley,resize,sourcearea'

									});
				}

				
				
				
				
				this.editor.on("instanceReady", this.onReady);
				this.editor.on('instanceReady', function(e) {
					$(e.editor.element.$).removeAttr("title");
				});
				rap.on("send", this.onSend);
			}
		},

		onSend : function() {
			if (this.editor.checkDirty()) {
				rap.getRemoteObject(this).set("text", this.editor.getData());
				
				this.editor.resetDirty();
			}
		},

		setText : function(text) {
			if (this.ready) {
				this.editor.setData(text);
				if(this.removeToolbar && this.editor.isReadOnly && this.editor.isReadOnly())
				{
					this.elementReadonly.innerHTML = (this.editor.document.getBody().getHtml());
				}
				
			} else {
				this._text = text;
			}
		},

		setFont : function(font) {
			if (this.ready) {
				async(this, function() { // Needed by IE for some reason
					this.editor.document.getBody().setStyle("font", font);
					this.Readonly.document.getBody().setStyle("font", font);
				});
			} else {
				this._font = font;
			}
		},

		setEnable : function(enable) {
			if (this.ready) {
				this.editor.setReadOnly(!enable);
				if(this.removeToolbar)
				{
					if (enable) {
						this.elementReadonly.style.visibility = "hidden";
						this.element.style.visibility = "visible";
						this.elementReadonly.innerHTML = '';
					} else {
	
						this.elementReadonly.innerHTML = (this.editor.document.getBody().getHtml());
						this.element.style.visibility = "hidden";
						this.elementReadonly.style.visibility = "visible";
	
					}	
				}
				

			} else {
				this._enable = enable;
			}
		},

		destroy : function() {
			if (this.element.parentNode) {
				rap.off("send", this.onSend);
				try {
					this.editor.destroy();
				} catch (e) {
				}
				
				this.element.parentNode.removeChild(this.element);
				if(this.removeToolbar && this.element.parentNode)
					this.element.parentNode.removeChild(this.elementReadonly);
			}
		},

		layout : function() {
			if (this.ready) {
				var area = this.parent.getClientArea();
				this.element.style.left = area[0] + "px";
				this.element.style.top = area[1] + "px";
				if(this.removeToolbar)
				{
					this.elementReadonly.style.left = area[0] + "px";
					this.elementReadonly.style.top = area[1] + "px";
				}
				
				this.editor.resize(area[2], area[3]);
			}
		}

	};

	var bind = function(context, method) {
		return function() {
			return method.apply(context, arguments);
		};
	};

	var bindAll = function(context, methodNames) {
		for (var i = 0; i < methodNames.length; i++) {
			var method = context[methodNames[i]];
			context[methodNames[i]] = bind(context, method);
		}
	};

	var async = function(context, func) {
		window.setTimeout(function() {
			func.apply(context);
		}, 0);
	};

}());
