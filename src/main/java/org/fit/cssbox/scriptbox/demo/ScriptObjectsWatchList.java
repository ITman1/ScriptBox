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
