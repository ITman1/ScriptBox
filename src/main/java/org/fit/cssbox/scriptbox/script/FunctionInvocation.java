/**
 * FunctionInvocation.java
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

package org.fit.cssbox.scriptbox.script;

import java.net.URL;

/**
 * Interface that collects informations necessary for the function invocation.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public interface FunctionInvocation {
	
	/**
	 * Returns origin of this function.
	 * 
	 * @return Origin of this function.
	 */
	public URL getOrigin();
	
	/**
	 * Returns scope object of the function.
	 * 
	 * @return Scope object of the function.
	 */
	public Object getThiz();
	
	/**
	 * Returns name of the function.
	 * 
	 * @return Name of the function.
	 */
	public String getName();
	
	/**
	 * Returns invocation arguments.
	 * 
	 * @return Invocation arguments.
	 */
	public Object[] getArgs();
}
