/**
 * DefaultShutter.java
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents the default implementation of the Shutter.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class DefaultShutter implements Shutter {
	private class ClassModifiers {
		public ClassModifiers(boolean allMethods, boolean allFields, boolean allCostructors) {
			this.allMethods = allMethods;
			this.allFields = allFields;
			this.allCostructors = allCostructors;
		}
		boolean allMethods;
		boolean allFields;
		boolean allCostructors;
	}
	
	private class FieldRecord {
		public FieldRecord(Class<?> type, Field field) {
			this.type = type;
			this.field = field;
		}
		@SuppressWarnings("unused")
		Class<?> type;
		@SuppressWarnings("unused")
		Field field;
		
		@Override
		public int hashCode() {
			// TODO: Implement, without this searching will not work.
			return super.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			// TODO: Implement, without this searching will not work.
			return super.equals(obj);
		}
	}
	
	private class ConstructorRecord extends CallableRecord {
		public ConstructorRecord(Class<?> type, Constructor<?> constructor) {
			super(type, constructor, constructor.getName(), constructor.getParameterTypes());
		}
	}
	
	private class FunctionRecord extends CallableRecord {
		public FunctionRecord(Class<?> type, Method method) {
			super(type, method, method.getName(), method.getParameterTypes());
		}
	}
	
	private class CallableRecord {
		public CallableRecord(Class<?> type, Member member, String memberName, Class<?>[] memberParams) {
			this.type = type;
			this.member = member;
			this.memberName = memberName;
			this.memberParams = memberParams;
		}
		Class<?> type;
		@SuppressWarnings("unused")
		Member member;
		String memberName;
		Class<?>[] memberParams;
		
		@Override
		public int hashCode() {
			return memberName.hashCode() ^ Arrays.hashCode(memberParams);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			if (obj == this)
				return true;
			if (!(obj instanceof FunctionRecord))
				return false;

			FunctionRecord rhs = (FunctionRecord) obj;

			boolean methodsEquals = false;
			boolean hasSupperClass = rhs.type.isAssignableFrom(type);
			
			if (hasSupperClass) {
				try {
					methodsEquals = type.getMethod(rhs.memberName, rhs.memberParams) != null;
				} catch (Exception e) {
				}
			}
			
			return hasSupperClass && methodsEquals;
		}
	}
	
	protected Map<Class<?>, ClassModifiers> visibleClasses;
	protected Set<FieldRecord> visibleFields;
	protected Set<FunctionRecord> visibleFunctions;
	protected Set<ConstructorRecord> visibleConstructors;
	
	public DefaultShutter() {
		visibleClasses = new HashMap<Class<?>, ClassModifiers>();
		visibleFields = new HashSet<FieldRecord>();
		visibleFunctions = new HashSet<FunctionRecord>();
		visibleConstructors = new HashSet<DefaultShutter.ConstructorRecord>();
	}
	
	@Override
	public boolean isClassVisible(Class<?> type) {
		return visibleClasses.containsKey(type);
	}

	@Override
	public boolean isFieldVisible(Class<?> type, Field field) {
		FieldRecord classField = new FieldRecord(type, field);
		return visibleFields.contains(classField) || hasAllFieldsModifier(type);
	}

	@Override
	public boolean isMethodVisible(Class<?> type, Method method) {
		FunctionRecord classFunction = new FunctionRecord(type, method);
		return visibleFunctions.contains(classFunction) || hasAllMethodsModifier(type, method);
	}
	
	@Override
	public boolean isConstructorVisible(Class<?> type, Constructor<?> constructor) {
		ConstructorRecord classConstructor = new ConstructorRecord(type, constructor);
		return visibleConstructors.contains(classConstructor) || hasAllConstructorsModifier(type, constructor);
	}
	
	/**
	 * Adds visible class.
	 * 
	 * @param type Class that should be visible from now.
	 */
	public void addVisibleClass(Class<?> type) {
		addVisibleClass(type, false, false, false);
	}
	
	/**
	 * Adds visible class.
	 * 
	 * @param type Class that should be visible from now.
	 * @param allMethods Include all methods
	 * @param allFields Include all fields
	 * @param allCostructors Include all constructors
	 */
	public void addVisibleClass(Class<?> type, boolean allMethods, boolean allFields, boolean allCostructors) {
		visibleClasses.put(type, new ClassModifiers(allMethods, allFields, allCostructors));
	}

	/**
	 * Adds visible field.
	 * 
	 * @param type Class that contains given field.
	 * @param field Field that should be visible from now.
	 */
	public void addVisibleField(Class<?> type, Field field) {
		FieldRecord classField = new FieldRecord(type, field);
		visibleFields.add(classField);
	}

	/**
	 * Adds visible method.
	 * 
	 * @param type Class that contains given method.
	 * @param method Method that should be visible from now.
	 */
	public void addVisibleMethod(Class<?> type, Method method) {
		FunctionRecord classFunction = new FunctionRecord(type, method);
		visibleFunctions.add(classFunction);
	}
	
	/**
	 * Adds visible constructor.
	 * 
	 * @param type Class that contains given constructor.
	 * @param constructor Constructor that should be visible from now.
	 */
	public void addVisibleMethod(Class<?> type, Constructor<?> constructor) {
		ConstructorRecord classFunction = new ConstructorRecord(type, constructor);
		visibleConstructors.add(classFunction);
	}
	
	/**
	 * Removes visible class.
	 * 
	 * @param type Class that should not be visible from now.
	 */
	public void removeVisibleClass(Class<?> type) {
		visibleClasses.remove(type);
	}

	/**
	 * Removes visible field.
	 * 
	 * @param type Class that contains given field.
	 * @param field Field that should not be visible from now.
	 */
	public void removeVisibleField(Class<?> type, Field field) {
		FieldRecord classField = new FieldRecord(type, field);
		visibleFields.remove(classField);
	}

	/**
	 * Removes visible method.
	 * 
	 * @param type Class that contains given method.
	 * @param method Method that should not be visible from now.
	 */
	public void removeVisibleMethod(Class<?> type, Method method) {
		FunctionRecord classFunction = new FunctionRecord(type, method);
		visibleFunctions.remove(classFunction);
	}
	
	/**
	 * Removes visible constructor.
	 * 
	 * @param type Class that contains given constructor.
	 * @param constructor Constructor that should not be visible from now.
	 */
	public void removeVisibleConstructor(Class<?> type, Constructor<?> constructor) {
		ConstructorRecord classFunction = new ConstructorRecord(type, constructor);
		visibleConstructors.remove(classFunction);
	}
	
	private boolean hasAllMethodsModifier(Class<?> type, Method method) {
		ClassModifiers modifiers = null;
		while (type != null && ((modifiers = visibleClasses.get(type)) == null)) {
			type = type.getSuperclass();
		}
		if (modifiers != null) {
			try {
				return modifiers.allMethods && type.getMethod(method.getName(), method.getParameterTypes()) != null;
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	private boolean hasAllConstructorsModifier(Class<?> type, Constructor<?> constructor) {
		ClassModifiers modifiers = null;
		while (type != null && ((modifiers = visibleClasses.get(type)) == null)) {
			type = type.getSuperclass();
		}
		if (modifiers != null) {
			try {
				return modifiers.allCostructors && type.getConstructor(constructor.getParameterTypes()) != null;
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	private boolean hasAllFieldsModifier(Class<?> type) {
		// FIXME: fix for superclass fields
		ClassModifiers modifiers = visibleClasses.get(type);
		return (modifiers != null)? modifiers.allFields : false;
	}

}
