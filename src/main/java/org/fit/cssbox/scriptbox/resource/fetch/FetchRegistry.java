/**
 * FetchRegistry.java
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

package org.fit.cssbox.scriptbox.resource.fetch;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.resource.fetch.handlers.FileFetch;
import org.fit.cssbox.scriptbox.resource.fetch.handlers.HttpFetch;
import org.fit.cssbox.scriptbox.resource.fetch.handlers.HttpsFetch;
import org.fit.cssbox.scriptbox.resource.fetch.handlers.JavascriptFetch;

public class FetchRegistry {	
	
	static private FetchRegistry instance;

	private Map<String, Set<Class<? extends Fetch>>> registeredFetchHandlers;
	
	private FetchRegistry() {
		registeredFetchHandlers = new HashMap<String, Set<Class<? extends Fetch>>>();
		
		registerFetchHandler(FileFetch.class);
		registerFetchHandler(HttpFetch.class);
		registerFetchHandler(HttpsFetch.class);
		registerFetchHandler(JavascriptFetch.class);
	}
	
	public static synchronized FetchRegistry getInstance() {
		if (instance == null) {
			instance = new FetchRegistry();
		}
		
		return instance;
	}
	
	public boolean isFetchable(URL url) {
		String scheme = url.getProtocol();
		return registeredFetchHandlers.containsKey(scheme);
	}
	
	public Fetch getFetch(BrowsingContext sourceContext, BrowsingContext destinationContext, URL url) {
		return getFetchProtected(sourceContext, destinationContext, url, null, null, null, null);
	}
	
	public Fetch getFetch(BrowsingContext sourceContext, BrowsingContext destinationContext, URL url, boolean synchronous, boolean manualRedirect, boolean isSafe, Task onFinishTask) {
		return getFetchProtected(sourceContext, destinationContext, url, synchronous, manualRedirect, isSafe, onFinishTask);
	}
	
	protected Fetch getFetchProtected(BrowsingContext sourceContext, BrowsingContext destinationContext, URL url, Boolean synchronous, Boolean manualRedirect, Boolean isSafe, Task onFinishTask) {
		Set<Class<? extends Fetch>> fetchHandlers = registeredFetchHandlers.get(url.getProtocol());
		
		if (fetchHandlers != null) {
			for (Class<? extends Fetch> fetchClass : fetchHandlers) {
				Fetch fetch = null;
				
				if (synchronous != null) {
					fetch = instantizeFetch(fetchClass, sourceContext, destinationContext, url, synchronous, manualRedirect, isSafe, onFinishTask);
				} else {
					fetch = instantizeFetch(fetchClass, sourceContext, destinationContext, url);
				}
				
				if (fetch == null) {
					continue;
				}
				
				if (fetch.isValid()) {
					return fetch;
				} else {
					try {
						fetch.close();
					} catch (IOException e) {}
				}
			}
		}
		
		return null;
	}
	
	public void registerFetchHandler(Class<? extends Fetch> fetchClass) {
		String[] protocols = null;
		Annotation[] annotations = fetchClass.getAnnotations();

		for(Annotation annotation : annotations){
		    if(annotation instanceof FetchPreamble){
		    	FetchPreamble fetchPreamble = (FetchPreamble) annotation;
		        protocols = fetchPreamble.protocols();
		    }
		}
		
		if (protocols == null) {
			// TODO: Throw exception about missing annotation.
			return;
		}
		
		for (String protocol : protocols) {
			Set<Class<? extends Fetch>> fetchHandlers = registeredFetchHandlers.get(protocol);
			
			if (fetchHandlers == null) {
				fetchHandlers = new HashSet<Class<? extends Fetch>>();
			}
			
			fetchHandlers.add(fetchClass);
			registeredFetchHandlers.put(protocol, fetchHandlers);		
		}
	}
	
	private Fetch instantizeFetch(Class<? extends Fetch> fetchClass, BrowsingContext sourceContext, BrowsingContext destinationContext, URL url, boolean synchronous, boolean manualRedirect, boolean isSafe, Task onFinishTask) {
		Fetch fetch = null;
		
		try {
			Constructor<? extends Fetch> contructor = fetchClass.getConstructor(BrowsingContext.class, BrowsingContext.class, URL.class, boolean.class, boolean.class, boolean.class, Task.class);
			Object newInstance = contructor.newInstance(sourceContext, destinationContext, url, synchronous, manualRedirect, isSafe, onFinishTask);
			
			if (newInstance instanceof Fetch) {
				fetch = (Fetch)newInstance;
			}
		} catch (Exception e) {
		}
		
		return fetch;
	}
	
	private Fetch instantizeFetch(Class<? extends Fetch> fetchClass, BrowsingContext sourceContext, BrowsingContext destinationContext, URL url) {
		Fetch fetch = null;
		
		try {
			Constructor<? extends Fetch> contructor = fetchClass.getConstructor(BrowsingContext.class, BrowsingContext.class, URL.class);
			Object newInstance = contructor.newInstance(sourceContext, destinationContext, url);
			
			if (newInstance instanceof Fetch) {
				fetch = (Fetch)newInstance;
			}
		} catch (Exception e) {
		}
		
		return fetch;
	}
}
