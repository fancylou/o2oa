MWF.xApplication.Organization = MWF.xApplication.Organization || {};
MWF.xApplication.Organization.Selector = MWF.xApplication.Organization.Selector || {};
MWF.xDesktop.requireApp("Organization", "Actions.RestActions", null, false);
MWF.xApplication.Organization.Selector.Person = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"count": 0,
		"title": "Select Person",
		"groups": [],
        "roles": [],
		"values": [],
		"names": [],
        "zIndex": 1000,
        "expand": true
	},
	initialize: function(container, options){
		this.setOptions(options);

		this.path = "/x_component_Organization/Selector/$Selector/";
		this.cssPath = "/x_component_Organization/Selector/$Selector/"+this.options.style+"/css.wcss";
		this._loadCss();
		
		this.container = $(container);
		this.action = new MWF.xApplication.Organization.Actions.RestActions();
		
		this.lastPeople = "";
		this.pageCount = "13";
		this.selectedItems = [];
        this.items = [];
	},
	load: function(){
        debugger;
		this.container.mask({
			"destroyOnHide": true,
			"style": this.css.maskNode
		});
      //  this.container.setStyle("z-index", this.options.zIndex);
		this.node = new Element("div", {
			"styles": (this.options.count.toInt()==1) ? this.css.containerNodeSingle : this.css.containerNode
		});
        this.node.setStyle("z-index", this.options.zIndex.toInt()+1);
		this.titleNode = new Element("div", {
			"styles": this.css.titleNode,
		}).inject(this.node);
		
		this.titleActionNode = new Element("div", {
			"styles": this.css.titleActionNode
		}).inject(this.titleNode);
		this.titleTextNode = new Element("div", {
			"styles": this.css.titleTextNode,
			"text": this.options.title
		}).inject(this.titleNode);
		
		this.contentNode = new Element("div", {
			"styles": this.css.contentNode
		}).inject(this.node);
		
		this.loadContent();
		
		this.actionNode = new Element("div", {
			"styles": this.css.actionNode
		}).inject(this.node);
        if (this.options.count.toInt()==1) this.actionNode.setStyle("text-align", "center");
		this.loadAction();
		
		this.node.inject(this.container);
		this.node.position({
			relativeTo: this.container,
		    position: "center",
		    edge: "center"
		});
		
		var size = this.container.getSize();
		var nodeSize = this.node.getSize();
		this.node.makeDraggable({
			"handle": this.titleNode,
			"limit": {
				"x": [0, size.x-nodeSize.x],
				"y": [0, size.y-nodeSize.y]
			}
		});
		
		this.setEvent();	
	},
	setEvent: function(){
		this.titleActionNode.addEvent("click", function(){
			this.close();
		}.bind(this));
		
	},
	close: function(){
		this.node.destroy();
		this.container.unmask();
		delete this;
	},
	loadAction: function(){
		this.okActionNode = new Element("button", {
			"styles": this.css.okActionNode,
			"text": "确　定"
		}).inject(this.actionNode);
		this.cancelActionNode = new Element("button", {
			"styles": this.css.cancelActionNode,
			"text": "取 消"
		}).inject(this.actionNode);
		this.okActionNode.addEvent("click", function(){
			this.fireEvent("complete", [this.selectedItems]);
			this.close();
		}.bind(this));
		this.cancelActionNode.addEvent("click", function(){this.fireEvent("cancel"); this.close();}.bind(this));
	},
	loadContent: function(){
		if (this.options.count.toInt()!=1) this.loadSelectedNode();
		this.loadSelectNode();
	},
	loadSelectNode: function(){
		this.selectNode = new Element("div", {
			"styles": (this.options.count.toInt()==1) ? this.css.selectNodeSingle : this.css.selectNode
		}).inject(this.contentNode);
		this.searchInputDiv = new Element("div", {
			"styles": this.css.searchInputDiv
		}).inject(this.selectNode);
		this.searchInput = new Element("input", {
			"styles": (this.options.count.toInt()==1) ? this.css.searchInputSingle : this.css.searchInput,
			"type": "text"
		}).inject(this.searchInputDiv);
		this.initSearchInput();
		
		this.letterAreaNode = new Element("div", {
			"styles": this.css.letterAreaNode
		}).inject(this.selectNode);
		this.loadLetters();
		
		this.itemAreaScrollNode = new Element("div", {
			"styles": this.css.itemAreaScrollNode
		}).inject(this.selectNode);
		
		this.itemAreaNode = new Element("div", {
			"styles": this.css.itemAreaNode
		}).inject(this.itemAreaScrollNode);
        this.itemSearchAreaNode = new Element("div", {
            "styles": this.css.itemAreaNode
        }).inject(this.itemAreaScrollNode);
		this.itemSearchAreaNode.setStyle("display", "none");

		MWF.require("MWF.widget.ScrollBar", function(){
			var _self = this;
			new MWF.widget.ScrollBar(this.itemAreaScrollNode, {
				"style":"xApp_Organization_Explorer", 
				"where": "before", 
				"distance": 30,
				"friction": 4,
				"axis": {"x": false, "y": true},
				"onScroll": function(y){
                    _self._scrollEvent(y);
				}
			});
		}.bind(this));
        this.initLoadSelectItems();
        if (!this.options.groups.length && !this.options.roles.length){
            this.loadSelectItems();
        }else{
            this.loadSelectItemsByCondition();
        }
	},
	initSearchInput: function(){
		this.searchInput.addEvents({
			"keydown": function(e){
				var iTimerID = this.searchInput.retrieve("searchTimer", null);
				if (iTimerID){
					window.clearTimeout(iTimerID);
					this.searchInput.eliminate("searchTimer");
				}
								
				iTimerID = window.setTimeout(function(){
					this.search();
				}.bind(this), 800);
				
				this.searchInput.store("searchTimer", iTimerID);
			}.bind(this),
            "change": function(e){
                var key = this.searchInput.get("value");
                if (!key) this.initSearchArea(false);
            }.bind(this),
            "blur": function(){
                var key = this.searchInput.get("value");
                if (!key) this.initSearchArea(false);
            }.bind(this)
		});
	},

    initSearchArea: function(flag){
        if (flag){
            this.itemSearchAreaNode.empty();
            this.itemAreaNode.setStyle("display", "none");
            this.itemSearchAreaNode.setStyle("display", "block");
        }else{
            this.itemAreaNode.setStyle("display", "block");
            this.itemSearchAreaNode.setStyle("display", "none");
        }
    },

	search: function(){
        if (!this.options.groups.length && !this.options.roles.length){
            var key = this.searchInput.get("value");
            if (key){
                this._listItemByKey(function(json){
                    this.initSearchArea(true);
                    json.data.each(function(data){
                        var flag = true;

                        if (this.options.departments){
                            if (this.options.departments.length){
                                if (this.options.departments.indexOf(data.departmentName)==-1) flag = false;
                            }
                        }
                        if (this.options.companys){
                            if (this.options.companys.length){
                                if (this.options.companys.indexOf(data.company)==-1) flag = false;
                            }
                        }

                        if (flag) this._newItem(data, this, this.itemSearchAreaNode);
                        //this._newItem(data, this, this.itemSearchAreaNode);
                    }.bind(this));
                }.bind(this), null, key);
            }else{
                this.initSearchArea(false);
            }
        }else{
            var key = this.searchInput.get("value");
            if (key){
                this.initSearchArea(true);
                this.searchInItems(key);
            }else{
                this.initSearchArea(false);
            }
        }
	},
    searchInItems: function(key){

        this.createItemsSearchData(function(){
            var word = key.toLowerCase();

            var createdId = [];
            this.itemsSearchData.each(function(obj){
                var text = obj.text+"#"+obj.pinyin+"#"+obj.firstPY;
                if (text.indexOf(word)!=-1){
                    if (createdId.indexOf(obj.data.id)==-1){
                        this._newItem(obj.data, this, this.itemSearchAreaNode);
                        createdId.push(obj.data.id);
                    }
                }
            }.bind(this));

            //this.searchItemsData(this.itemsSearchData.name, word, createdId);
            //this.searchItemsData(this.itemsSearchData.pinyin, word, createdId);
            //this.searchItemsData(this.itemsSearchData.firstPY, word, createdId);
            delete createdId;
        }.bind(this));
    },
    createItemsSearchData: function(callback){
        if (!this.itemsSearchData){
            this.itemsSearchData = [];
            MWF.require("MWF.widget.PinYin", function(){
                var initIds = [];
                this.items.each(function(item){
                    if (initIds.indexOf(item.data.id)==-1){
                        var text = item._getShowName().toLowerCase();
                        var pinyin = text.toPY().toLowerCase();
                        var firstPY = text.toPYFirst().toLowerCase();
                        this.itemsSearchData.push({
                            "text": text,
                            "pinyin": pinyin,
                            "firstPY": firstPY,
                            "data": item.data
                        });
                        initIds.push(item.data.id);
                    }
                }.bind(this));
                delete initIds;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    //searchItemsData: function(str, key, createdId){
    //    var name = str;
    //    var idx = name.indexOf(key);
    //    var position = 0;
    //    while (idx !=-1){
    //        var left = name.substr(0, idx);
    //        var i = left.split("#").length-1;
    //
    //        var index = i+position;
    //        position = i+1;
    //        var data = this.itemsSearchData.data[index];
    //        if (createdId.indexOf(data.id)==-1){
    //            this._newItem(data, this, this.itemSearchAreaNode);
    //            createdId.push(data.id);
    //        }
    //
    //
    //        name = name.substr(idx+key.length, name.length);
    //        var si = name.indexOf("#");
    //        if (si!=-1){
    //            name = name.substr(si+1, name.length);
    //            idx = name.indexOf(key);
    //        }else{
    //            idx = -1;
    //        }
    //    }
    //},
    //createItemsSearchData: function(callback){
    //    if (!this.itemsSearchData){
    //        this.itemsSearchData = {};
    //        MWF.require("MWF.widget.PinYin", function(){
    //            this.itemsSearchData.name = "";
    //            this.itemsSearchData.pinyin = "";
    //            this.itemsSearchData.firstPY = "";
    //            this.itemsSearchData.data = [];
    //
    //            var initIds = [];
    //            this.items.each(function(item){
    //                if (initIds.indexOf(item.data.id)==-1){
    //                    var text = item._getShowName();
    //                    var pinyin = text.toPY().toLowerCase();
    //                    var firstPY = text.toPYFirst().toLowerCase();
    //                    this.itemsSearchData.name = (this.itemsSearchData.name) ? this.itemsSearchData.name+"#"+text : text;
    //                    this.itemsSearchData.pinyin = (this.itemsSearchData.pinyin) ? this.itemsSearchData.pinyin+"#"+pinyin : pinyin;
    //                    this.itemsSearchData.firstPY = (this.itemsSearchData.firstPY) ? this.itemsSearchData.firstPY+"#"+firstPY : firstPY;
    //                    this.itemsSearchData.data.push(item.data);
    //                    initIds.push(item.data.id);
    //                }
    //            }.bind(this));
    //            delete initIds;
    //            if (callback) callback();
    //        }.bind(this));
    //    }else{
    //        if (callback) callback();
    //    }
    //},
	
	loadSelectedNode: function(){
		this.selectedScrollNode = new Element("div", {
			"styles": this.css.selectedScrollNode
		}).inject(this.contentNode);
		
		this.selectedNode = new Element("div", {
			"styles": this.css.selectedNode
		}).inject(this.selectedScrollNode);
		
		this.setSelectedItem();

		MWF.require("MWF.widget.ScrollBar", function(){
			var _self = this;
			new MWF.widget.ScrollBar(this.selectedScrollNode, {
				"style":"xApp_Organization_Explorer", "where": "before", "distance": 100, "friction": 4,"axis": {"x": false, "y": true}
			});
		}.bind(this));
	},

	setSelectedItem: function(){

		if (this.options.values.length){
			this.options.values.each(function(v){

				this._getItem(function(json){
					this.selectedItems.push(this._newItemSelected(json.data, this, null));
				}.bind(this), null, v, false);
			}.bind(this));
		}else if (this.options.names.length){
			this.options.names.each(function(v){
                if (v){
                    this._listItemByKey(function(json){
                        var data = "";
                        if (json.data.length) data = json.data[0];
                        if (data){
                            this.selectedItems.push(this._newItemSelected(data, this, null));
                        }

                    }.bind(this), null, v);
                }
			}.bind(this));
		}
	},
	loadLetters: function(){
		var _self = this;
		letters = ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"];
		letters.each(function(l){
			var letterNode = new Element("div", {
				"styles": this.css.letterNode,
				"text": l
			}).inject(this.letterAreaNode);
			letterNode.addEvents({
				"mouseover": function(e){
					e.target.setStyles(this.css.letterNode_over);
					var showNode = new Element("div", {
						"styles": this.css.letterShowNode,
						"text": e.target.get("text"),
					}).inject(this.selectNode);
					showNode.position({
						relativeTo: this.itemAreaScrollNode,
					    position: "center",
					    edge: "center"
					});
					e.target.store("showNode", showNode);
				}.bind(this),
				"mouseout": function(e){
					var showNode = e.target.retrieve("showNode");
					showNode.destroy();
					e.target.setStyles(this.css.letterNode);
				}.bind(this),
				"click": function(){
					_self.listPersonByPinyin(this);
				}
			});
		}.bind(this));
	},

	listPersonByPinyin: function(node){
        this.searchInput.focus();
		var pinyin = this.searchInput.get("value");
		pinyin = pinyin+node.get("text");
		this.searchInput.set("value", pinyin);

        if (!this.options.groups.length && !this.options.roles.length){
            if (pinyin){
                this._listItemByPinyin(function(json){
                    this.initSearchArea(true);
                    json.data.each(function(data){
                        var flag = true;
                        if (this.options.departments){
                            if (this.options.departments.length){
                                if (this.options.departments.indexOf(data.departmentName)==-1) flag = false;
                            }
                        }
                        if (this.options.companys){
                            if (this.options.companys.length){
                                if (this.options.companys.indexOf(data.company)==-1) flag = false;
                            }
                        }
                        if (flag) this._newItem(data, this, this.itemSearchAreaNode);
                        //this._newItem(data, this, this.itemSearchAreaNode);
                    }.bind(this));
                }.bind(this), null, pinyin.toLowerCase());
            }
        }else{
            if (pinyin){
                this.initSearchArea(true);
                this.searchInItems(pinyin);
            }else{
                this.initSearchArea(false);
            }
        }


	},
    initLoadSelectItems: function(){
        this.loaddingItems = false;
        this.isItemLoaded = false;
        this.loadItemsQueue = 0;
        this.initSearchArea(false);
    },
	loadSelectItems: function(addToNext){
        if (!this.isItemLoaded){
            if (!this.loaddingItems){
                this.loaddingItems = true;
                var count = 20;
                this._listItemNext(this.getLastLoadedItemId(), count, function(json){
                    if (json.data.length){
                        json.data.each(function(data){
                            var item = this._newItem(data, this, this.itemAreaNode);
                            this.items.push(item);
                        }.bind(this));
                        this.loaddingItems = false;

                        if (json.data.length<count){
                            this.isItemLoaded = true;
                        }else{
                            if (this.loadItemsQueue>0){
                                this.loadItemsQueue--;
                                this.loadSelectItems();
                            }
                        }
                    }else{
                        this.isItemLoaded = true;
                        this.loaddingItems = false;
                    }
                }.bind(this));
            }else{
                if (addToNext) this.loadItemsQueue++;
            }
        }
    },
    getLastLoadedItemId: function(){
        return (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
    },

    loadSelectItemsByCondition: function(){
        this.options.groups.each(function(group){

            this.action.listGroupByKey(function(json){
                if (json.data.length){
                    var groupData = json.data[0]
                    var category = this._newItemCategory("ItemGroupCategory", groupData, this, this.itemAreaNode);
                    this._getChildrenItemIds(groupData).each(function(id){
                        this._getItem(function(json){
                            var item = this._newItem(json.data, this, category.children);
                            this.items.push(item);
                        }.bind(this), null, id)
                    }.bind(this));
                }
            }.bind(this), null, group);
        }.bind(this));

        this.options.roles.each(function(role){
            this.action.listRoleByKey(function(json){
                if (json.data.length){
                    var roleData = json.data[0]
                    var category = this._newItemCategory("ItemRoleCategory", roleData, this, this.itemAreaNode);
                    this._getChildrenItemIds(roleData).each(function(id){
                        this._getItem(function(json){
                            var item = this._newItem(json.data, this, category.children);
                            this.items.push(item);
                        }.bind(this), null, id)
                    }.bind(this));
                }
            }.bind(this), null, role);
        }.bind(this));

    },
    _getChildrenItemIds: function(data){
        return data.personList;
    },
    _newItemCategory: function(type, data, selector, item){
        return new MWF.xApplication.Organization.Selector.Person[type](data, selector, item)
    },

    _listItemByKey: function(callback, failure, key){
        this.action.listPersonByKey(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _getItem: function(callback, failure, id, async){
        this.action.getPerson(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, id, async);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Organization.Selector.Person.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        this.action.listPersonByPinyin(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _newItem: function(data, selector, container){
        return new MWF.xApplication.Organization.Selector.Person.Item(data, selector, container);
    },
    _listItemNext: function(last, count, callback){
        this.action.listPersonNext(last, count, function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this));
    },
    _scrollEvent: function(y){
        if (!this.options.groups.length && !this.options.roles.length){
            var scrollSize = this.itemAreaScrollNode.getScrollSize();
            var clientSize = this.itemAreaScrollNode.getSize();
            var scrollHeight = scrollSize.y-clientSize.y;
            if (y+30>scrollHeight) {
                if (!this.isItemLoaded) this.loadSelectItems();
            }
        }
    }
});
MWF.xApplication.Organization.Selector.Person.Item = new Class({
	initialize: function(data, selector, container, level){
		this.data = data;
		this.selector = selector;
		this.container = container;
		this.isSelected = false;
        this.level = (level) ? level.toInt() : 1;
		this.load();
	},
    _getShowName: function(){
        return this.data.display+"("+this.data.employee+")";
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/personicon.png)");
    },
	load: function(){
		this.node = new Element("div", {
			"styles": this.selector.css.selectorItem
		}).inject(this.container);

        this.levelNode = new Element("div", {
            "styles": this.selector.css.selectorItemLevelNode
        }).inject(this.node);
        var indent = this.level*10;
        this.levelNode.setStyle("width", ""+indent+"px");

		this.iconNode = new Element("div", {
			"styles": this.selector.css.selectorItemIconNode
		}).inject(this.node);
        this._setIcon();
		
		this.actionNode = new Element("div", {
			"styles": this.selector.css.selectorItemActionNode
		}).inject(this.node);
		
		this.textNode = new Element("div", {
			"styles": this.selector.css.selectorItemTextNode,
			"text": this._getShowName()
		}).inject(this.node);
        var m = this.textNode.getStyle("margin-left").toFloat()+indent;
        this.textNode.setStyle("margin-left", ""+m+"px");

        this.loadSubItem();

		this.setEvent();
		
		this.check();
	},
    loadSubItem: function(){},
	check: function(){
		if (this.selector.options.count.toInt()==1){
			this.checkSelectedSingle();
		}else{
			this.checkSelected();
		}
	},
	checkSelectedSingle: function(){
		var selectedItem = this.selector.options.values.filter(function(item, index){
		    return this.data.id == item;
		}.bind(this));
		if (selectedItem.length){
			this.selectedSingle();
		}
	},
	checkSelected: function(){
		var selectedItem = this.selector.selectedItems.filter(function(item, index){
		    return item.data.id == this.data.id;
		}.bind(this));
		if (selectedItem.length){
			//selectedItem[0].item = this;
            selectedItem[0].addItem(this);
			this.selectedItem = selectedItem[0];
			this.setSelected();
		}
	},
	setSelected: function(){
        this.isSelected = true;
        this.node.setStyles(this.selector.css.selectorItem_selected);
        this.textNode.setStyles(this.selector.css.selectorItemTextNode_selected);
        this.actionNode.setStyles(this.selector.css.selectorItemActionNode_selected);
    },

	setEvent: function(){
		this.node.addEvents({
			"mouseover": function(){
				this.overItem();
			}.bind(this),
			"mouseout": function(){
				this.outItem();
			}.bind(this),
			"click": function(){
				this.clickItem();
			}.bind(this)
		});
	},
    clickItem: function(){
        if (this.selector.options.count.toInt()==1){
            this.selectedSingle();
        }else{
            if (this.isSelected){
                this.unSelected();
            }else{
                this.selected();
            }
        }
    },
    overItem: function(){
        if (!this.isSelected){
            this.node.setStyles(this.selector.css.selectorItem_over);
            this.actionNode.setStyles(this.selector.css.selectorItemActionNode_over);
        }
    },
    outItem: function(){
        if (!this.isSelected){
            this.node.setStyles(this.selector.css.selectorItem);
            this.actionNode.setStyles(this.selector.css.selectorItemActionNode);
        }
    },
	selectedSingle: function(){
        if (!this.isSelected){
            if (this.selector.currentItem) this.selector.currentItem.unSelectedSingle();
            this.selector.currentItem = this;
            this.isSelected = true;
            this.selector.selectedItems.push(this);
            this.node.setStyles(this.selector.css.selectorItem_selected);
            this.textNode.setStyles(this.selector.css.selectorItemTextNode_selected);
            this.actionNode.setStyles(this.selector.css.selectorItemActionNode_selected);
        }else {
            this.unSelectedSingle();
        }
	},
	unSelectedSingle: function(){
		this.selector.currentItem = null;
		this.isSelected = false;
		this.selector.selectedItems.erase(this);
		this.node.setStyles(this.selector.css.selectorItem);
		this.textNode.setStyles(this.selector.css.selectorItemTextNode);
		this.actionNode.setStyles(this.selector.css.selectorItemActionNode);
	},
	selected: function(){
		if ((this.selector.options.count==0) || (this.selector.selectedItems.length+1)<=this.selector.options.count){
			this.isSelected = true;
			this.node.setStyles(this.selector.css.selectorItem_selected);
			this.textNode.setStyles(this.selector.css.selectorItemTextNode_selected);
			this.actionNode.setStyles(this.selector.css.selectorItemActionNode_selected);
			
			this.selectedItem = this.selector._newItemSelected(this.data, this.selector, this);
            this.selectedItem.check();
			this.selector.selectedItems.push(this.selectedItem);
		}else{
			MWF.xDesktop.notice("error", {x: "right", y:"top"}, "最多可选择"+this.selector.options.count+"个选项", this.selector.node);
		}
	},
	unSelected: function(){
		this.isSelected = false;
		this.node.setStyles(this.selector.css.selectorItem);
		this.textNode.setStyles(this.selector.css.selectorItemTextNode);
		this.actionNode.setStyles(this.selector.css.selectorItemActionNode);
		
		if (this.selectedItem){
			this.selector.selectedItems.erase(this.selectedItem);

            this.selectedItem.items.each(function(item){
                if (item != this){
                    item.isSelected = false;
                    item.node.setStyles(this.selector.css.selectorItem);
                    item.textNode.setStyles(this.selector.css.selectorItemTextNode);
                    item.actionNode.setStyles(this.selector.css.selectorItemActionNode);
                }
            }.bind(this));

			this.selectedItem.destroy();
			this.selectedItem = null;
		}
	}
	
});

MWF.xApplication.Organization.Selector.Person.ItemSelected = new Class({
	Extends: MWF.xApplication.Organization.Selector.Person.Item,
	initialize: function(data, selector, item){
		this.data = data;
		this.selector = selector;
		this.container = this.selector.selectedNode;
		this.isSelected = false;
        this.items = [];
		if (item) this.items.push(item);
        this.level = 0;
		this.load();
	},
    clickItem: function(){
        if (this.items.length){
            this.items.each(function(item){
                item.unSelected();
            });
        }else{
            //this.item.selectedItem = null;
            //this.item.isSelected = false;
            this.destroy();
            this.selector.selectedItems.erase(this);
        }
    },
    overItem: function(){
        if (!this.isSelected){
            this.node.setStyles(this.selector.css.selectorItem_over);
            this.actionNode.setStyles(this.selector.css.selectorItemActionNode_selected_over);
        }
    },
    addItem: function(item){
        if (this.items.indexOf(item)==-1) this.items.push(item);
    },
	check: function(){
        if (this.selector.items.length){
            var items = this.selector.items.filter(function(item, index){
                return item.data.id == this.data.id;
            }.bind(this));
            this.items = items
            if (items.length){
                items.each(function(item){
                    item.selectedItem = this;
                    item.setSelected();
                }.bind(this));
            }
        }
    },
	destroy: function(){
		this.node.destroy();
		delete this;
	}
});

MWF.xApplication.Organization.Selector.Person.ItemCategory = new Class({
    Extends: MWF.xApplication.Organization.Selector.Person.Item,
    initialize: function(data, selector, container, level){
        this.data = data;
        this.selector = selector;
        this.container = container;
        this.isSelected = false;
        this.level = (level) ? level.toInt() : 1;
        this.load();
    },

    createNode: function(){
        this.node = new Element("div", {
            "styles": this.selector.css.selectorItemCategory
        }).inject(this.container);
    },

    load: function(){
        this.createNode();
        this.levelNode = new Element("div", {
            "styles": this.selector.css.selectorItemLevelNode
        }).inject(this.node);
        var indent = this.level*10;
        this.levelNode.setStyle("width", ""+indent+"px");

        this.iconNode = new Element("div", {
            "styles": this.selector.css.selectorItemIconNode
        }).inject(this.node);
        this._setIcon();

        this.actionNode = new Element("div", {
            "styles": (this.selector.options.expand) ? this.selector.css.selectorItemCategoryActionNode_expand : this.selector.css.selectorItemCategoryActionNode_collapse
        }).inject(this.node);

        this.textNode = new Element("div", {
            "styles": this.selector.css.selectorItemCategoryTextNode,
            "text": this._getShowName()
        }).inject(this.node);
        var m = this.textNode.getStyle("margin-left").toFloat()+indent;
        this.textNode.setStyle("margin-left", ""+m+"px");

        this.children = new Element("div", {
            "styles": this.selector.css.selectorItemCategoryChildrenNode,
        }).inject(this.node, "after");
        if (!this.selector.options.expand) this.children.setStyle("display", "none");

        var subIdList = this.selector._getChildrenItemIds(this.data);
        if (subIdList){
            var count = subIdList.length;
            this.childrenHeight = count*29;
            this.children.setStyle("height", ""+this.childrenHeight+"px");
        }
        if (!this._hasChild()){
            this.actionNode.setStyle("background", "transparent");
            this.textNode.setStyle("color", "#777");
        }

        this.setEvent();

        this.check();
    },
    clickItem: function(){
        if (this._hasChild()){
            if (!this.fx){
                this.fx = new Fx.Tween(this.children, {
                    "duration": 200
//                "transition": Fx.Transitions.Cubic.easeIn
                });
            };
            if (!this.fx.isRunning()){
                var display = this.children.getStyle("display");
                if (display == "none"){
                    this.children.setStyles({
                        "display": "block",
                        "height": "0px"
                    });
                    this.fx.start("height", "0px", ""+this.childrenHeight+"px");
                    this.actionNode.setStyles(this.selector.css.selectorItemCategoryActionNode_expand);

                }else{
                    if (!this.childrenHeight) this.childrenHeight = this.children.getStyle("height").toFloat();
                    this.fx.start("height", ""+this.childrenHeight+"px", "0px").chain(function(){
                        this.children.setStyles({
                            "display": "none",
                            "height": "0px"
                        });
                    }.bind(this));
                    this.actionNode.setStyles(this.selector.css.selectorItemCategoryActionNode_collapse);
                }
            }
        }
    },
    overItem: function(){
        //if (!this.isSelected){
        //    this.node.setStyles(this.selector.css.selectorItem_over);
        //    this.actionNode.setStyles(this.selector.css.selectorItemActionNode_over);
        //}
    },
    outItem: function(){
        //if (!this.isSelected){
        //    this.node.setStyles(this.selector.css.selectorItem);
        //    this.actionNode.setStyles(this.selector.css.selectorItemActionNode);
        //}
    },
    _hasChild: function(){
        var subIdList = this.selector._getChildrenItemIds(this.data);
        if (subIdList) if (subIdList.length) return true;
        return false;
    }
});
MWF.xApplication.Organization.Selector.Person.ItemGroupCategory = new Class({
    Extends: MWF.xApplication.Organization.Selector.Person.ItemCategory,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/groupicon.png)");
    }
});
MWF.xApplication.Organization.Selector.Person.ItemRoleCategory = new Class({
    Extends: MWF.xApplication.Organization.Selector.Person.ItemCategory,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/roleicon.png)");
    }
});
