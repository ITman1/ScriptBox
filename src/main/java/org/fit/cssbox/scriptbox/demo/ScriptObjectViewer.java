/**
 * ScriptObjectViewer.java
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
import org.fit.cssbox.scriptbox.script.javascript.JavaScriptEngine;
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
    protected GlobalObjectJavaScriptEngine scriptEngine;
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
		scriptEngine = null;
		membersResolverFactory = null;
		if (document != null) {
			Window window = document.getWindow();
			ScriptSettings<?> settings = window.getScriptSettings();
			
			scriptEngine = (GlobalObjectJavaScriptEngine)settings.getExecutionEnviroment(JavaScriptEngine.JAVASCRIPT_LANGUAGE);
			rootHostedObject = window;
			membersResolverFactory = scriptEngine.getClassMembersResolverFactory();
		}
		

	}
	
	protected void refresh(DefaultMutableTreeNode rootNode) {
        treeModel = new DefaultTreeModel(rootNode);
        
        /*TreePath rootNodePath = null;
        Enumeration<TreePath> expandedPaths = null;
        if (this.rootNode != null) {
            rootNodePath = new TreePath(this.rootNode);
            expandedPaths = getExpandedDescendants(rootNodePath);
        }*/

        setModel(treeModel);
        expandPath(new TreePath(rootNode));
	}
}
