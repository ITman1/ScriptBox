/**
 * DOMException.java
 * (c) Radim Loskot and Radek Burget, 2013-2014
 *
 * ScriptBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ScriptBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with ScriptBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.fit.cssbox.scriptbox.dom;

/**
 * Extends DOM3 DOMException about new state codes.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://www.w3.org/TR/dom/#exception-domexception">Exception DOMException</a>
 */
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
