if (console === undefined) {
	var console = new Object();
	console.log = new Function();
	console.info = new Function();
	console.warn = new Function();
	console.error = new Function();
}

function go_back() {
	console.info("called go_back()");
	
	window.history.back();
}

function go_forward() {
	console.info("called go_forward()");
	
	window.history.forward();
}

onerror = function (e) {
	console.error(e.message);
}