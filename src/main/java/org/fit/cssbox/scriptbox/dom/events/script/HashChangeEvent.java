package org.fit.cssbox.scriptbox.dom.events.script;

import org.fit.cssbox.scriptbox.dom.events.EventTarget;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;

/*
 * http://www.w3.org/html/wg/drafts/html/CR/browsers.html#hashchangeevent
 */
public class HashChangeEvent extends TrustedEvent {
	protected String oldURL;
	protected String newURL;
	
	@ScriptGetter
	public String getOldURL() {
		return oldURL;
	}
	
	@ScriptGetter
	public String getNewURL() {
		return newURL;
	}

	@ScriptFunction
	public void initEvent(String eventTypeArg, boolean canBubbleArg, boolean cancelableArg, boolean isTrusted, EventTarget targetOverride, String oldURL, String newURL) {
		super.initEvent(eventTypeArg, canBubbleArg, cancelableArg, isTrusted, targetOverride);
		
		this.oldURL = oldURL;
		this.oldURL = oldURL;
	}
}
