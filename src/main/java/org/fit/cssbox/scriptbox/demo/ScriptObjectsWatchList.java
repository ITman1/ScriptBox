/**
 * ScriptObjectsWatchList.java
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

package org.fit.cssbox.scriptbox.demo;

import javax.script.ScriptException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import org.fit.cssbox.scriptbox.browser.BrowsingUnit;

public class ScriptObjectsWatchList extends ScriptObjectViewer {

	private static final long serialVersionUID = -9105904291337305707L;
	    
    protected DefaultMutableTreeNode watchedListRoot;
        
	public ScriptObjectsWatchList() {
	}
	
	public ScriptObjectsWatchList(BrowsingUnit browsingUnit) {
		this();
		setBrowsingUnit(browsingUnit);
	}
	
	@Override
	public void refresh() {
		if (watchedListRoot == null) {
			watchedListRoot = new DefaultMutableTreeNode();
		}
		
		if (watchedListRoot.getChildCount() > 0) {
			ObjectFieldTreeNode child = (ObjectFieldTreeNode)watchedListRoot.getFirstChild();
			
			do {
				String fieldName = child.getFieldName();
				
				Object fieldValue = null;
				Class<?> fieldType = null;
				Exception exception = null;
				try {
					fieldValue = scriptEngine.eval(fieldName);
					fieldType = fieldValue.getClass();
				} catch (ScriptException e) {
					exception = e;
				}
				
				child.setNewFieldValue(fieldType, fieldValue, exception);
				
				child = (ObjectFieldTreeNode)child.getNextSibling();
			} while (child != null);
		}
	
		super.refresh(watchedListRoot);
	}
	
	public void addVariable(String variableName) {
		if (isIdentifier(variableName)) {
			ObjectFieldTreeNode newNode = new ObjectFieldTreeNode(variableName, new Object(), membersResolverFactory);
			watchedListRoot.add(newNode);
		}
		
		refresh();
	}
	
	public void removeVariable(int index) {
		watchedListRoot.remove(index);
		
		refresh();
	}
	
	public void removeVariable(MutableTreeNode node) {
		watchedListRoot.remove(node);
		
		refresh();
	}
	
	/*
	 * FIXME: Tests against Java identifiers names not JavaScript names
	 */
	protected boolean isIdentifier(String identifier) {
		char[] c = identifier.toCharArray();
		
		if (!Character.isJavaIdentifierStart(c[0])) {
			return false;
		}

		for (int i = 1; i < c.length; i++) {
			if (!Character.isJavaIdentifierPart(c[i])) {
				return false;
			}
		}

		return true;
	}

}
