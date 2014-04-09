package org.fit.cssbox.scriptbox.demo;

import java.awt.BorderLayout;

import javax.script.ScriptException;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.script.ScriptSettings;
import org.fit.cssbox.scriptbox.script.javascript.GlobalObjectJavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassMembersResolverFactory;

public class ScriptObjectsWatchList extends JPanel {

	private static final long serialVersionUID = -9105904291337305707L;

	private class WatchedScriptObjectsViewer extends ScriptObjectViewer {

		private static final long serialVersionUID = 3158933383091089783L;

		@Override
		public void refresh() {			
			refresh(watchedListRoot);
		}
	}
	
    protected BrowsingUnit browsingUnit;
    protected GlobalObjectJavaScriptEngine scriptEngine;
    protected ClassMembersResolverFactory membersResolverFactory;

    protected WatchedScriptObjectsViewer objectViewer;
    
    protected DefaultMutableTreeNode watchedListRoot;
        
	public ScriptObjectsWatchList() {
		watchedListRoot = new DefaultMutableTreeNode();
		
		setLayout(new BorderLayout(0, 0));
			
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		objectViewer = new WatchedScriptObjectsViewer();
		scrollPane.setViewportView(objectViewer);
	}
	
	public ScriptObjectsWatchList(BrowsingUnit browsingUnit) {
		this();
		setBrowsingUnit(browsingUnit);
	}
	
	public void refresh() {
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
		
		objectViewer.refresh();
	}
	
	public void setBrowsingUnit(BrowsingUnit browsingUnit) {
		this.browsingUnit = browsingUnit;
		this.scriptEngine = null;
		
		BrowsingContext context = browsingUnit.getWindowBrowsingContext();
		Html5DocumentImpl document = context.getActiveDocument();
		
		if (document != null) {
			Window window = document.getWindow();
			ScriptSettings<?> settings = window.getScriptSettings();
			
			scriptEngine = (GlobalObjectJavaScriptEngine)settings.getExecutionEnviroment("text/javascript");
			membersResolverFactory = scriptEngine.getClassMembersResolverFactory();
		}
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
