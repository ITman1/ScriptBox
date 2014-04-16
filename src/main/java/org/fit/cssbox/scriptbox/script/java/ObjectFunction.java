/**
 * ObjectFunction.java
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

package org.fit.cssbox.scriptbox.script.java;

import java.lang.reflect.Method;

public class ObjectFunction extends ObjectMember<ClassFunction, Method> implements MemberFunction {

	public ObjectFunction(Object object, ClassFunction classFunction) {
		super(object, classFunction);
	}
	
	public ObjectFunction(Object object, Method method) {
		this(object, new ClassFunction(object.getClass(), method));
	}
	
	@Override
	public Class<?>[] getParameterTypes() {
		return member.getParameterTypes();
	}
	
	public Object invoke(Object ...args) {		
		return classMember.invoke(object, args);
	}

}
