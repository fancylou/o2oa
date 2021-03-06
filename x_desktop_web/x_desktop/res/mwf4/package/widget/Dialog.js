MWF.widget = MWF.widget || {};
MWF.widget.Dialog = MWF.DL = new Class({
	Implements: [Options, Events],
    Extends: MWF.widget.Common,
	options: {
		"style": "default",
		"title": "dialog",
		"width": "300",
		"height": "150",
		"top": "0",
		"left": "0",
		"fromTop": "0",
		"fromLeft": "0",
		"mark": true,

		"html": "",
		"text": "",
		"url": "",
		"content": null,

		"isMax": false,
		"isClose": true,
		"isResize": true,
		"isMove": true,
		
		"buttons": null,
		"buttonList": null,
        "maskNode" : null,

        "container": null
	},
	initialize: function(options){
		this.setOptions(options);

		this.path = MWF.defaultPath+"/widget/$Dialog/";
		this.cssPath = MWF.defaultPath+"/widget/$Dialog/"+this.options.style+"/css.wcss";

		this._loadCss();
		
		this.reStyle();
//		this.css.to.height = this.options.height;
//		this.css.to.width = this.options.width;
//		this.css.to.top = this.options.top;
//		this.css.to.left = this.options.left;
//		this.css.to.top = this.options.top;
//		this.css.from.top = this.options.fromTop;
//		this.css.from.left = this.options.fromLeft;
		
		this.fireEvent("queryLoad");

		this.getContentUrl();
		var request = new Request.HTML({
			url: this.contentUrl,
			method: "GET",
			async: false,
			onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
				this.node = responseTree[0];
				this.getDialogNode();
				this.fireEvent("postLoad");
			}.bind(this),
			onFailure: function(xhr){
				alert(xhr);
			}
		});
		request.send();
	},
	getContentUrl: function(){
		this.contentUrl = MWF.defaultPath+"/widget/$Dialog/"+this.options.style+"/dialog.html";
	},
	reStyle: function(options){
		if (options) this.setOptions(options);
		this.css.to.height = this.options.height+"px";
		this.css.to.width = this.options.width+"px";
		this.css.to.top = this.options.top+"px";
		this.css.to.left = this.options.left+"px";
		this.css.to.top = this.options.top+"px";
		this.css.from.top = this.options.fromTop+"px";
		this.css.from.left = this.options.fromLeft+"px";

		if (this.node) this.node.set("styles", this.css.from);
	},

    getParentSelect: function(node){
        var select = ""
        var pnode = node.getParent();
        while (!select && pnode){
            select = pnode.getStyle("-webkit-user-select");
            var pnode = pnode.getParent();
        }
        return select;
    },
	getDialogNode: function(){
		this.node.set("styles", this.css.from);
		this.node.inject(this.options.container || $(document.body));
		this.node.addEvent("selectstart", function(e){

			
			var select = e.target.getStyle("-webkit-user-select");
            if (!select) select = this.getParentSelect(e.target);
			if (!select){
				select = "none";
			}else{
				select = select.toString().toLowerCase();
			}
			var tag = e.target.tagName.toString().toLowerCase();
			if (select!="text" && select!="auto" && ["input", "textarea"].indexOf(tag)==-1) e.preventDefault();
			
        }.bind(this));

		this.title = this.node.getElement(".MWF_dialod_title");
		this.titleCenter = this.node.getElement(".MWF_dialod_title_center");
        this.titleRefresh = this.node.getElement(".MWF_dialod_title_refresh");
		this.titleText = this.node.getElement(".MWF_dialod_title_text");
		this.titleAction = this.node.getElement(".MWF_dialod_title_action");
        this.under = this.node.getElement(".MWF_dialod_under");
		this.content = this.node.getElement(".MWF_dialod_content");
		this.bottom = this.node.getElement(".MWF_dialod_bottom");
		this.resizeNode = this.node.getElement(".MWF_dialod_bottom_resize");
		this.button = this.node.getElement(".MWF_dialod_button");

		if (this.title) this.setTitleEvent();
        if (this.titleRefresh) this.setTitleRefreshNode();
	//	if (this.titleText) this.getTitle();
		if (this.content) this.getContent();
		if (this.titleAction) this.getAction();
		if (this.resizeNode) this.setResizeNode();
	//	if (this.button) this.getButton();

		if (this.content) this.setContentSize();
	},
    setTitleRefreshNode: function(){
        this.titleRefresh.setStyles(this.css.titleRefresh);
        this.titleRefresh.set("title", MWF.LP.widget.refresh);
    },
	setTitleEvent: function(){

		this.title.addEvent("mousedown", function(){
			this.containerDrag = new Drag.Move(this.node);
		}.bind(this));
		this.title.addEvent("mouseup", function(){
			this.node.removeEvents("mousedown");
			this.title.addEvent("mousedown", function(){
				this.containerDrag = new Drag.Move(this.node);
			}.bind(this));
		}.bind(this));
	},
	setResizeNode: function(){
		//未实现................................
	},
	getAction: function(){
		//未实现................................
	},
	getButton: function(){
		for (i in this.options.buttons){
			var button = new Element("input", {
				"type": "button",
				"value": i,
				"styles": this.css.button,
				"events": {
					"click": this.options.buttons[i].bind(this)
				}
			}).inject(this.button);
		}
		if (this.options.buttonList){
			this.options.buttonList.each(function(bt){
				var button = new Element("input", {
					"type": "button",
					"value": bt.text,
					"styles": this.css.button,
					"events": {
						"click": bt.action.bind(this, this)
					}
				}).inject(this.button);
			}.bind(this));
		}
	},
	getContentSize: function(height, width){
		if (!height) height = this.options.height;
		if (!width) width = this.options.width;

		if (this.title){
			var h1 = this.title.getSize().y;
			var ptop1 = this.title.getStyle("padding-top").toFloat();
			var pbottom1 = this.title.getStyle("padding-bottom").toFloat();
			var mtop1 = this.title.getStyle("margin-top").toFloat();
			var mbottom1 = this.title.getStyle("margin-bottom").toFloat();
			
			height = height - h1 - ptop1 - pbottom1 - mtop1 - mbottom1;
		}
		if (this.bottom){
			var h2 = this.bottom.getSize().y;
			var ptop2 = this.bottom.getStyle("padding-top").toFloat();
			var pbottom2 = this.bottom.getStyle("padding-bottom").toFloat();
			var mtop2 = this.bottom.getStyle("margin-top").toFloat();
			var mbottom2 = this.bottom.getStyle("margin-bottom").toFloat();
			
			height = height - h2 - ptop2 - pbottom2 - mtop2 - mbottom2;
		}
		if (this.button){
			var h3 = this.button.getSize().y;
			var ptop3 = this.button.getStyle("padding-top").toFloat();
			var pbottom3 = this.button.getStyle("padding-bottom").toFloat();
			var mtop3 = this.button.getStyle("margin-top").toFloat();
			var mbottom3 = this.button.getStyle("margin-bottom").toFloat();
			
			height = height - h3 - ptop3 - pbottom3 - mtop3 - mbottom3;
		}
				
		var ptop4 = this.content.getStyle("padding-top").toFloat();
		var pbottom4 = this.content.getStyle("padding-bottom").toFloat();
		var mtop4 = this.content.getStyle("margin-top").toFloat();
		var mbottom4 = this.content.getStyle("margin-bottom").toFloat();
		height = height - ptop4 - pbottom4 - mtop4 - mbottom4;

        //if (this.content.getParent().getStyle("overflow-x")!="hidden" ) height = height-18;
		
		var pleft = this.content.getStyle("padding-left").toFloat();
		var pright = this.content.getStyle("padding-right").toFloat();
		var mleft = this.content.getStyle("margin-left").toFloat();
		var mright = this.content.getStyle("margin-right").toFloat();
		width = width-pleft-pright-mleft-mright;
        //if (this.content.getParent().getStyle("overflow-y")!="hidden" ) width = width-18;

        if (!height || height<0){
            this.content.setStyles({"overflow": "hidden", "height": "auto", "width": ""+width+"px"});
            height = this.content.getSize().y;
            var h = height + h1 + ptop1 + pbottom1 + mtop1 + mbottom1;
            h = h + h2 + ptop2 + pbottom2 + mtop2 + mbottom2;
            h = h + h3 + ptop3 + pbottom3 + mtop3 + mbottom3;
            h = h + ptop4 + pbottom4 + mtop4 + mbottom4;
            this.css.to.height = h;
        }

//		var ptop5 = this.node.getStyle("padding-top").toFloat();
//		var pbottom5 = this.node.getStyle("padding-bottom").toFloat();
//		height = height - ptop5 - pbottom5;
		
		return {"height": height+"px", "width": width+"px"};
	},
	setContentSize: function(height, width){
		//this.content.setStyle("height", this.getContentSize(height));
		// if (!this.options.height && !height){
         //    this.content.setStyle("height", "auto");
         //    this.content.setStyle("overflow", "hidden");
         //    this.content.setStyle("width", "auto");
		// }else{
            this.content.setStyles(this.getContentSize(height, width));
            this.content.setStyle("width", "auto");
		//}

	},
	getTitle: function(){
		this.titleText.set("text", this.options.title);
	},
	getContent: function(){
		this.content.setStyles(this.css.content);
		if (this.options.content){
			this.options.content.inject(this.content);
		}else if (this.options.url){
			this.content.set("load", {"method": "get", "async": false});
			$(this.content).load(this.options.url);
/*
			var request = new Request.HTML({
				url: this.options.url,
				method: "GET",
				onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
					alert(responseHTML);
					this.content.set("html", responseHTML);
				}.bind(this),
				onFailure: function(xhr){
					alert("回退出现错误："+xhr.status+" "+xhr.statusText);
					window.close();
				}
			});*/
		}else if (this.options.html){
			this.content.set("html", this.options.html);
		}else if (this.options.text){
			this.content.set("text", this.options.text);
		}
//		this.content.addEvent("selectstart", function(e){
//			e.preventDefault();
//		});
	},
	show: function(){
		if (this.options.mark) this._markShow();
		if (!this.morph){
			this.morph = new Fx.Morph(this.node, {duration: 200});
		}
		if (this.fireEvent("queryShow")){
			this.node.setStyle("display", "block");
			this.morph.start(this.css.to).chain(function(){
				if (this.titleText) this.getTitle();
				if (this.button) this.getButton();
			//	this.content.setStyle("display", "block");
				this.fireEvent("postShow");
			}.bind(this));
		}
	},
	hide: function() {
		if (!this.morph){
			this.morph = new Fx.Morph(this.node, {duration: 200});
		}
		if (this.fireEvent("queryHide")){
			if (this.titleText) this.titleText.set("text", "");
			if (this.button) this.button.set("html", "");
			
			this.morph.start(this.css.from).chain(function(){
				this._markHide();
				this.node.setStyle("display", "none");
				this.fireEvent("postHide");
			}.bind(this));
		}
	},
	close: function(){
		if (!this.morph){
			this.morph = new Fx.Morph(this.node, {duration: 200});
		}
		
		if (this.fireEvent("queryClose")){
			this.morph.start(this.css.from).chain(function(){
				this._markHide();
				this.node.destroy();
				this.node = null;
				this.fireEvent("postClose");
			}.bind(this));
		}
	},
	_markShow: function(){

		if (this.options.mark){
			if (!this.markNode){
				var size = MWF.getMarkSize(this.options.maskNode);
				this.markNode = new Element("div", {
					styles: this.css.mark
				}).inject(this.options.container || $(document.body));
				this.markNode.set("styles", {
					"height": size.y,
					"width": size.x
				});
			}
			this.markNode.setStyle("display", "block");
		}
	},
	
	_markHide: function(){
		if (this.markNode){
			this.markNode.setStyle("display", "none");
		}
	}
});