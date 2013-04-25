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
				headers: {'format': dataType },
				dataType: dataType,
				success: function(){
					callback.apply(this, arguments);
				}
			};
			
			makeRequest(options);
		}
	};
	
})();