package org.fit.cssbox.scriptbox.script.reflect;

/**
 * Abstract class for representing the object members 
 * - class members which have an associated object.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class ObjectMembers extends ClassMembers {
	protected Object object;
	
	/**
	 * Constructs object members.
	 * 
	 * @param object Object that have members which represents this class.
	 */
	public ObjectMembers(Object object) {
		this.object = object;
	}
	
	/**
	 * Returns associated object.
	 * 
	 * @return Associated object with class members given by this class.
	 */
	public Object getObject() {
		return object;
	}
}
