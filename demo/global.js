var __times = new Object();
var __diffs = new Object();
var __wasUndefined = false;

if (console === undefined) {
	__wasUndefined = true;
	var console = new Object();
	console.log = new Function();
	console.info = new Function();
	console.warn = new Function();
	console.error = new Function();
	console.time = new Function();
	console.timeEnd = new Function();
}

if (__wasUndefined && !window.opera && !window.chrome && __debug !== undefined) {
	console.log = function (arg) {
		__debug(" LOG  - " + arg);
	}
	console.info = function (arg) {
		__debug(" INFO - " + arg);
	}
	console.warn = function (arg) {
		__debug(" WARN - " + arg);
	}
	console.error = function (arg) {
		__debug(" ERR - " + arg);
	}
	console.time = function (arg) {
		__debug(arg);
	}
	console.timeEnd = function (arg) {
		__debug(arg);
	}
}

if(window.opera || window.chrome || __wasUndefined){

	console.time = function (label) {
		__times[label] = new Date();
	}
	
	console.timeEnd = function (label) {
		if (__times[label] !== undefined) {
			var diff = new Date() - __times[label];
			
			if (__diffs[label] !== undefined) {
				diff = diff + __diffs[label];
			}
			
			console.log("Time for '" + label + "': " + diff + " ms");
		}
	}
	
	console.timeContinue = function (label) {
		__times[label] = new Date();
	}
	
	console.timePause = function (label) {
		var diff = 0;
		
		if (__times[label] !== undefined) {
			diff = new Date() - __times[label];
		} else {
			return
		}
		
		if (__diffs[label] === undefined) {
			__diffs[label] = 0;
		}
		
		__diffs[label] = __diffs[label] + diff;
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