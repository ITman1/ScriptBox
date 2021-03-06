/**
 * JavaMember.java
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

package org.fit.cssbox.scriptbox.script.reflect;

/**
 * Interface for members of the classes/objects.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public interface JavaMember<MemberType extends java.lang.reflect.Member> {
	/**
	 * Returns class that owns this member.
	 * 
	 * @return Class that owns this member.
	 */
	public Class<?> getClazz();
	
	/**
	 * Returns associated Java member.
	 * 
	 * @return Associated Java member.
	 */
	public MemberType getMember();
	
	/**
	 * Returns name of this member.
	 * 
	 * @return Name of this member.
	 */
	public String getName();
	
	/**
	 * Tests whether has this member given option.
	 * 
	 * @param option Option that should be tested.
	 * @return True if has this member the given option, otherwise false.
	 */
	boolean hasOption(String option);
	
	/**
	 * Returns options.
	 * 
	 * @return Array with the options.
	 */
	String[] getOptions();
}
