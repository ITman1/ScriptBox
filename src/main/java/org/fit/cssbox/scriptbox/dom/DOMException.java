package org.fit.cssbox.scriptbox.dom;

public class DOMException extends org.w3c.dom.DOMException {

	private static final long serialVersionUID = -7487087556447991435L;
	
	public static final short SECURITY_ERR = 18;
	public static final short NETWORK_ERR = 19;
	public static final short ABORT_ERR = 20;
	public static final short URL_MISMATCH_ERR = 21;
	public static final short QUOTA_EXCEEDED_ERR = 22;
	public static final short TIMEOUT_ERR = 23;
	public static final short INVALID_NODE_TYPE_ERR = 24;
	public static final short DATA_CLONE_ERR = 25;

	public DOMException(short code, String message) {
		super(code, message);
	}
}
