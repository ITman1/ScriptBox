package org.fit.cssbox.scriptbox.history;

import java.net.URL;

import org.fit.cssbox.scriptbox.document.script.ScriptableDocument;

public class SessionHistoryEntry {
	private URL _url;
	private String _title;
	private ScriptableDocument _document;
	private int _scrollPositionX;
	private int _scrollPositionY;
	
	public URL getURL() {
		return _url;
	}
	
	public void setURL(URL url) {
		this._url = url;
	}
	
	public ScriptableDocument getDocument() {
		return _document;
	}
	
	public void setSocument(ScriptableDocument document) {
		this._document = document;
	}
	
	public int getScrollPositionX() {
		return _scrollPositionX;
	}
	
	public void setScrollPositionX(int scrollPositionX) {
		this._scrollPositionX = scrollPositionX;
	}
	
	public int getScrollPositionY() {
		return _scrollPositionY;
	}
	
	public void setScrollPositionY(int scrollPositionY) {
		this._scrollPositionY = scrollPositionY;
	}
	
	public String getTitle() {
		return _title;
	}
	
	public void setTitle(String title) {
		this._title = title;
	}
}
