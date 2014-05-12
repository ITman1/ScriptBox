package org.fit.cssbox.scriptbox.script.reflect;

/**
 * Interface for members that can be invoked - e.g. constructors and functions.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public interface InvocableMember<MemberType extends java.lang.reflect.Member> extends Member<MemberType> {
	/**
	 * Returns invokable parameters types.
	 * 
	 * @return Invokable parameters types.
	 */
	public Class<?>[] getParameterTypes();
}
