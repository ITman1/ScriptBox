package org.fit.cssbox.scriptbox.exceptions;

public class LifetimeEndedException extends RuntimeException {
	
	private static final long serialVersionUID = -3834334259354157493L;

	public LifetimeEndedException() {}
	
	public LifetimeEndedException(String message) {
		super(message);
	}
}
