package org.fit.cssbox.scriptbox.browser;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;

public class AuxiliaryBrowsingContext extends BrowsingContext {
	protected BrowsingContext openerBrowsingContext;
	protected Html5DocumentImpl creatorDocument;
	
	public AuxiliaryBrowsingContext(BrowsingUnit browsingUnit, BrowsingContext openerBrowsingContext, String name) {
		super(browsingUnit, name);

		this.openerBrowsingContext = openerBrowsingContext;
		
		BrowsingContext creatorContext = getCreatorContext();
		if (creatorContext != null) {
			this.creatorDocument = creatorContext.getActiveDocument();
		}
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
	
	public boolean hasCreatorDocument() {
		return getCreatorDocument() != null;
	}
	
	/*
	 * If a browsing context A has a creator browsing context, then the Document 
	 * that was the active document of that creator browsing context at the 
	 * time A was created is the creator Document.
	 */
	public Html5DocumentImpl getCreatorDocument() {
		return creatorDocument;
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
