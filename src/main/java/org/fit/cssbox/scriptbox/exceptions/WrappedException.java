package org.fit.cssbox.scriptbox.exceptions;

/**
 * This exception class wraps exception into runtime exception, 
 * so it can be propagated without throws keyword.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
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
