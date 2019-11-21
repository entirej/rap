var TINYMCEEDITOR_BASEPATH = "rwt-resources/tinymceeditor/";

(function() {
	'use strict';

	rap.registerTypeHandler("eclipsesource.TinymceEditor", {

		factory : function(properties) {
			return new eclipsesource.TinymceEditor(properties);
		},

		destructor : "destroy",

		properties : [ "text", "font", "enable" ]

	});

	
	if (!window.eclipsesource) {
		window.eclipsesource = {};
	}

	eclipsesource.TinymceEditor = function(properties) {
		bindAll(this, [ "layout", "onReady", "onSend", "onRender" ,"setEditorSetup"]);
		this.parent = rap.getObject(properties.parent);
		this.inline = properties.inline;
		this.profile = properties.profile;
		this.contentCss = properties.contentCss;
		this.configObj = properties.configObj;
		this.removeToolbar = properties.removeToolbar;
		this.readonly = false;
		this.element = document.createElement("div");

		this.element.style.height = '100%';
		this.element.style.width = '100%';
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
			this.elementReadonly.style["z-index"] = "2147483647";// visible
			

			this.parent.append(this.elementReadonly);
			
		}
		

		
		 
		
		this.parent.append(this.element);
		this.parent.addListener("Resize", this.layout);
		rap.on("render", this.onRender);
	};

	eclipsesource.TinymceEditor.prototype = {

		ready : false,

		setEditorSetup : function( e) {
			this.editor = e;
			e.on('init', this.onReady);
			
			
			
		},
		onReady : function(ed) {
			// TODO [tb] : on IE 7/8 the iframe and body has to be made
			// transparent explicitly
			this.ready = true;
			var area = this.parent.getClientArea();
			
			try
			{
				this.editor.theme.resizeTo (area[2] - 2, area[3] - 105);
			}catch(e)
			{
				//ignore
			}
			
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
				
				
				
				var editorElm = this.element.cloneNode(true);
				this.element.appendChild(editorElm);

				

				if (this.profile == 'Basic') {
					
				} else if (this.profile == 'Standard') {
					
				} else {
					
				}

				
				//this.element.style.visibility = "hidden";
				if (this.inline) {
					
					tinymce.init({
				          target: editorElm,
				          menubar: true,
				          inline: true,
				          branding: false,
				          removed_menuitems: 'newdocument',
						  plugins: [
						    'advlist autolink lists  image  print preview  textcolor',
						    'searchreplace visualblocks ',
						    'insertdatetime table contextmenu paste wordcount'
						  ],
						  content_style: this.contentCss,
						  fontsize_formats: this.configObj!=null && this.configObj.fontsize_formats ? this.configObj.fontsize_formats :"8px 10px 11px 12px 13px 14px 18px 20px 22px 24px 36px",
						  toolbar: 'insert | undo redo |  formatselect | sizeselect fontselect fontsizeselect bold italic backcolor forecolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | removeformat',
						  
						  
						  style_formats: this.configObj!=null && this.configObj.style_formats ? this.configObj.style_formats :undefined,
						  visualblocks_default_state: this.configObj!=null && this.configObj.visualblocks_default_state ? this.configObj.visualblocks_default_state :undefined,
						  end_container_on_empty_block: this.configObj!=null && this.configObj.end_container_on_empty_block ? this.configObj.end_container_on_empty_block :undefined,
						  formats: this.configObj!=null && this.configObj.formats ? this.configObj.formats :undefined,
										  
						  setup: this.setEditorSetup});
				} else {
					
					tinymce.init({
				          target: editorElm,
				          menubar: true,
				          branding: false,
				          resize: false,
				          removed_menuitems: 'newdocument',
						  plugins: [
						    'advlist autolink lists  image  print preview  textcolor',
						    'searchreplace visualblocks ',
						    'insertdatetime table contextmenu paste wordcount'
						  ],
						  fontsize_formats: this.configObj!=null && this.configObj.fontsize_formats ? this.configObj.fontsize_formats :"8px 10px 11px 12px 13px 14px 18px 20px 22px 24px 36px",
						  toolbar: 'insert | undo redo |  formatselect | sizeselect fontselect fontsizeselect bold italic backcolor forecolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | removeformat',
						  content_style: this.contentCss,
						  style_formats: this.configObj!=null && this.configObj.style_formats ? this.configObj.style_formats :undefined,
						  visualblocks_default_state: this.configObj!=null && this.configObj.visualblocks_default_state ? this.configObj.visualblocks_default_state :undefined,
						  end_container_on_empty_block: this.configObj!=null && this.configObj.end_container_on_empty_block ? this.configObj.end_container_on_empty_block :undefined,
						  formats: this.configObj!=null && this.configObj.formats ? this.configObj.formats :undefined,
								  
						  setup: this.setEditorSetup});
					
				}

				
				
				
				
				
				rap.on("send", this.onSend);
			}
		},

		onSend : function() {
			if (this.editor && this.editor.isDirty()) {
				rap.getRemoteObject(this).set("text", this.editor.getContent());
				try
				{
					this.editor.setDirty(false);
				}catch(e)
				{
					//ignore
				}
				
			}
		},

		setText : function(text) {
			if (this.ready) {
				try
				{
				    this.editor.setContent(text);
					if(this.removeToolbar && this.readonly  )
					{
						this.copyHtmlToReadOnly();
					}
				}catch(e)
				{
					//ignore
				}
				
				
			} else {
				this._text = text;
			}
		},

		setFont : function(font) {
			if (this.ready) {
				async(this, function() { // Needed by IE for some reason
					//this.element.style.font = font;
					
				});
			} else {
				this._font = font;
			}
		},

		setEnable : function(enable) {
			if (this.ready) {
				try
				{
					
					this.readonly = (!enable);
					if(this.removeToolbar)
					{
						if (enable) {
							this.elementReadonly.style.visibility = "hidden";
							this.element.style.visibility = "visible";
							this.elementReadonly.innerHTML = '';
						} else {
		
							
							
							this.elementReadonly.innerHTML = '';
							var readOnly =document.createElement("div");
							this.elementReadonly.appendChild(readOnly);
							readOnly.innerHTML = "<style type=\"text/css\">"+this.contentCss+"</style>"+ this.editor.getContent();
							this.element.style.visibility = "hidden";
							this.elementReadonly.style.visibility = "visible";
		
						}	
					}
					else
					{
						this.element.style.visibility = "visible";
					}

					this.editor.setMode(this.readonly ? 'readonly' : 'design');
				}catch(e)
				{
					//ignore
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
				try
				{
					this.editor.theme.resizeTo (area[2] - 2, area[3] - 105);
					
				}catch(e)
				{
					//ignore
				}
				
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
