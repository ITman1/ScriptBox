/**
 * SessionHistoryEntry.java
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

package org.fit.cssbox.scriptbox.history;

import java.net.URL;
import java.util.Date;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.security.origins.DocumentOrigin;

public class SessionHistoryEntry {
	private URL _url;
	private String _title;
	private Html5DocumentImpl _document;
	private History _history;
	private int _scrollPositionX;
	private int _scrollPositionY;
	
	private Date _visitedDate;
	private SessionHistory _sessionHistory;
	private String _browsingContextName;
	private StateObject _stateObject;
	
	public SessionHistoryEntry(SessionHistory sessionHistory) {
		_sessionHistory = sessionHistory;
	}
		
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
	
	public Date getVisited() {
		return _visitedDate;
	}
	
	public void setVisited(Date date) {
		_visitedDate = date;
	}
	
	public SessionHistory getSessionHistory() {
		return _sessionHistory;
	}
	
	public String getBrowsingContextName() {
		return _browsingContextName;
	}
	
	public void setBrowsingContextName(String name) {
		_browsingContextName = name;
	}
	
	public boolean hasSameDocumentOrigin(SessionHistoryEntry entry) {
		Html5DocumentImpl entryDocument = entry.getDocument();
		DocumentOrigin entryEntryDocumentOrigin = entryDocument.getOriginContainer().getOrigin();
		DocumentOrigin thisDocumentOrigin = _document.getOriginContainer().getOrigin();
		
		if (thisDocumentOrigin != null && thisDocumentOrigin.equals(entryEntryDocumentOrigin)) {
			return true;
		}
		
		return false;
	}
	
	// FIXME: Implement.
	public boolean hasPersistedUserState() {
		return false;
	}
	
	// FIXME: Implement.
	public boolean hasStateObject() {
		return false;
	}
	
	public StateObject getStateObject() {
		return _stateObject;
	}
}
