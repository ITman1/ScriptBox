/**
 * AuxiliaryBrowsingContext.java
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

package org.fit.cssbox.scriptbox.browser;

/**
 * Class representing auxiliary browsing contexts constructed e.g. by scripts
 * without being nested through an element.
 *
 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#auxiliary-browsing-context">Auxiliary browsing contexts</a>
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class AuxiliaryBrowsingContext extends BrowsingContext {
	/**
	 * Specifies whether this context has been created by a script or not.
	 */
	protected boolean createdByScript;
	
	/**
	 * Browsing context from which the auxiliary browsing context was created.
	 * 
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#opener-browsing-context">Opener browsing context</a>
	 */
	protected BrowsingContext openerBrowsingContext;
	
	/**
	 * Constructs new top-level auxiliary browsing context.
	 * 
	 * @param browsingUnit Browsing unit to which belongs this browsing context.
	 * @param openerBrowsingContext Browsing context from which the auxiliary browsing context was created.
	 * @param name Name of the browsing context.
	 * @param createdByScript Specifies whether this context has been created by a script or not.
	 */
	public AuxiliaryBrowsingContext(BrowsingUnit browsingUnit, BrowsingContext openerBrowsingContext, String name, boolean createdByScript) {
		super(browsingUnit, name);

		this.openerBrowsingContext = openerBrowsingContext;
		this.createdByScript = createdByScript;
	}

	public AuxiliaryBrowsingContext(BrowsingContext openerBrowsingContext) {
		this(openerBrowsingContext, null);
	}
	
	public AuxiliaryBrowsingContext(BrowsingContext openerBrowsingContext, String name) {
		this(openerBrowsingContext.browsingUnit, openerBrowsingContext, name, true);
	}
	
	/**
	 * Returns browsing context from which the auxiliary browsing context was created.
	 * 
	 * @return Browsing context from which the auxiliary browsing context was created.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#opener-browsing-context">Opener browsing context</a>
	 */
	public BrowsingContext getOpenerContext() {
		return openerBrowsingContext;
	}
		
	@Override
	public BrowsingContext getCreatorContext() {
		BrowsingContext creatorContext = super.getCreatorContext();
		// if the browsing context has an opener browsing context, 
		// then that is its creator browsing context.
		if (creatorContext == null) {
			creatorContext = openerBrowsingContext;
		}
		
		return creatorContext;
	}
	
	@Override
	public boolean isFamiliarWith(BrowsingContext context) {
		boolean isFamiliar = super.isFamiliarWith(context);
		
		if (!isFamiliar) {
			BrowsingContext a = this;
			AuxiliaryBrowsingContext b = null;
			
			if (context instanceof AuxiliaryBrowsingContext) {
				b = (AuxiliaryBrowsingContext)context;
			}
			
			if (b != null && a.isFamiliarWith(b.getOpenerContext())) {
				isFamiliar = true;
			}
		}
		
		return isFamiliar;
	}
	
	/*
	 * http://www.w3.org/html/wg/drafts/html/master/browsers.html#script-closable
	 */
	@Override
	public boolean isScriptClosable() {
		if (!createdByScript) {
			return super.isScriptClosable();
		}
	
		return true;
	}
}
