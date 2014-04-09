package org.fit.cssbox.scriptbox.demo;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.script.ScriptSettings;
import org.fit.cssbox.scriptbox.script.javascript.GlobalObjectJavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassMembersResolverFactory;

public class ScriptObjectViewer extends JTree {

	private static final long serialVersionUID = -7851448937001280871L;
	protected static final String NO_ROOT = "(no root)";

	private class MutableTreeExpansionListener implements TreeExpansionListener {

		@Override
		public void treeExpanded(TreeExpansionEvent event) {
			TreePath path = event.getPath();

			if (path.getPathCount() > 1) {
				ObjectFieldTreeNode expandedNode = (ObjectFieldTreeNode)path.getLastPathComponent();
				expandedNode.visit();
			}
			
			return;
		}

		@Override
		public void treeCollapsed(TreeExpansionEvent event) {

		}

	}
	
    protected DefaultMutableTreeNode rootNode;
    protected Object rootHostedObject;
    protected DefaultTreeModel treeModel;
    protected BrowsingUnit browsingUnit;
    protected ClassMembersResolverFactory membersResolverFactory;

	public ScriptObjectViewer() {
        addTreeExpansionListener(new MutableTreeExpansionListener());

        setRootVisible(false);
        setShowsRootHandles(true);
        
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setOpenIcon(null);
        renderer.setClosedIcon(null);
        setCellRenderer(renderer);
        
        refresh();
	}
	
	public ScriptObjectViewer(BrowsingUnit browsingUnit) {
		this();
		setBrowsingUnit(browsingUnit);
	}
	
	public void refresh() {
		if (rootHostedObject != null) {
			rootNode = new ObjectFieldTreeNode(rootHostedObject, membersResolverFactory);
		} else {
	        rootNode = new DefaultMutableTreeNode(NO_ROOT);
		}
		
		refresh(rootNode);
	}
	
	public void setBrowsingUnit(BrowsingUnit browsingUnit) {
		this.browsingUnit = browsingUnit;
		BrowsingContext context = browsingUnit.getWindowBrowsingContext();
		Html5DocumentImpl document = context.getActiveDocument();
		
		rootHostedObject = null;
		membersResolverFactory = null;
		if (document != null) {
			Window window = document.getWindow();
			ScriptSettings<?> settings = window.getScriptSettings();
			GlobalObjectJavaScriptEngine engine = (GlobalObjectJavaScriptEngine)settings.getExecutionEnviroment("text/javascript");

			rootHostedObject = window;
			membersResolverFactory = engine.getClassMembersResolverFactory();
		}
		

	}
	
	protected void refresh(DefaultMutableTreeNode rootNode) {
        treeModel = new DefaultTreeModel(rootNode);
        setModel(treeModel);
        expandPath(new TreePath(rootNode));
	}
}
