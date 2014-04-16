package org.fit.cssbox.scriptbox.exceptions;

public class WrappedException extends RuntimeException {

	private static final long serialVersionUID = 6908801303890300658L;
	
	protected Exception exception;
	
	public WrappedException(Exception exception) {
		this.exception = exception;
	}
	
	public Exception unwrap() {
		return exception;
	}
}
