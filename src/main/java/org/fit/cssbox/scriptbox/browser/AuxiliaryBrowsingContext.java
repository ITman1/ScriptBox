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
