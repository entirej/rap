var CKEDITOR_BASEPATH = "rwt-resources/ejhtmlview/";

(function() {
    'use strict';

    rap.registerTypeHandler("entirej.HtmlView", {
        factory: function(properties) {
            return new entirej.HtmlView(properties);
        },

        destructor: "destroy",

        properties: ["text", 'scroll', 'selection']

    });

    if (!window.entirej) {
        window.entirej = {};
    }

    entirej.HtmlView = function(properties) {
        bindAll(this, ["layout", "onRender", "ej_action", "ej_select", "ej_dblclick", 'ej_scroll', 'ej_scroll_main', 'ej_text_select']);
        this.parent = rap.getObject(properties.parent);
        this.document = document;
        this.window = window;
        this.element = document.createElement("div");
        this.textSelect = properties.textSelect;
        this.element.style.height = '100%';
        this.element.style.overflow = 'auto';
        this.element.style.position = 'absolute';
        this.element.style.bottom = '0px';
        this.element.style.top = '0px';
        this.element.style.left = '0px';
        this.element.style.right = '0px';

        this.parent.append(this.element);
        this.parent.addListener("Resize", this.layout);
        rap.on("render", this.onRender);
    };

    entirej.HtmlView.prototype = {



        onRender: function() {
            if (this.element.parentNode) {
                rap.off("render", this.onRender);
            }
        },



        ej_action: function(event) {

            var target;

            target = event.target;


            if (target && target.hasAttribute && target.hasAttribute('em')) {
                var remoteObject = rap.getRemoteObject(this);
                var args = {};
                var argTag = target.getAttribute('earg');
                if (argTag) {
                    var tags = argTag.split(" , ");
                    for (var i = 0; i < tags.length; i++) {
                        args['' + i] = tags[i];
                    }
                }

                remoteObject.call(target.getAttribute('em'), args);
            }
        },

        ej_select: function(event) {

            var target;

            target = event.target;


            while (true) {
                if (target.parentNode == null) {
                    break;
                }
                if (target.parentNode && target.parentNode.nodeName == "TR") {
                    target = target.parentNode;
                    break;
                }
                target = target.parentNode;
            }


            if (target && target.hasAttribute && target.hasAttribute('recid')) {

                var elemsnts = this.element.getElementsByClassName("rowindi");
                if (elemsnts) {

                    for (var i = 0; i < elemsnts.length; i++) {
                        var elm = elemsnts[i];
                        if (elm.parentNode == target) {
                            elm.style.visibility = "visible";
                        }
                        else
                            elm.style.visibility = "hidden";
                    }
                }






                var remoteObject = rap.getRemoteObject(this);
                var args = {};
                args['0'] = target.getAttribute('recid');

                remoteObject.call('eselect', args);
            }
        },
        ej_dblclick: function(event) {

            var target;

            target = event.target;


            while (true) {
                if (target.parentNode == null) {
                    break;
                }
                if (target.parentNode && target.parentNode.nodeName == "TR") {
                    target = target.parentNode;
                    break;
                }
                target = target.parentNode;
            }


            if (target && target.hasAttribute && target.hasAttribute('recid')) {

                var elemsnts = this.element.getElementsByClassName("rowindi");
                if (elemsnts) {

                    for (var i = 0; i < elemsnts.length; i++) {
                        var elm = elemsnts[i];
                        if (elm.parentNode == target) {
                            elm.style.visibility = "visible";
                        }
                        else
                            elm.style.visibility = "hidden";
                    }
                }






                var remoteObject = rap.getRemoteObject(this);
                var args = {};
                args['0'] = target.getAttribute('recid');

                remoteObject.call('edblclick', args);
            }
        },


        ej_text_select: function selectText(e) {
            e = e || window.event;
            var obj = e.target || e.srcElement;
            if (this.document.selection) {
                var range = this.document.body.createTextRange();
                if (obj.firstChild == null || obj.firstChild.nodeType != 3) {
                    return;
                }
                range.moveToElementText(obj.firstChild);
                range.select();
            }
            else if (this.window.getSelection) {
                var range = this.document.createRange();
                if (obj.firstChild == null || obj.firstChild.nodeType != 3) {
                    return;
                }
                range.selectNode(obj.firstChild);
                this.window.getSelection().removeAllRanges();
                this.window.getSelection().addRange(range);
            }
        },


        ej_scroll: function() {
            var remoteObject = rap.getRemoteObject(this);

            var pos = this.element.scrollTop;
            var scrollHeight = 0;
            var scrollWidth = this.element.scrollWidth;
            var elemsnts = this.element.getElementsByTagName("tr");
            if (elemsnts) {

                for (var i = 0; i < elemsnts.length; i++) {

                    var row = elemsnts[i]
                    scrollHeight += row.offsetHeight;
                }
            }

            var arg = {}
            arg['vpos'] = pos;
            arg['scrollHeight'] = scrollHeight;
            arg['scrollWidth'] = scrollWidth;
            remoteObject.set('scroll', arg);

        },
        ej_scroll_main: function() {
            var remoteObject = rap.getRemoteObject(this);

            var pos = this.element.scrollTop;
            var scrollHeight = 0;
            var scrollWidth = this.element.scrollWidth;
            var elemsnts = this.element.getElementsByTagName("tr");
            if (elemsnts) {

                for (var i = 0; i < elemsnts.length; i++) {

                    var row = elemsnts[i]
                    scrollHeight += row.offsetHeight;
                }
            }
            var arg = {}
            arg['vpos'] = pos;
            arg['scrollHeight'] = scrollHeight;
            arg['scrollWidth'] = scrollWidth;
            remoteObject.set('scroll', arg);

        },

        setScroll: function(pos) {
            this.scrolDiv.scrollTop = pos;
            this.element.scrollTop = pos;
        },

        setText: function(text) {


            while (this.element.childNodes[0]) {
                this.element.removeChild(this.element.childNodes[0]);
            }
            var elm = this.document.createElement("div");

            this.element.appendChild(elm);
            elm.innerHTML = text;

            var elemsnts = this.element.getElementsByTagName("ejl");
            if (elemsnts) {
                var func = this.ej_action;
                for (var i = 0; i < elemsnts.length; i++) {
                    elemsnts[i].onclick = func;
                }
            }

            var elemsnts = this.element.getElementsByTagName("tr");
            if (elemsnts) {

                for (var i = 0; i < elemsnts.length; i++) {

                    var func = this.ej_select;
                    var dbfunc = this.ej_dblclick;
                    elemsnts[i].onclick = func;
                    elemsnts[i].ondblclick = dbfunc;
                }
            }

            if (this.textSelect) {
                var elemsnts = this.element.getElementsByTagName("td");
                if (elemsnts) {

                    for (var i = 0; i < elemsnts.length; i++) {

                        var func = this.ej_text_select;
                        elemsnts[i].onclick = func;
                    }
                }

            }


            var divs = this.element.getElementsByTagName("div");
            var ej_scroll_main = this.ej_scroll_main;
            this.element.onscroll = this.ej_scroll_main;
            if (divs && divs[0]) {
                this.scrolDiv = divs[0];
                this.scrolDiv.onscroll = this.ej_scroll;
            }
            window.setTimeout(ej_scroll_main, 100);


        },



        setSelection: function(id) {
            var elemsnts = this.element.getElementsByTagName("tr");
            if (elemsnts) {

                for (var i = 0; i < elemsnts.length; i++) {
                    var el = elemsnts[i];
                    if (el.getAttribute('recid') == id) {
                        el.scrollIntoView(true);

                        var indelemsnts = this.element.getElementsByClassName("rowindi");
                        if (indelemsnts) {

                            for (var j = 0; j < indelemsnts.length; j++) {
                                var elm = indelemsnts[j];
                                if (elm.parentNode == el) {
                                    elm.style.visibility = "visible";
                                }
                                else
                                    elm.style.visibility = "hidden";
                            }
                        }

                        break;
                    }
                }
            }

        },


        destroy: function() {
            if (this.element.parentNode) {
                this.element.parentNode.removeChild(this.element);
            }


        },

        layout: function() {
            var area = this.parent.getClientArea();
            this.element.style.left = area[0] + "px";
            this.element.style.top = area[1] + "px";
            var ej_scroll_main = this.ej_scroll_main;
            window.setTimeout(ej_scroll_main, 100);
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



}());