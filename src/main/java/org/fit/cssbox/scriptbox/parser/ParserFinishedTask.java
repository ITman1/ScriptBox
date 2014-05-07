/**
 * ParserFinishedTask.java
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

package org.fit.cssbox.scriptbox.parser;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;

/**
 * Task class from which should derive every task 
 * called by a parser after it finishes parsing.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class ParserFinishedTask extends Task {
	/**
	 * This field might be automatically injected by a parser.
	 * It is stored here an exception which occured while parsing the Document.
	 */
	protected Exception exception;

	public ParserFinishedTask(TaskSource source, Html5DocumentImpl document) {
		super(source, document);
	}
	
	public ParserFinishedTask(TaskSource source, Html5DocumentImpl document, Exception exception) {
		this(source, document);
		
		this.exception = exception;
	}

	/**
	 * Returns parser exception.
	 * 
	 * @return Parsing exception if there is any.
	 */
	public Exception getException() {
		return exception;
	}
}
