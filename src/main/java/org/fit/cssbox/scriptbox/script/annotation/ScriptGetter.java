/**
 * ScriptGetter.java
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

package org.fit.cssbox.scriptbox.script.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.fit.cssbox.scriptbox.script.reflect.ClassMember;

/**
 * Annotation for methods which makes them visible from the script as a readable fields.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface ScriptGetter {
	public static final String ENUMERABLE_FIELD = ClassMember.ENUMERABLE;
	public static final String CALLABLE_GETTER = "callable";
	public static final String CALLABLE_ENUMERABLE_GETTER = "enum_callable";
	public static final String FIELD_GET_OVERRIDE = "field_override";
	public static final String EMPTY = "";
	public static final String[] DEFAULT_OPTIONS = {ENUMERABLE_FIELD, FIELD_GET_OVERRIDE};
	
	String[] engines() default {};
	String[] options() default {ENUMERABLE_FIELD, FIELD_GET_OVERRIDE};
	String field() default EMPTY;
}
