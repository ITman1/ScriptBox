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
import org.fit.cssbox.scriptbox.resource.fetch.handlers.FileFetchHandler;
import org.fit.cssbox.scriptbox.resource.fetch.handlers.HttpFetchHandler;
import org.fit.cssbox.scriptbox.resource.fetch.handlers.HttpsFetchHandler;
import org.fit.cssbox.scriptbox.resource.fetch.handlers.JavaScriptFetchHandler;

/**
 * Fetch registry which collects all kinds of the fetches.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class FetchRegistry {	
	
	static private FetchRegistry instance;

	private Map<String, Set<Class<? extends FetchHandler>>> registeredFetchHandlers;
	
	private FetchRegistry() {
		registeredFetchHandlers = new HashMap<String, Set<Class<? extends FetchHandler>>>();
		
		registerFetchHandler(FileFetchHandler.class);
		registerFetchHandler(HttpFetchHandler.class);
		registerFetchHandler(HttpsFetchHandler.class);
		registerFetchHandler(JavaScriptFetchHandler.class);
	}
	
	/**
	 * Returns instance of the fetch registry.
	 * 
	 * @return Instance of the fetch registry.
	 */
	public static synchronized FetchRegistry getInstance() {
		if (instance == null) {
			instance = new FetchRegistry();
		}
		
		return instance;
	}
	
	/**
	 * Tests whether is passed URL fetchable.
	 * 
	 * @param url URL to be tested.
	 * @return True if is passed URL fetchable, otherwise false.
	 */
	public boolean isFetchable(URL url) {
		String scheme = url.getProtocol();
		return registeredFetchHandlers.containsKey(scheme);
	}
	
	/**
	 * Returns fetch for a given parameters.
	 * 
	 * @param sourceContext Source browsing context which mediates the fetch.
	 * @param destinationContext Destination browsing context where is new resource being fetched.
	 * @param url URL which is being fetched.
	 * @return Constructed fetch if there exists appropriate fetch, otherwise null.
	 */
	public FetchHandler getFetch(BrowsingContext sourceContext, BrowsingContext destinationContext, URL url) {
		return getFetchPrivate(sourceContext, destinationContext, url, null, null, null, null);
	}
	
	/**
	 * Returns fetch for a given parameters.
	 * 
	 * @param sourceContext Source browsing context which mediates the fetch.
	 * @param destinationContext Destination browsing context where is new resource being fetched.
	 * @param url URL which is being fetched.
	 * @param synchronous If true, then fetching is blocking operation.
	 * @param manualRedirect If is set, then no redirecting will be processed.
	 * @param isSafe Should be set to true, if was invoked by user agent and is secure.
	 * @param onFinishTask Task that should be called after fetch is complete.
	 * @return Constructed fetch if there exists appropriate fetch, otherwise null.
	 */
	public FetchHandler getFetch(BrowsingContext sourceContext, BrowsingContext destinationContext, URL url, boolean synchronous, boolean manualRedirect, boolean isSafe, Task onFinishTask) {
		return getFetchPrivate(sourceContext, destinationContext, url, synchronous, manualRedirect, isSafe, onFinishTask);
	}
	
	/**
	 * Registers new fetch class.
	 * 
	 * @param fetchClass New fetch class.
	 */
	public void registerFetchHandler(Class<? extends FetchHandler> fetchClass) {
		String[] protocols = null;
		Annotation[] annotations = fetchClass.getAnnotations();

		for(Annotation annotation : annotations){
		    if(annotation instanceof FetchHandlerPreamble){
		    	FetchHandlerPreamble fetchPreamble = (FetchHandlerPreamble) annotation;
		        protocols = fetchPreamble.protocols();
		    }
		}
		
		if (protocols == null) {
			// TODO?: Throw exception about missing annotation.
			return;
		}
		
		for (String protocol : protocols) {
			Set<Class<? extends FetchHandler>> fetchHandlers = registeredFetchHandlers.get(protocol);
			
			if (fetchHandlers == null) {
				fetchHandlers = new HashSet<Class<? extends FetchHandler>>();
			}
			
			fetchHandlers.add(fetchClass);
			registeredFetchHandlers.put(protocol, fetchHandlers);		
		}
	}
	
	private FetchHandler instantizeFetch(Class<? extends FetchHandler> fetchClass, BrowsingContext sourceContext, BrowsingContext destinationContext, URL url, boolean synchronous, boolean manualRedirect, boolean isSafe, Task onFinishTask) {
		FetchHandler fetch = null;
		
		try {
			Constructor<? extends FetchHandler> contructor = fetchClass.getConstructor(BrowsingContext.class, BrowsingContext.class, URL.class, boolean.class, boolean.class, boolean.class, Task.class);
			Object newInstance = contructor.newInstance(sourceContext, destinationContext, url, synchronous, manualRedirect, isSafe, onFinishTask);
			
			if (newInstance instanceof FetchHandler) {
				fetch = (FetchHandler)newInstance;
			}
		} catch (Exception e) {
		}
		
		return fetch;
	}
	
	private FetchHandler instantizeFetch(Class<? extends FetchHandler> fetchClass, BrowsingContext sourceContext, BrowsingContext destinationContext, URL url) {
		FetchHandler fetch = null;
		
		try {
			Constructor<? extends FetchHandler> contructor = fetchClass.getConstructor(BrowsingContext.class, BrowsingContext.class, URL.class);
			Object newInstance = contructor.newInstance(sourceContext, destinationContext, url);
			
			if (newInstance instanceof FetchHandler) {
				fetch = (FetchHandler)newInstance;
			}
		} catch (Exception e) {
		}
		
		return fetch;
	}
	
	private FetchHandler getFetchPrivate(BrowsingContext sourceContext, BrowsingContext destinationContext, URL url, Boolean synchronous, Boolean manualRedirect, Boolean isSafe, Task onFinishTask) {
		Set<Class<? extends FetchHandler>> fetchHandlers = registeredFetchHandlers.get(url.getProtocol());
		
		if (fetchHandlers != null) {
			for (Class<? extends FetchHandler> fetchClass : fetchHandlers) {
				FetchHandler fetch = null;
				
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
}
