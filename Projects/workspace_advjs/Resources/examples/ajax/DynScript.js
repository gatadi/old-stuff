/*
 *  To use the library, call:
 *  	 DynScript.scriptTagRequest(config)
 * 
 * 		where config is an object that can contain or override any parameters desired
 * 		config object parameters include:
 * 			success - a function called called after a successful response
 * 			failure - a function that defines how an error response should be handled
 * 			url     - no default is supplied, you should supply one
 * 			callback- the name of the parameter that the server looks for
 */

DynScript = (function(){
			
		var _config = {url : null, 
				       success : function(data){ alert(data); }, 
				       failure : function() { alert("Error with script tag request."); },
				       callback : 'callback'}
	
		var success = _config.success;
		var scriptTag = null; 
		
		function JSONDynamicScriptTag(url) {
		    this.url = url; 
		    this.headTag = document.getElementsByTagName("head")[0];
			this.scriptTag = document.createElement("script");
		    this.scriptTag.setAttribute("type", "text/javascript");
		    this.scriptTag.setAttribute("src", this.url);
		    console.log('loading...' + new Date().getTime());
		    this.headTag.appendChild(this.scriptTag);
		    console.log('loaded...' + new Date().getTime());
		    
		}
		
		JSONDynamicScriptTag.prototype.removeScriptTag = function() {
		    	this.headTag.removeChild(this.scriptTag);  
		};
		
		return {
			scriptTagRequest : function(config){ 
				for(prop in _config) 					   			// copies default properties
					if(typeof config[prop] == "undefined")      	// from config into the user's xhrConfig
						config[prop] = _config[prop]; 

				config.url += ("&" + config.callback + "=DynScript._callback");
				
				success = config.success;
				scriptTag = new JSONDynamicScriptTag(config.url);
			},
		
			_callback : function(data) {
				success(data);
				//if (scriptTag) scriptTag.removeScriptTag();
				//scriptTag = null;
			}
		};
})();