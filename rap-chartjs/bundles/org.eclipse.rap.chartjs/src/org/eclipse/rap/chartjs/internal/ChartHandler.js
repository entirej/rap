
(function() {
	'use strict';

	rap.registerTypeHandler("chartjs.AbstractChart", {

		factory : function(properties) {
			return new chartjs.AbstractChart(properties);
		},

		destructor : "destroy",

		properties : [ "context"]

	});

	if (!window.chartjs) {
		window.chartjs = {};
	}

	chartjs.AbstractChart = function(properties) {
		bindAll(this, [ "layout", "onReady", "onSend", "onRender","chart_action","chart_tooltip","chart_legend_action" ]);
		this.parent = rap.getObject(properties.parent);
		
		this.element = document.createElement("canvas");
		this.element.style.height = '100%';
		this.element.style.overflow = 'auto';
		this.element.style.position = 'absolute';
		this.element.style.bottom = '0px';
		this.element.style.top = '0px';
		this.element.style.left = '0px';
		this.element.style.right = '0px';
		this.context = properties.context;
		this.chart =  null;

		this.defaultLegendClickHandle = null;
		
		

		this.parent.append(this.element);
		this.parent.addListener("Resize", this.layout);
		rap.on("render", this.onRender);
	};

	chartjs.AbstractChart.prototype = {

		ready : false,

		onReady : function() {
			
			
			
			
			this.layout();
			
		},

		onRender : function() {

			if (this.element.parentNode) {
				rap.off("render", this.onRender);
				
				this.ready = true;

				this.parentNode = this.element;
				this.setContext(this.context);
				
				rap.on("send", this.onSend);
			}
		},

		onSend : function() {
			
		},

		setContext : function(_context) {
			this.context = _context;
			if(this.context&&  this.context.options)
			{
				this.context.options.onClick = this.chart_action;
				this.defaultLegendClickHandler = (this.context.type == 'pie' || this.context.type == 'doughnut') ?
                Chart.controllers.doughnut.overrides.plugins.legend.onClick :  Chart.defaults.plugins.legend.onClick;
				this.context.options.plugins.legend.onClick = this.chart_legend_action;
				this.context.options.tooltips.callbacks.label = this.chart_tooltip;
				
				
				if(this.context.options.scales) {
					
					if(this.context.options.scales.y && this.context.options.scales.y.ticks)
						this.context.options.scales.y.ticks.callback= function(label, index, labels) {
					        return isNaN(label) ? label :Number(label).toLocaleString();
					    };
					    
				    if(this.context.options.scales.x && this.context.options.scales.ticks)
						this.context.options.scales.x.ticks.callback= function(label, index, labels) {
					        return isNaN(label) ? label :Number(label).toLocaleString();
					    };
					
				}
				
				    
			    
			}
			if (this.ready) {
				
				if( this.chart )
				{
					this.chart.stop();
					this.chart.destroy();
					this.chart = null;
				}
					
                if(this.context)
                {
                	var gc = this.parentNode.getContext("2d");
                	var area = this.parent.getClientArea();
                	gc.canvas.style.zIndex = 10000; // small hack to make sure chart gets the mouse events
                    this.context.options.responsive = false;
                    this.context.options.maintainAspectRatio = false;
                    gc.canvas.position = 'absolute';
                    gc.canvas.height =area[3];
                    gc.canvas.width = area[2];
                	
                	this.chart = new Chart( gc ,this.context);
                	this.context.options.animation = false; // no animation on refresh
                    let rChart  = this.chart;
                    window.setTimeout(function() {
                                rChart.resize();
                            }, 100);
                }
				
			} 
		},

	
	

		destroy : function() {
			if (this.parentNode.parentNode) {
				rap.off("send", this.onSend);
				if( this.chart )
				{
					this.chart.stop();
					this.chart.destroy();
					this.chart = null;
				}
				this.element.parentNode.removeChild(this.element);
				
                
			}
		},

		layout : function() {
			if (this.ready) {
				
					
				if(this.context)
                {
                	var gc = this.parentNode.getContext("2d");
                	var area = this.parent.getClientArea();
                	gc.canvas.style.zIndex = 10000; // small hack to make sure chart gets the mouse events
                	gc.canvas.position = 'absolute';
                    gc.canvas.height =area[3];
                    gc.canvas.width = area[2];
                	
                    let rChart  = this.chart;
                     window.setTimeout(function() {
                     rChart.resize();
                                                }, 100);
                	
                }
				
				
			}
		},
		chart_action : function(evt) {
	    	
			 var activeElement = this.chart.getElementsAtEventForMode(evt, 'nearest', { intersect: true }, false) 
	    	 if(activeElement!=null && activeElement[0]!=null && activeElement[0]._model!=null)
			 {
	    		 if(this.context.data.actions)
	    	     {
	    		    var action = this.context.data.actions[activeElement[0]._datasetIndex];
	    		    if(action)
	    		    {
			    		  var remoteObject = rap.getRemoteObject(this);
			    		  var dataset = activeElement[0]._chart.data.datasets[activeElement[0]._datasetIndex];
			    		  
				          var args = {data_label: dataset.label,label: activeElement[0]._chart.data.labels[activeElement[0]._index],value: dataset.data[activeElement[0]._index]};
				        
				        	
				        	  remoteObject.call(action,args);
	    	        }
			     }
			 }
	        
	        
	        
	    },
	    
	    chart_legend_action : function(e, legendItem,i) {
	        
	    	
            if(this.context.options.plugins.legend.defaultAction)
	    	  // Do the original logic
	           this.defaultLegendClickHandler(e, legendItem,i);
	        
	        var remoteObject = rap.getRemoteObject(this);
	        var args = {index: legendItem.datasetIndex,label:legendItem.text};
      	    remoteObject.call('legend_action',args);
	    	
	    	
	    },
	    chart_tooltip : function(tooltipItem, data) {
	    	
			if(data.datasets[tooltipItem.datasetIndex].dataTooltips) 
				return data.datasets[tooltipItem.datasetIndex].dataTooltips[tooltipItem.index];
	    	
	    	return data.datasets[tooltipItem.datasetIndex].data[tooltipItem.index];
	        
	        
	    },

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
