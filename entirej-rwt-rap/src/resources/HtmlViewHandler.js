var CKEDITOR_BASEPATH = "rwt-resources/ejhtmlview/";

(function(){
  'use strict';

  rap.registerTypeHandler( "entirej.HtmlView", {
    factory : function( properties ) {
      return new entirej.HtmlView( properties );
    },

    destructor : "destroy",

    properties : [ "text",'scroll' ]

  } );

  if( !window.entirej ) {
    window.entirej = {};
  }

  entirej.HtmlView = function( properties ) {
    bindAll( this, [ "layout", "onRender","ej_action","ej_select",'ej_scroll' ] );
    this.parent = rap.getObject( properties.parent );
    this.document = document;
    this.element = document.createElement( "div" );

    this.element.style.height = '100%';
    this.element.style.overflow = 'auto';
    this.element.style.position = 'absolute';
    this.element.style.bottom= '0px';
    this.element.style.top= '0px';
    this.element.style.left= '0px';
    this.element.style.right= '0px';
    
    this.parent.append( this.element );
    this.parent.addListener( "Resize", this.layout );
    rap.on( "render", this.onRender );
  };

  entirej.HtmlView.prototype = {

    

    onRender : function() {
      if( this.element.parentNode ) {
        rap.off( "render", this.onRender );
      }
    },

 
    
    ej_action : function(event) {
    	
    	var target;
       
          target = event.target;
        
        
        if(target && target.hasAttribute && target.hasAttribute('em'))
    	{
        	var remoteObject = rap.getRemoteObject(this);
        	var args = {};
        	var argTag  = target.getAttribute('earg');
        	if(argTag)
    		{
    		  var tags = argTag.split(" , ");
    		    for(var i =0;i<tags.length;i++)
	          	{
    		    	args[''+i]= tags[i];
	          	}
    		}
        	
        	remoteObject.call(target.getAttribute('em'),args);
    	}
    },
    
    ej_select : function(event) {
    	
    	var target;
       
          target = event.target;
        
        
        while(true) {
          if(target.parentNode == null) {
            break;
          }
          if(target.parentNode && target.parentNode.nodeName == "TR") {
        	  target =  target.parentNode ;
            break;
          }
          target =  target.parentNode ;
        }
        
        
        if(target && target.hasAttribute && target.hasAttribute('recid'))
    	{
        	
       	 var elemsnts= this.element.getElementsByClassName("rowindi");
         if(elemsnts)
     	{
         	
         	for(var i =0;i<elemsnts.length;i++)
         	{
         		var elm = elemsnts[i];
         		if(elm.parentNode == target)
         			{
         			elm.style.visibility = "visible";
         			}
         		else
         			elm.style.visibility = "hidden";
         	}
     	}
        
    	
    	
    	

        	
        	var remoteObject = rap.getRemoteObject(this);
        	var args = {};
        	args['0']=target.getAttribute('recid');
        	
        	remoteObject.call('eselect',args);
    	}
    },
    
    ej_scroll : function() {
    	var remoteObject = rap.getRemoteObject(this);
    	
    	var pos = this.scrolDiv.scrollTop;
    	
    	var arg = {}
    	arg['vpos'] = pos;
    	remoteObject.set('scroll',arg);
        
    },
    
    setScroll : function(pos) {
    	this.scrolDiv.scrollTop = pos;
    },
   
    setText : function( text ) {
      
    	
    	while( this.element.childNodes[0] ) {
    		this.element.removeChild( this.element.childNodes[0] );
    	}
        var elm = this.document.createElement( "div" );

        this.element.appendChild( elm);
        elm.innerHTML = text;
    	
        var elemsnts= this.element.getElementsByTagName("ejl");
        if(elemsnts)
    	{
        	var func = this.ej_action;
        	for(var i =0;i<elemsnts.length;i++)
        	{
        		elemsnts[i].onclick =  func;
        	}
    	}
        
        var elemsnts= this.element.getElementsByTagName("tr");
        if(elemsnts)
    	{
        	
        	for(var i =0;i<elemsnts.length;i++)
        	{
        		
        		var func = this.ej_select;
        		elemsnts[i].onclick =  func;
        	}
    	}
        
        
        var divs = this.element.getElementsByTagName("div");
        if(divs && divs[0])
        {
        	this.scrolDiv = divs[0];
        	this.scrolDiv.onscroll = this.ej_scroll;
        }
       
    },

    destroy : function() {
    	if(this.element.parentNode)
		{
    		this.element.parentNode.removeChild( this.element);
		}
    	
      
    },

    layout : function() {
        var area = this.parent.getClientArea();
        this.element.style.left = area[ 0 ] + "px";
        this.element.style.top = area[ 1 ] + "px";
    }

  };

  var bind = function( context, method ) {
    return function() {
      return method.apply( context, arguments );
    };
  };

  var bindAll = function( context, methodNames ) {
    for( var i = 0; i < methodNames.length; i++ ) {
      var method = context[ methodNames[ i ] ];
      context[ methodNames[ i ] ] = bind( context, method );
    }
  };



}());