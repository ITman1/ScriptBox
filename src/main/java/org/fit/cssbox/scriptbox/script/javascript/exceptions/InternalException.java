package org.fit.cssbox.scriptbox.script.javascript.exceptions;

public class InternalException extends RuntimeException {

	private static final long serialVersionUID = -8104961261059957898L;

	public InternalException(Exception e) {
		super(e);
	}
	
	public InternalException(String details) {
		super(details);
	}

}