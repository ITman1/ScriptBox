/**
 * WindowScriptSettings.java
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

package org.fit.cssbox.scriptbox.browser;

import java.net.URL;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.EventLoop;
import org.fit.cssbox.scriptbox.script.ScriptSettings;
import org.fit.cssbox.scriptbox.security.origins.Origin;

public class WindowScriptSettings extends ScriptSettings<Window> {

	private Window _window;
		
	public WindowScriptSettings(Window window) {
		_window = window;
	}
	
	public Window getWindow() {
		return _window;
	}

	@Override
	public Window getGlobalObject() {
		return _window;
	}

	@Override
	public BrowsingContext getResposibleBrowsingContext() {
		return _window.getDocumentImpl().getBrowsingContext();
	}

	@Override
	public Html5DocumentImpl getResponsibleDocument() {
		return _window.getDocumentImpl();
	}

	@Override
	public EventLoop getResposibleEventLoop() {
		return getResposibleBrowsingContext().getEventLoop();
	}

	@Override
	public Object getReferrerSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUrlCharacterEncoding() {
		return null;
	}

	@Override
	public URL getBaseUrl() {
		return getResposibleBrowsingContext().getBaseURL();
	}

	@Override
	public Origin<?> getOrigin() {
		return getResponsibleDocument().getOriginContainer().getOrigin();
	}

	@Override
	public Origin<?> getEffectiveScriptOrigin() {
		return getResponsibleDocument().getOriginContainer().getEffectiveScriptOrigin();
	}
}
