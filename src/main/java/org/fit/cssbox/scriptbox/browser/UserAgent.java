package org.fit.cssbox.scriptbox.browser;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.fit.cssbox.scriptbox.url.UrlUtils;

public class UserAgent {
	static {
		UrlUtils.registerUrlHandlerPackage("org.fit.cssbox.scriptbox.url");
	}
	
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
	
	public boolean scriptsEnabled(URI page) {
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
			browsingUnit.destroy();
		}
	}
	
	public void stop() {
		while (!_browsingUnits.isEmpty()) {
			BrowsingUnit browsingUnit = _browsingUnits.get(0);
			destroyBrowsingUnit(browsingUnit);
		}
	}
}
