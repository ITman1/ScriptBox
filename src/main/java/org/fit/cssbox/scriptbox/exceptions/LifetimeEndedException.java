/**
 * LifetimeEndedException.java
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

package org.fit.cssbox.scriptbox.exceptions;

/**
 * Exception that is thrown when task should be synchronously terminated.  
 * If this exception is not catched anywhere, then it is propagated into  
 * event loop where is handled.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class LifetimeEndedException extends RuntimeException {
	
	private static final long serialVersionUID = -3834334259354157493L;

	public LifetimeEndedException() {}
	
	public LifetimeEndedException(String message) {
		super(message);
	}
}
