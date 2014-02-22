package org.fit.cssbox.scriptbox.dom.interfaces;

/**
 * 
 * FIXME: Add missing methods according to the HTML5.
 * 
 * @see http://www.whatwg.org/specs/web-apps/current-work/#the-iframe-element
 * @author Radim Loskot
 *
 */
public interface Html5IFrameElement extends Html5Element {
	public boolean getSeamless();
	public void setSeamless(boolean seamless);
}
