package org.fit.cssbox.scriptbox.history;

import java.net.URL;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;

public class SessionHistoryEntry {
	private URL _url;
	private String _title;
	private Html5DocumentImpl _document;
	private History _history;
	private int _scrollPositionX;
	private int _scrollPositionY;
	
	public URL getURL() {
		return _url;
	}
	
	public void setURL(URL url) {
		_url = url;
	}
	
	public Html5DocumentImpl getDocument() {
		return _document;
	}
	
	public void setSocument(Html5DocumentImpl document) {
		_document = document;
	}
	
	public int getScrollPositionX() {
		return _scrollPositionX;
	}
	
	public void setScrollPositionX(int scrollPositionX) {
		_scrollPositionX = scrollPositionX;
	}
	
	public int getScrollPositionY() {
		return _scrollPositionY;
	}
	
	public void setScrollPositionY(int scrollPositionY) {
		_scrollPositionY = scrollPositionY;
	}
	
	public String getTitle() {
		return _title;
	}
	
	public void setTitle(String title) {
		_title = title;
	}
	
	public History getHistory() {
		return _history;
	}
}
