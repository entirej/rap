
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
		this.resizeTimer = null;

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
			if (this.resizeTimer) {
				window.clearTimeout(this.resizeTimer);
				this.resizeTimer = null;
			}
			this.context = _context;
			if(this.context&&  this.context.options)
			{
				var options = this.context.options;
				options.plugins = options.plugins || {};
				options.plugins.legend = options.plugins.legend || {};
				options.plugins.tooltip = options.plugins.tooltip || {};
				options.plugins.tooltip.callbacks = options.plugins.tooltip.callbacks || {};
				options.onClick = this.chart_action;
				this.defaultLegendClickHandler = (this.context.type == 'pie' || this.context.type == 'doughnut') ?
                Chart.controllers.doughnut.overrides.plugins.legend.onClick :  Chart.defaults.plugins.legend.onClick;
				options.plugins.legend.onClick = this.chart_legend_action;
				options.plugins.tooltip.callbacks.label = this.chart_tooltip;
				
				
				if(this.context.options.scales) {
					
					if(this.context.options.scales.y && this.context.options.scales.y.ticks)
						this.context.options.scales.y.ticks.callback= function(label, index, labels) {
					        return isNaN(label) ? label :Number(label).toLocaleString();
					    };
					    
				    if(this.context.options.scales.x && this.context.options.scales.x.ticks)
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
					this.scheduleResize();
                }
				
			} 
		},

	
	

		scheduleResize : function() {
			if (this.resizeTimer) {
				window.clearTimeout(this.resizeTimer);
			}
			var chart = this.chart;
			this.resizeTimer = window.setTimeout(function() {
				if (chart && chart.canvas) {
					chart.resize();
				}
			}, 100);
		},

		destroy : function() {
			rap.off("render", this.onRender);
			rap.off("send", this.onSend);
			if (this.parent) {
				this.parent.removeListener("Resize", this.layout);
			}
			if (this.resizeTimer) {
				window.clearTimeout(this.resizeTimer);
				this.resizeTimer = null;
			}
			if (this.chart) {
				this.chart.stop();
				this.chart.destroy();
				this.chart = null;
			}
			if (this.element.parentNode) {
				this.element.parentNode.removeChild(this.element);
			}
			this.context = null;
			this.parentNode = null;
			this.parent = null;
		},

		layout : function() {
			if (this.ready) {
				
					
				if(this.context && this.chart)
                {
                	var gc = this.parentNode.getContext("2d");
                	var area = this.parent.getClientArea();
                	gc.canvas.style.zIndex = 10000; // small hack to make sure chart gets the mouse events
                	gc.canvas.position = 'absolute';
                    gc.canvas.height =area[3];
                    gc.canvas.width = area[2];
                	
					this.scheduleResize();
                	
                }
				
				
			}
		},
		chart_action : function(evt) {
			if (!this.chart || !this.context) {
				return;
			}
			var activeElements = this.chart.getElementsAtEventForMode(evt, 'nearest', { intersect: true }, false);
			if(activeElements && activeElements.length > 0) {
				var activeElement = activeElements[0];
				if(this.context.data.actions) {
					var action = this.context.data.actions[activeElement.datasetIndex];
					if(action) {
						var remoteObject = rap.getRemoteObject(this);
						var dataset = this.chart.data.datasets[activeElement.datasetIndex];
						var args = {data_label: dataset.label,label: this.chart.data.labels[activeElement.index],value: dataset.data[activeElement.index]};
						remoteObject.call(action,args);
					}
				}
			}
		},

		chart_legend_action : function(e, legendItem, legend) {
			if(this.context.options.plugins.legend.defaultAction) {
				this.defaultLegendClickHandler(e, legendItem, legend);
			}

			var remoteObject = rap.getRemoteObject(this);
			var index = legendItem.datasetIndex == null ? legendItem.index : legendItem.datasetIndex;
			var args = {index: index,label:legendItem.text};
			remoteObject.call('legend_action',args);
		},

		chart_tooltip : function(context) {
			var dataset = context.dataset;
			var index = context.dataIndex;
			if(dataset.dataTooltips && dataset.dataTooltips[index] != null)
				return dataset.dataTooltips[index];

			return dataset.data[index];
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
