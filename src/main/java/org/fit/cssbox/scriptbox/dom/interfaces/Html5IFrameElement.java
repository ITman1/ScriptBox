package org.fit.cssbox.scriptbox.dom.interfaces;

/**
 * 
 * @see http://www.whatwg.org/specs/web-apps/current-work/#the-iframe-element
 * @author Radim Loskot
 *
 */
public interface Html5IFrameElement extends Html5Element {
	public boolean getSeamless();
	public void setSeamless(boolean seamless);
}
