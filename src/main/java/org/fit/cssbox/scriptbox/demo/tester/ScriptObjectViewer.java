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

package org.fit.cssbox.scriptbox.demo.tester;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.script.ScriptSettings;
import org.fit.cssbox.scriptbox.script.javascript.WindowJavaScriptEngine;
import org.fit.cssbox.scriptbox.script.reflect.ClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.window.Window;
import org.fit.cssbox.scriptbox.window.WindowProxy;

/**
 * Class representing tree component which inspects passed given 
 * hosted JavaScript object and displays all its members as children nodes.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
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
    protected WindowProxy rootHostedObject;
    protected DefaultTreeModel treeModel;
    protected BrowsingUnit browsingUnit;
    protected WindowJavaScriptEngine scriptEngine;
    protected ClassMembersResolverFactory membersResolverFactory;

    /**
     * Constructs viewer tree for JavaScript object.
     */
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
	
	/**
	 * Constructs viewer for the global object of the window browsing context
	 * of the given browsing unit.
	 * 
	 * @param browsingUnit Browsing unit which will be used for retrieving the global object.
	 */
	public ScriptObjectViewer(BrowsingUnit browsingUnit) {
		this();
		setBrowsingUnit(browsingUnit);
	}
	
	/**
	 * Constructs or updates new updated tree.
	 */
	public void refresh() {
		if (rootHostedObject != null) {
			rootNode = new ObjectFieldTreeNode(rootHostedObject, membersResolverFactory);
		} else {
	        rootNode = new DefaultMutableTreeNode(NO_ROOT);
		}
		
		refresh(rootNode);
	}
	
	/**
	 * Sets new browsing unit and refreshes this tree.
	 * 
	 * @param browsingUnit New browsing unit to which should be associated this tree.
	 */
	public void setBrowsingUnit(BrowsingUnit browsingUnit) {
		this.browsingUnit = browsingUnit;
		
		updateScriptEngine();
	}
	
	/**
	 * Retrieves script engine from the associated browsing unit.
	 */
	protected void updateScriptEngine() {
		if (browsingUnit != null) {
			BrowsingContext context = browsingUnit.getWindowBrowsingContext();
			Html5DocumentImpl document = context.getActiveDocument();
			
			rootHostedObject = null;
			scriptEngine = null;
			membersResolverFactory = null;
			if (document != null) {
				Window window = document.getWindow();
				ScriptSettings<?> settings = window.getScriptSettings();
				
				scriptEngine = (WindowJavaScriptEngine)settings.getExecutionEnviroment(WindowJavaScriptEngine.JAVASCRIPT_LANGUAGE);
				rootHostedObject = context.getWindowProxy();
				membersResolverFactory = scriptEngine.getClassMembersResolverFactory(); // FIXME?: We are supposing that this does not changes in other contexts
			}
		}
	}
	
	/**
	 * Refreshes passed tree node.
	 * 
	 * @param rootNode Tree node to be refreshed.
	 */
	protected void refresh(DefaultMutableTreeNode rootNode) {
		updateScriptEngine();
		
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
