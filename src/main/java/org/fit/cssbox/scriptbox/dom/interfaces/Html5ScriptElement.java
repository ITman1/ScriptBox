package org.fit.cssbox.scriptbox.dom.interfaces;

/**
 * 
 * @see http://dev.w3.org/html5/markup/script.html#script-interface
 * @author Radim Loskot
 *
 */
public interface Html5ScriptElement extends Html5Element {
	public String getSrc();
	public void  setSrc(String src);
	
	public boolean getAsync();
	public void setAsync(boolean async);
	
	public boolean getDefer();
	public void setDefer(boolean defer);
	
	public String getType();
	public void setType(String type);
	
	public String getCharset();
	public void setCharset(String charset);
	
	public String getText();
	public void setText(String text);	  
}
