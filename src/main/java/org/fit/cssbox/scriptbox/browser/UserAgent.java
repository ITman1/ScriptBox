/**
 * UserAgent.java
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.fit.cssbox.scriptbox.ui.BarProp;
import org.fit.cssbox.scriptbox.ui.ScrollBarsProp;
import org.fit.cssbox.scriptbox.url.UrlUtils;

public class UserAgent {
	static {
		UrlUtils.registerUrlHandlerPackage("org.fit.cssbox.scriptbox.url");
	}
	
	public static class NoBarProp extends BarProp {
		@ScriptGetter
		@Override
		public boolean getVisible() {
			return false;
		}
	}
	
	public static class NoScrollBarsProp extends ScrollBarsProp {
		@ScriptGetter
		@Override
		public boolean getVisible() {
			return false;
		}
		
		@Override
		public void scroll(int xCoord, int yCoord) {
		}

		@Override
		public boolean scrollToFragment(String fragment) {
			return true;
		}
	}
	
	private static BarProp noBarAvailable = new NoBarProp();
	private static NoScrollBarsProp noScrollBarsAvailable = new NoScrollBarsProp();
	
	private List<BrowsingUnit> _browsingUnits;
	
	public UserAgent() {
		_browsingUnits = new ArrayList<BrowsingUnit>();
	}
	
	public Set<BrowsingContext> getBrowsingContextsByName(String name) {
		return null;
	}
	
	public boolean scriptsSupported() {
		return true;
	}
	
	public boolean scriptsEnabled(URL page) {
		return true;
	}
	
	public boolean cookiesEnabled() {
		return true;
	}
	
	public BrowsingUnit openBrowsingUnit() {
		BrowsingUnit browsingUnit = new BrowsingUnit(this);
		
		_browsingUnits.add(browsingUnit);
		
		return browsingUnit;
	}
	
	public void destroyBrowsingUnit(BrowsingUnit browsingUnit) {
		if (_browsingUnits.remove(browsingUnit)) {
			browsingUnit.discard();
		}
	}
	
	public void stop() {
		while (!_browsingUnits.isEmpty()) {
			BrowsingUnit browsingUnit = _browsingUnits.get(0);
			destroyBrowsingUnit(browsingUnit);
		}
	}

	public BarProp getLocationbar() {
		return noBarAvailable;
	}

	public BarProp getMenubar() {
		return noBarAvailable;
	}

	public BarProp getPersonalbar() {
		return noBarAvailable;
	}

	public ScrollBarsProp getScrollbars() {
		return noScrollBarsAvailable;
	}

	public BarProp getStatusbar() {
		return noBarAvailable;
	}

	public BarProp getToolbar() {
		return noBarAvailable;
	}
}
