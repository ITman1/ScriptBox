package org.fit.cssbox.scriptbox.script.exceptions;

public class DocumentScriptException extends Exception {

	private static final long serialVersionUID = 1L;

	public DocumentScriptException() {
		super();
	}

	public DocumentScriptException(String message) {
		super(message);
	}

	public DocumentScriptException(String message, Throwable cause) {
		super(message, cause);
	}

	public DocumentScriptException(Throwable cause) {
		super(cause);
	}

	protected DocumentScriptException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
