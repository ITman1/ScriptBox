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