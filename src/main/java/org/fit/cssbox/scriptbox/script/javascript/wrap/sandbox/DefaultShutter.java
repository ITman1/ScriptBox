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

package org.fit.cssbox.scriptbox.script.javascript.wrap.sandbox;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultShutter implements Shutter {
	class ClassModifiers {
		public ClassModifiers(boolean allMethods, boolean allFields) {
			this.allMethods = allMethods;
			this.allFields = allFields;
		}
		boolean allMethods;
		boolean allFields;
	}
	
	class FieldRecord {
		public FieldRecord(Class<?> type, Field field) {
			this.type = type;
			this.field = field;
		}
		Class<?> type;
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
	
	class FunctionRecord {
		public FunctionRecord(Class<?> type, Method method) {
			this.type = type;
			this.method = method;
			this.methodName = method.getName();
			this.methodParams = method.getParameterTypes();
		}
		Class<?> type;
		Method method;
		String methodName;
		Class<?>[] methodParams;
		
		@Override
		public int hashCode() {
			return methodName.hashCode() ^ Arrays.hashCode(methodParams);
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
					methodsEquals = type.getMethod(rhs.methodName, rhs.methodParams) != null;
				} catch (Exception e) {
				}
			}
			
			return hasSupperClass && methodsEquals;
		}
	}
	
	protected Map<Class<?>, ClassModifiers> visibleClasses;
	protected Set<FieldRecord> visibleFields;
	protected Set<FunctionRecord> visibleFunctions;
	
	public DefaultShutter() {
		visibleClasses = new HashMap<Class<?>, ClassModifiers>();
		visibleFields = new HashSet<FieldRecord>();
		visibleFunctions = new HashSet<FunctionRecord>();
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
	
	public void addVisibleClass(Class<?> type) {
		addVisibleClass(type, false, false);
	}
	
	public void addVisibleClass(Class<?> type, boolean allMethods, boolean allFields) {
		visibleClasses.put(type, new ClassModifiers(allMethods, allFields));
	}

	public void addVisibleField(Class<?> type, Field field) {
		FieldRecord classField = new FieldRecord(type, field);
		visibleFields.add(classField);
	}

	public void addVisibleMethod(Class<?> type, Method method) {
		FunctionRecord classFunction = new FunctionRecord(type, method);
		visibleFunctions.add(classFunction);
	}
	
	public void removeVisibleClass(Class<?> type) {
		visibleClasses.remove(type);
	}

	public void removeVisibleField(Class<?> type, Field field) {
		FieldRecord classField = new FieldRecord(type, field);
		visibleFields.remove(classField);
	}

	public void removeVisibleMethod(Class<?> type, Method method) {
		FunctionRecord classFunction = new FunctionRecord(type, method);
		visibleFunctions.remove(classFunction);
	}
	
	public boolean hasAllMethodsModifier(Class<?> type, Method method) {
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
	
	public boolean hasAllFieldsModifier(Class<?> type) {
		// FIXME: fix for superclass fields
		ClassModifiers modifiers = visibleClasses.get(type);
		return (modifiers != null)? modifiers.allFields : false;
	}

}
