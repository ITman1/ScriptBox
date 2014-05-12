/**
 * ObjectFieldTreeNode.java
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

package org.fit.cssbox.scriptbox.demo.tester;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;

import org.fit.cssbox.scriptbox.script.reflect.ClassField;
import org.fit.cssbox.scriptbox.script.reflect.ClassMember;
import org.fit.cssbox.scriptbox.script.reflect.ClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.reflect.ObjectGetter;
import org.fit.cssbox.scriptbox.script.reflect.DefaultObjectMembers;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Function;

/**
 * Class representing tree node which is constructed from the given object.
 * Tree node has passed node name and has children nodes which are retrieved
 * by resolving the members of the passed object using class member factory.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class ObjectFieldTreeNode extends DefaultMutableTreeNode implements Comparable<ObjectFieldTreeNode> {

	private static final long serialVersionUID = 1996230762591708828L;
	private static final int DEFAULT_RECURSION = 2;

	private boolean childrenLoaded;
	private ClassMembersResolverFactory membersResolverFactory;

	private String fieldName;
	private Class<?> fieldType;
	private Object fieldValue;
	private Exception exception;
	
	private String fieldTypeStr;
	private String fieldValueStr;
	
	/**
	 * Constructs tree node with the default name "(root)" containing members 
	 * of the passed object resolved with class members resolver factory.
	 * 
	 * @param rootObject Object of which members should be resolved and put into tree node.
	 * @param membersResolverFactory Class member resolver factory used for resolvinf members of the passed object.
	 */
	public ObjectFieldTreeNode(Object rootObject, ClassMembersResolverFactory membersResolverFactory) {
		this("(root)", rootObject, membersResolverFactory, null);
	}
	
	/**
	 * Constructs tree node containing members of the passed object resolved 
	 * with class members resolver factory.
	 * 
	 * @param nodeName Name of this constructed node.
	 * @param rootObject Object of which members should be resolved and put into tree node.
	 * @param membersResolverFactory Class member resolver factory used for resolvinf members of the passed object.
	 */
	public ObjectFieldTreeNode(String nodeName, Object rootObject, ClassMembersResolverFactory membersResolverFactory) {
		this(nodeName, rootObject, membersResolverFactory, null);
	}
	
	/**
	 * Constructs tree node from the passed object and exception which might have occurred 
	 * while retrieving value of the passed object. It also creates children nodes which
	 * represents the members of the passed object resolved with class members resolver factory.
	 * 
	 * @param nodeName Name of this constructed node.
	 * @param rootObject Object of which members should be resolved and put into tree node.
	 * @param membersResolverFactory Class member resolver factory used for resolvinf members of the passed object.
	 * @param exception Exception which is passed if there was some exception while retrieving the objet value
	 */
	public ObjectFieldTreeNode(String nodeName, Object rootObject, ClassMembersResolverFactory membersResolverFactory, Exception exception) {
		this(membersResolverFactory, nodeName, rootObject.getClass(), rootObject, exception, DEFAULT_RECURSION);
		childrenLoaded = true;
	}
	
	private ObjectFieldTreeNode(ClassMembersResolverFactory membersResolverFactory, String fieldName, Class<?> fieldType, Object fieldValue, Exception exception, int recursion) {
		this.membersResolverFactory = membersResolverFactory;
		this.fieldName = fieldName;
		this.exception = exception;
		
		setNewFieldValue(fieldType, fieldValue, exception, recursion);		
	}
	
	public String getFieldName() {
		return fieldName;
	}

	public Class<?> getFieldType() {
		return fieldType;
	}

	public Object getFieldValue() {
		return fieldValue;
	}
	
	/**
	 * Sets new value for this node.
	 * 
	 * @param fieldType Type of member which will represent this node.
	 * @param fieldValue Value of the member which will represent this node.
	 * @param exception Exception which might have occurred which retrieving passed object.
	 */
	public void setNewFieldValue(Class<?> fieldType, Object fieldValue, Exception exception) {
		setNewFieldValue(fieldType, fieldValue, exception, DEFAULT_RECURSION);
	}
	
	private void setNewFieldValue(Class<?> fieldType, Object fieldValue, Exception exception, int recursion) {
		this.fieldType = fieldType;
		this.fieldValue = fieldValue;
		
		this.fieldTypeStr = (fieldType != null)? fieldType.getSimpleName() : null;
		this.fieldTypeStr = (fieldType == null && fieldValue != null)? fieldValue.getClass().getSimpleName() : this.fieldTypeStr;
		
		if (fieldValue instanceof Function) {
			if (fieldValue instanceof BaseFunction) {
				BaseFunction baseFunction = (BaseFunction)fieldValue;
				this.fieldValueStr = baseFunction.getFunctionName() + "()";
			} else {
				this.fieldValueStr = "f()";
			}
		} else if (fieldValue instanceof String) {
			this.fieldValueStr = "\"" + (String)fieldValue + "\"";
		} else if (fieldTypeStr != null) {
			try {
				this.fieldValueStr = (fieldValue != null)? fieldValue.toString() : "null";
			} catch (Exception e) {
				//e.printStackTrace();
				this.fieldValueStr = "(exceptiono occured)";
			}
		}
		
		constructObjectTree(this, fieldValue, recursion);
	}
	
	/**
	 * Visits this node. This method should be called when this node is walked inside tree.
	 * It ensures generating of the nested child tree nodes.
	 */
	public void visit() {
		if (getChildCount() > 0 && !childrenLoaded) {
			childrenLoaded = true;
			
			ObjectFieldTreeNode child = (ObjectFieldTreeNode)getFirstChild();
			
			do {
				constructObjectTree(child, child.fieldValue, 1);
				child = (ObjectFieldTreeNode)child.getNextSibling();
			} while (child != null);
		}
	}
	
	private void constructObjectTree(ObjectFieldTreeNode parentNode, Object object, int recursion) {
		if (recursion < 1 || object == null) {
			return;
		} else {
			recursion--;
		}
		
		DefaultObjectMembers objectMembers = DefaultObjectMembers.getObjectMembers(object, membersResolverFactory);
		Set<Entry<String, Set<ClassMember<?>>>> members = objectMembers.getNamedMemberEtrySet();
		List<ObjectFieldTreeNode> childrenList = new ArrayList<ObjectFieldTreeNode>();
		
		for (Entry<String, Set<ClassMember<?>>> member : members) {
			String fieldName = member.getKey();
			Set<ClassMember<?>> memberSet = member.getValue();
			
			
			if (memberSet.size() == 1) {
				ClassMember<?> classMember = memberSet.iterator().next();
				
				if (classMember instanceof ClassField) {
					ClassField classField = (ClassField)classMember;
					Class<?> fieldType = classField.getFieldType();
					Object fieldValue = null;
					Exception exception = null;
					try {
						fieldValue = classField.get(object);
					} catch (Exception e) {
						//e.printStackTrace();
						exception = e;
					}
					ObjectFieldTreeNode node = new ObjectFieldTreeNode(membersResolverFactory, fieldName, fieldType, fieldValue, exception, recursion);
					
					childrenList.add(node);
				}
			}
		}
		
		if (object instanceof ObjectGetter) {
			ObjectGetter objectWithGetter = (ObjectGetter)object;
			Collection<Object> keys = objectWithGetter.getKeys();
			
			for (Object key : keys) {
				Object fieldValue = null;
				Exception exception = null;
				try {
					fieldValue = objectWithGetter.get(key);
				} catch (Exception e) {
					//e.printStackTrace();
					exception = e;
				}

				Class<?> fieldType = fieldValue.getClass();
				ObjectFieldTreeNode node = new ObjectFieldTreeNode(membersResolverFactory, "[" + key.toString() + "]", fieldType, fieldValue, exception, recursion);
				
				childrenList.add(node);
			}
		}
		
		Collections.sort(childrenList);
		
		for (ObjectFieldTreeNode child : childrenList) {
			parentNode.add(child);
		}
		
	}
	
	@Override
	public String toString() {
		if (exception != null) {
			return fieldName + " - (exception occured)";
		} else if (fieldTypeStr == null) {
			return fieldName + " - (undefined property)";
		} else {
			return fieldName + ": " + fieldTypeStr + " = " + fieldValueStr;
		}

	}

	@Override
	public int compareTo(ObjectFieldTreeNode o) {
		return fieldName.compareTo(o.fieldName);
	}
} 
