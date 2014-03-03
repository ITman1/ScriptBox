package org.fit.cssbox.scriptbox.exceptions;

public class TaskAbortedException extends Exception {

	private static final long serialVersionUID = -9164037566180025543L;

	public TaskAbortedException() {
		super();
	}
	
	public TaskAbortedException(String message) {
		super(message);
	}
}
