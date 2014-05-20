var _times = new Object();

if (console === undefined) {
	var console = new Object();
	console.log = new Function();
	console.info = new Function();
	console.warn = new Function();
	console.error = new Function();
}

if(window.opera || window.chrome){
	console.time = function (label) {
		_times[label] = new Date();
	}
	
	console.timeEnd = function (label) {
		if (_times[label] !== undefined) {
			var diff = new Date() - _times[label];
			console.log(diff + " ms");
		}
	}
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
	if (e.message == null) {
		console.error("[no message]");
	} else {
		console.error(e.message);
	}
}