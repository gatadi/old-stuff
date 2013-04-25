init = (function(){
	
	function _(string) { return string.toLocaleString();};


	function localizeTag(id) {
		elem = document.getElementById(id);
		elem.innerHTML = _(elem.innerHTML);
	}


	function getParameterValue(parameter) {
		parameter = parameter.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
		var regexS = "[\\?&]" + parameter + "=([^&#]*)";
		var regex = new RegExp(regexS);
		var results = regex.exec(window.location.href);
		if(results == null)
			return "";
		else
			return results[1];
	}

	
	return function(){
		var lang = getParameterValue("lang");
		if (lang != "") String.locale = lang;

		alert(_("Localizing the document title..."));
		document.title = _(document.title);

		alert(_("Localizing other HTML tags..."));
		localizeTag("headertext");
		localizeTag("subtitletext");
		localizeTag("showinenglish");
		localizeTag("showingerman");

		alert(_("Localizing done!"));
	};
	
})();