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

public class AuxiliaryBrowsingContext extends BrowsingContext {
	protected BrowsingContext openerBrowsingContext;
	
	public AuxiliaryBrowsingContext(BrowsingUnit browsingUnit, BrowsingContext openerBrowsingContext, String name) {
		super(browsingUnit, name);

		this.openerBrowsingContext = openerBrowsingContext;
	}

	public AuxiliaryBrowsingContext(BrowsingContext openerBrowsingContext) {
		this(openerBrowsingContext, null);
	}
	
	public AuxiliaryBrowsingContext(BrowsingContext openerBrowsingContext, String name) {
		this(openerBrowsingContext.browsingUnit, openerBrowsingContext, name);
	}
	
	/*
	 * An auxiliary browsing context has an opener browsing context, which is the browsing 
	 * context from which the auxiliary browsing context was created.
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
}
