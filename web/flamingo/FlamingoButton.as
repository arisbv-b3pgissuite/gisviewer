﻿/*-----------------------------------------------------------------------------
Copyright (C) 2006  Menko Kroeske

This file is part of Flamingo MapComponents.

Flamingo MapComponents is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
-----------------------------------------------------------------------------*/
class FlamingoButton extends MovieClip {
	private var mc:MovieClip;
	private var uplink:String;
	private var overlink:String;
	private var downlink:String;
	private var functions:Object;
	private var enabled:Boolean;
	private var applytarget:Object;
	function FlamingoButton(mc:MovieClip, uplink:String, overlink:String, downlink:String, hitlink:String, target:Object) {
		if (mc == undefined) {
			return;
		}
		if (uplink == undefined) {
			return;
		}
		this.uplink = uplink;
		this.mc = mc;
		this.overlink = overlink;
		this.downlink = downlink;
		this.applytarget = target;
		if (this.overlink == undefined) {
			this.overlink = this.uplink;
		}
		if (this.downlink == undefined) {
			this.downlink = this.uplink;
		}
		if (this.applytarget == undefined) {
			this.applytarget = this.mc._parent;
		}
		if (hitlink == undefined) {
			mc.attachMovie(uplink, "hit", 0, {_alpha:0});
		} else {
			//mc.attachMovie(hitlink, "hit", 0);
			mc.attachMovie(hitlink, "hit", 0, {_alpha:0});
		}
		this.enabled = true;
		var thisObj = this;
		var hit:Boolean;
		var pressed:Boolean;
		mc.attachMovie(uplink, "skin", 1);
		mc.hit.useHandCursor = false;
		mc.hit.onDragOver = function() {
			if (thisObj.enabled) {
				if (pressed) {
					mc.attachMovie(thisObj.downlink, "skin", 1);
				}
				thisObj.fire("onDragOver");
			}
		};
		mc.hit.onDragOut = function() {
			if (thisObj.enabled) {
				mc.attachMovie(thisObj.uplink, "skin", 1);
				thisObj.fire("onDragOut");
			}
		};
		mc.hit.onRelease = function() {
			if (thisObj.enabled) {
				pressed = false;
				mc.attachMovie(thisObj.overlink, "skin", 1);
				thisObj.fire("onRelease");
			}
		};
		mc.hit.onReleaseOutside = function() {
			if (thisObj.enabled) {
				pressed = false;
				thisObj.fire("onReleaseOutside");
			}
		};
		mc.hit.onRollOver = function() {
			if (thisObj.enabled) {
				hit = true;
				mc.attachMovie(thisObj.overlink, "skin", 1);
				thisObj.fire("onRollOver");
			}
		};
		mc.hit.onRollOut = function() {
			if (thisObj.enabled) {
				hit = false;
				mc.attachMovie(thisObj.uplink, "skin", 1);
				thisObj.fire("onRollOut");
			}
		};
		mc.hit.onPress = function() {
			if (thisObj.enabled) {
				pressed = true;
				mc.attachMovie(thisObj.downlink, "skin", 1);
				thisObj.fire("onPress");
			}
		};
	}
	function setFunction(f:Function, eventname:String) {
		if (this.functions == undefined) {
			this.functions = new Object();
		}
		if (eventname == undefined) {
			eventname = "onpress";
		}
		this.functions[eventname.toLowerCase()] = f;
	}
	function move(x:Number, y:Number) {
		if (not isNaN(x)) {
			this.mc._x = x;
		}
		if (not isNaN(y)) {
			this.mc._y = y;
		}
	}
	function press(unpresstime:Number) {
		if (arguments.caller != this.functions.onpress) {
			if (this.enabled) {
				this.mc.attachMovie(this.downlink, "skin", 1);
				this.fire("onPress");
				if (unpresstime == undefined) {
					unpresstime = 150;
				}
				_global['setTimeout'](this, 'unpress', unpresstime);
			}
		}
	}
	private function unpress() {
		this.mc.attachMovie(this.uplink, "skin", 1);
	}
	function release() {
		if (this.enabled) {
			if (arguments.caller != this.functions.onrelease) {
				this.mc.attachMovie(this.uplink, "skin", 1);
				this.fire("onRelease");
			}
		}
	}
	private function fire(eventname:String) {
		if (this[eventname] != undefined) {
			this[eventname].apply(this.applytarget);
		}
		if (this.functions[eventname.toLowerCase()] != undefined) {
			this.functions[eventname.toLowerCase()].apply(this.applytarget);
		}
	}
	function setEnabled(b:Boolean) {
		if (not b) {
			var w = this.mc._width;
			var h = this.mc._height;
			var d = this.mc.createEmptyMovieClip("disable", 4);
			with (d) {
				beginFill(0xffffff, 70);
				moveTo(0, 0);
				lineTo(w, 0);
				lineTo(w, h);
				lineTo(0, h);
				lineTo(0, 0);
				endFill();
			}
			this.mc.attachMovie(this.uplink, "skin", 1);
		} else {
			this.mc.disable.removeMovieClip();
		}
		this.enabled = b;
	}
	function getEnabled():Boolean {
		return (this.enabled);
	}
	function getWidth():Number {
		return (this.mc.skin._width);
	}
	function getHeight():Number {
		return (this.mc.skin._height);
	}
	function getLeft():Number {
		return (this.mc._x);
	}
	function getRight():Number {
		return (this.mc._x+this.mc.skin._width);
	}
	function getTop():Number {
		return (this.mc._y);
	}
	function getBottom():Number {
		return (this.mc._y+this.mc.skin._height);
	}
	function getXCenter():Number {
		return (this.mc._x+(this.mc.skin._width/2));
	}
	function getYCenter():Number {
		return (this.mc._y+(this.mc.skin._height/2));
	}
}
