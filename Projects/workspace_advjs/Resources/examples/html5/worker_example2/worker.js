

function myFunc(data){
	postMessage(data);
}

onmessage = function(event) {
	importScripts(event.data);
}