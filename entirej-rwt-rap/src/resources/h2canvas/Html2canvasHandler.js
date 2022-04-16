var CKEDITOR_BASEPATH = "rwt-resources/html2canvas/";

(function(){
  'use strict';

  rap.registerTypeHandler( "entirej.H2Canvas", {
    factory : function( properties ) {
      return new entirej.H2Canvas( properties );
    },

    destructor : "destroy",

    properties : [ "data" ]

  } );

  if( !window.entirej ) {
    window.entirej = {};
  }

  entirej.H2Canvas = function( properties ) {
    bindAll( this, [ "ej_action" ] );
    this.document = document;
    this.window = window;
   
  };

  entirej.H2Canvas.prototype = {

    

    
    ej_action : function(event) {
    	
    	
    },
    
    
    setData : async function( data ) {
    	var remoteObject = rap.getRemoteObject(this);
    	if(data=='snap') {
    		
	    	var canvas	= await html2canvas( document.body);	
	    	var img = canvas.toDataURL("image/png");
	    	var args = {};
        	args['0']=img;
			remoteObject.call('data',args);
    	}
    	
       
    },
    
  
    

    

    destroy : function() {
    	
    	
      
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