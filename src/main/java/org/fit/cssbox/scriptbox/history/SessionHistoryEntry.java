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
import org.fit.cssbox.scriptbox.url.URLUtilsHelper;
import org.fit.cssbox.scriptbox.url.URLUtilsHelper.UrlComponent;

/**
 * Class collecting all informations which are being stored into session history.
 * 
 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#session-history-entry">Session history entry</a>
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class SessionHistoryEntry {
	private boolean _defaultEntry;
	
	private URL _url;
	private String _title;
	private Html5DocumentImpl _document;
	
	private boolean _pushStateTitleOrigin;
	private PersistedUserState _persistedUserState;
	private Date _visitedDate;
	private SessionHistory _sessionHistory;
	private String _browsingContextName;
	private StateObject _stateObject;
	
	private SessionHistoryEntry _pushedEntry;
	
	public SessionHistoryEntry(SessionHistory sessionHistory) {
		this(sessionHistory, false);
	}
	
	public SessionHistoryEntry(SessionHistory sessionHistory, boolean defaultEntry) {
		_sessionHistory = sessionHistory;
		_defaultEntry = defaultEntry;
	}
	
	/**
	 * Tests whether is this session history entry contiguous with the specified entry.
	 * Contiguous entries that differ just by fragment identifier also share the same Document.
	 * 
	 * @param entry Session history entry against which to be tested.
	 * 
	 * @return True if is passed entry contiguous with this session entry.
	 */
	public boolean isContiguous(SessionHistoryEntry entry) {
		boolean identicalUrls = URLUtilsHelper.identicalComponents(_url, entry._url, 
				UrlComponent.PROTOCOL, UrlComponent.HOST, UrlComponent.PORT, 
				UrlComponent.PATH, UrlComponent.QUERY);
		
		return _document == entry._document && identicalUrls;
	} 
		
	/**
	 * Tests whether is this session history entry created by default - after
	 * session history is initialized.
	 * 
	 * @return True if is session history entry is default, otherwise false.
	 */
	public boolean isDefaultEntry() {
		return _defaultEntry;
	}
	
	/**
	 * Returns associated URL.
	 * 
	 * @return Associated URL.
	 */
	public URL getURL() {
		return _url;
	}
	
	/**
	 * Sets new associated URL.
	 * 
	 * @param url New associated URL.
	 */
	public void setURL(URL url) {
		_url = url;
	}
	
	/**
	 * Returns associated document.
	 * 
	 * @return Associated document.
	 */
	public Html5DocumentImpl getDocument() {
		return _document;
	}
	
	/**
	 * Sets new associated document.
	 * 
	 * @param document New associated document.
	 */
	public void setDocument(Html5DocumentImpl document) {
		_document = document;
	}
	
	/**
	 * Returns associated title.
	 * 
	 * @return Associated title.
	 */
	public String getTitle() {
		return _title;
	}
	
	/**
	 * Sets new associated title.
	 * 
	 * @param title New associated title.
	 * @param pushStateTitleOrigin True if is this title set by a {@link History#pushState(StateObject, String, String)}.
	 */
	public void setTitle(String title, boolean pushStateTitleOrigin) {
		_title = title;
		
		_pushStateTitleOrigin = pushStateTitleOrigin;
	}
	
	/**
	 * Tests whether was title set by a {@link History#pushState(StateObject, String, String)}.
	 * @return True if is this title set by a {@link History#pushState(StateObject, String, String)}, otherwise false.
	 */
	public boolean hasPushedStateTitle() {
		return _pushStateTitleOrigin;
	}
	
	/**
	 * Sets new associated title.
	 * 
	 * @param title New associated title.
	 */
	public void setTitle(String title) {
		setTitle(title, false);
	}
	
	/**
	 * Returns date when was this session history entry visited.
	 * 
	 * @return Date of the visiting this session history entry.
	 */
	public Date getVisited() {
		return _visitedDate;
	}
	
	/**
	 * Sets new date when was this session history entry visited.
	 * 
	 * @param date New data when was this session history entry visited.
	 */
	public void setVisited(Date date) {
		_visitedDate = date;
	}
	
	/**
	 * Returns associated session history.
	 * 
	 * @return Associated session history.
	 */
	public SessionHistory getSessionHistory() {
		return _sessionHistory;
	}
	
	/**
	 * Returns associated browsing context name.
	 * 
	 * @return Associated browsing context name.
	 */
	public String getBrowsingContextName() {
		return _browsingContextName;
	}
	
	/**
	 * Sets new associated browsing context name.
	 * 
	 * @param name New associated browsing context name.
	 */
	public void setBrowsingContextName(String name) {
		_browsingContextName = name;
	}
	
	/**
	 * Tests whether have two compared session history entries the same document origin.
	 * 
	 * @param entry Session history entry against which to be tested.
	 * @return True if have two session history document the same origins.
	 */
	public boolean hasSameDocumentOrigin(SessionHistoryEntry entry) {
		Html5DocumentImpl entryDocument = entry.getDocument();
		DocumentOrigin entryEntryDocumentOrigin = entryDocument.getOriginContainer().getOrigin();
		DocumentOrigin thisDocumentOrigin = _document.getOriginContainer().getOrigin();
		
		if (thisDocumentOrigin != null && thisDocumentOrigin.equals(entryEntryDocumentOrigin)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Tests whether has this session history entry persisted user state.
	 * 
	 * @return True if has this session history entry persisted user state.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#an-entry-with-persisted-user-state">An entry with persisted user state</a>
	 */
	public boolean hasPersistedUserState() {
		return _persistedUserState != null;
	}
	
	/**
	 * Tests whether has this session history entry associated state object.
	 * 
	 * @return True if has this session history entry associated state object.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#state-object">State object</a>
	 */
	public boolean hasStateObject() {
		return _stateObject != null;
	}
	
	/**
	 * Returns state object associated with this session history entry.
	 * 
	 * @return State object associated with this session history entry.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#state-object">State object</a>
	 */
	public StateObject getStateObject() {
		return _stateObject;
	}
	
	/**
	 * Sets new state object for this session history entry.
	 * 
	 * @param stateObject New state object for this session history entry.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#state-object">State object</a>
	 */
	public void setStateObject(StateObject stateObject) {
		_stateObject = stateObject;
	}
	
	/**
	 * Updates settings in user persisted state.
	 * 
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#an-entry-with-persisted-user-state">An entry with persisted user state</a>
	 */
	public void updatePersistedUserState() {
		if (_persistedUserState == null && PersistedUserState.shouldPersist(_document)) {
			_persistedUserState = new PersistedUserState(_document);
		}
	
		if (_persistedUserState != null) {
			_persistedUserState.updateState();
		}
	}
	
	/**
	 * Applies settings from the user persisted state.
	 * 
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#an-entry-with-persisted-user-state">An entry with persisted user state</a>
	 */
	public void applyPersistedUserState() {	
		if (_persistedUserState != null) {
			_persistedUserState.applyState();
		}
	}

	/**
	 * Returns user persisted state associated with this session history entry.
	 * 
	 * @return User persisted state associated with this session history entry.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#an-entry-with-persisted-user-state">An entry with persisted user state</a>
	 */
	public PersistedUserState getPersistedUserState() {
		return _persistedUserState;
	}

	/**
	 * Sets new state object for this session history entry.
	 * 
	 * @param stateObject New state object for this session history entry.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#an-entry-with-persisted-user-state">An entry with persisted user state</a>
	 */
	public void setPpersistedUserState(PersistedUserState persistedUserState) {
		this._persistedUserState = persistedUserState;
	}

	/**
	 * Returns entry that was pushed from this entry.
	 * 
	 * @return Entry that was pushed from this entry
	 */
	public SessionHistoryEntry getPushedEntry() {
		return _pushedEntry;
	}

	/**
	 * Sets new entry that pushed this entry.
	 * 
	 * @param pushedEntry Entry that pushed this entry.
	 */
	public void setPushedEntry(SessionHistoryEntry pushedEntry) {
		this._pushedEntry = pushedEntry;
	}
}
