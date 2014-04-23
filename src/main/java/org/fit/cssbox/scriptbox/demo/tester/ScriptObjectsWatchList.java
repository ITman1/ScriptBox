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

package org.fit.cssbox.scriptbox.demo.tester;

import javax.script.ScriptException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import org.fit.cssbox.scriptbox.browser.BrowsingUnit;

/**
 * Class representing watch list which resolves passed variables inside
 * the given window JavaScript engine.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class ScriptObjectsWatchList extends ScriptObjectViewer {

	private static final long serialVersionUID = -9105904291337305707L;
	    
    protected DefaultMutableTreeNode watchedListRoot;
        
    /**
     * Constructs watch list.
     */
	public ScriptObjectsWatchList() {
	}
	
	/**
	 * Constructs watch list with the given browsing unit which will be used for
	 * retrieving the corresponding script engine used for the resolution.
	 * 
	 * @param browsingUnit Browsing unit which will be used for the variable resolution.
	 */
	public ScriptObjectsWatchList(BrowsingUnit browsingUnit) {
		this();
		setBrowsingUnit(browsingUnit);
	}
	
	/**
	 * Refreshes this watch - resolves passed variables inside script engine.
	 */
	@Override
	public void refresh() {
		updateScriptEngine();
		
		if (watchedListRoot == null) {
			watchedListRoot = new DefaultMutableTreeNode();
		}
		
		if (watchedListRoot.getChildCount() > 0) {
			ObjectFieldTreeNode child = (ObjectFieldTreeNode)watchedListRoot.getFirstChild();
			child.removeAllChildren();
			
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
	
	/**
	 * Adds variable into watch list.
	 * 
	 * @param variableName New variable to be added into watch list.
	 */
	public void addVariable(String variableName) {
		if (isIdentifier(variableName)) {
			ObjectFieldTreeNode newNode = new ObjectFieldTreeNode(variableName, new Object(), membersResolverFactory);
			watchedListRoot.add(newNode);
		}
		
		refresh();
	}
	
	/**
	 * Removes variable at the specified index.
	 * 
	 * @param index Index of the variable which should be removed.
	 */
	public void removeVariable(int index) {
		watchedListRoot.remove(index);
		
		refresh();
	}
	
	/**
	 * Removes node which should be removed from the watch list.
	 * 
	 * @param node Tree node to be removed from this watch list.
	 */
	public void removeVariable(MutableTreeNode node) {
		watchedListRoot.remove(node);
		
		refresh();
	}
	
	/*
	 * FIXME: Now it proceeds tests against Java identifiers names not identical as JavaScript names
	 */
	private boolean isIdentifier(String identifier) {
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
