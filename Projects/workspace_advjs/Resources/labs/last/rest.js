RestClient = (function(){
	
	function makeRequest(options){
		$.ajax(options);
	}
	
	return {
		retrieve: function(url, dataType, callback) {
			
			if (!(dataType === 'xml' || dataType === 'json'))
				return;
			
			var options = {
				url:  url,
				type: 'GET',
				headers: {'format': dataType, 'Origin': 'http://foo.com' },
				dataType: dataType,
				success: function(){
					callback.apply(this, arguments);
				}
			};
			
			makeRequest(options);
		},
		
		update: function(url, params, callback){
			var options = {
					url:  url,
					type: 'POST',
					data: params,
					dataType: 'xml',
					success: function(){
						callback.apply(this, arguments);
					}
				};
				
				makeRequest(options);
		},
		
		insert: function(url, params, callback){
			this.update(url, params, callback);
		},
		
		
		del: function(url, callback) {
			var options = {
					url:  url,
					type: 'DELETE',
					dataType: 'xml',
					success: function(){
						callback.apply(this, arguments);
					}
				};
				
			makeRequest(options);
		}
	};
	
})();