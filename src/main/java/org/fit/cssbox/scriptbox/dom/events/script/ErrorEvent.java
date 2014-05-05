/**
 * ErrorEvent.java
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

package org.fit.cssbox.scriptbox.dom.events.script;

import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;

/**
 * Represents pure script visible error event class.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#errorevent">Error event interface</a>
 */
public class ErrorEvent extends Event {

	protected String message;
	protected String filename;
	protected int lineno;
	protected int colno;
	protected Object error;
	
	/**
	 * Returns error message.
	 * 
	 * @return Error maessage.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#dom-errorevent-message">Error event's message</a>
	 */
	@ScriptGetter
	public String getMessage() {
		return message;
	}

	/**
	 * Returns filename.
	 * 
	 * @return Filename.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#dom-errorevent-filename">Error event's filename</a>
	 */
	@ScriptGetter
	public String getFilename() {
		return filename;
	}

	/**
	 * Return line number.
	 * 
	 * @return Line number.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#dom-errorevent-lineno">Error event's line number</a>
	 */
	@ScriptGetter
	public int getLineno() {
		return lineno;
	}

	/**
	 * Returns column number.
	 * 
	 * @return Column number.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#dom-errorevent-colno">Error event's column number</a>
	 */
	@ScriptGetter
	public int getColno() {
		return colno;
	}

	/**
	 * Returns error.
	 * 
	 * @return Error.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#dom-errorevent-error">Error event's error</a>
	 */
	@ScriptGetter
	public Object getError() {
		return error;
	}

	/**
	 * Initializes this event.
	 * 
	 * @param eventTypeArg event type
	 * @param canBubbleArg bubbles flag
	 * @param cancelableArg cancelable flag
	 * @param message Message
	 * @param filename Filename
	 * @param lineno Line number of the error
	 * @param colno Column number of the error
	 * @param error Error object
	 */
	@ScriptFunction
	public void initEvent(String eventTypeArg, boolean canBubbleArg, boolean cancelableArg, String message, String filename, int lineno, int colno, Object error) {
		super.initEvent(eventTypeArg, canBubbleArg, cancelableArg);
		
		this.message = message;
		this.filename = filename;
		this.lineno = lineno;
		this.colno = colno;
		this.error = error;
	}
	
	@ScriptFunction
	@Override
	public String toString() {
		return "[object ErrorEvent]";
	}
}
