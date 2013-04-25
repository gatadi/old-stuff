/*
 *  To use the library, call:
 *  	 XHRequest.createRequest(config)
 * 
 * 		where config is an object that can contain or override any parameters desired
 * 		config object parameters include:
 * 			success - a function that defines how a successful response should be handled
 * 			failure - a function that defines how an error response should be handled
 * 			method  - usually "GET" or "POST", defaults to get 
 * 			url     - no default is supplied, you should supply one
 * 			asynch  - set to true, usually don't need to override this
 * 			params  - a query string to be sent to the server * 
 * 		    a simple call may look like the following:		Cors.makeCorsRequest({url: "myURL", success: mySuccessFunction});
 */
var Cors = (function(){

	// any of these values can be overridden and tailored to your choosing
	var config =  {     
			success: function(xhr){alert(xhr.responseText);}, 
			failure: function(xhr, xhrConfig, error){alert(error.message);},
			method: "GET",
			url: null,
			asynch: true,
			params: null,
	}
	
	function buildCorsXhr(xhrConfig) {
		
		var xhr = new XMLHttpRequest();					
		if ("withCredentials" in xhr)										// most browsers
			xhr.open(xhrConfig.method, xhrConfig.url, xhrConfig.asynch);	
		else if (typeof XDomainRequest != "undefined")  					// IE8
			xhr = new XDomainRequest();
			xhr.open(xhrConfig.method, xhrConfig.url);
		else 
			xhr = null;
		
		return xhr;
		 
	}
	
	function processCorsXhr(xhr, xhrConfig) {
		var queryString = "";
		
		if (xhr == null) {
			if (console) console.log('xhr was null, request not made');
		}
		
		xhr.onload = function() {
			xhrConfig.success(xhr, xhrConfig);
			return;
		}
		
		xhr.onerror = function {
			xhrConfig.failure(xhr, xhrConfig, new Error("Error: " + xhr.status + " " + xhr.statusCode))
			return;
		}
		
		if(xhrConfig.params && xhrConfig.params.constructor == Object) { // if params is an object, convert it to a queryString
			queryString = convertToQueryString(xhrConfig.params);
		else if (typeof xhrConfig.params === 'string')
			queryString = xhrConfig.params;  // params was provided as a string
		
		if (xhrConfig.method && xhrConfig.method.toUpperCase() == "GET") {	
			
			var urlWithParams = xhrConfig.url;
			if (queryString.length > 0)		
				urlWithParams += ("?" + queryString);
 
			xhr.send(null);
		}
		else {
			xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); 
			xhr.send(queryString);
		}
	}
		
	function convertToQueryString(qStrObj) {
		
		var queryString = "", prop=null, oneProp="", notFirst=false;
		
		for(prop in qStrObj) {
			oneProp = "";
			
			if (notFirst) oneProp += "&";	
			notFirst = true;
			
			oneProp += encodeURI(prop);
			oneProp += "=";
			oneProp += encodeURI(qStrObj[prop]);
			
			queryString += oneProp;
		}
		
		return queryString
	}
	
	return {
		makeCorsRequest : function(xhrConfig) {
		
			for(prop in config) 					   			// copies default properties
				if(typeof xhrConfig[prop] == "undefined")      	// from config into the user's xhrConfig
					xhrConfig[prop] = config[prop]; 
			
			try {
				var xhr = buildCorsXhr(xhrConfig);
				processCorsXhr(xhr, xhrConfig);
			}
			catch(error) {
				xhrConfig.failure(xhr, xhrConfig, new Error("Error: " + error.name + "\n" + "Message: " +error.message));
			}
			
			return xhr;											// returned to user for development purposes
		}
	};
})();
