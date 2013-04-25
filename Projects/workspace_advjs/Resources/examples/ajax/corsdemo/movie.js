(function(){
		
	Movie = function(name, synopsis, releaseYear, avgRating, rating, url) {
		this.url = url;
		this.mpaa_rating = rating;
		this.userRating = avgRating;
		this.releaseYear = releaseYear;
		this.synopsis = synopsis;
		this.title = name;
	}
	
	Movie.prototype = {
			
			htmlTemplate : '<h3>{title} <span class="release"> ({releaseYear}) </span></h3>' + 
						   '<div>' + 
						      '<span class="mpaa_rating">MPAA Rating: {mpaa_rating}</span>' + 
	                          '<span class="userRating">User Rating: {userRating}</span>' +
	                       '</div>' +
	                       '<p>{synopsis}</p><h6><a href="{url}">Get More Info</a></h6>',
	
	        getHTML : function() {
				var html = this.htmlTemplate;
				for (prop in this) {
					html = html.replace('{'+prop+'}', this[prop]);
				}
				return html;
			}
	};
})();