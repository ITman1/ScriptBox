package org.fit.cssbox.scriptbox.resource.fetch;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
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
	
	public Fetch getFetch(BrowsingContext context, URL url) {
		return getFetchProtected(context, url, null);
	}
	
	public Fetch getFetch(BrowsingContext context, URL url, boolean synchronous) {
		return getFetchProtected(context, url, synchronous);
	}
	
	protected Fetch getFetchProtected(BrowsingContext context, URL url, Boolean synchronous) {
		Set<Class<? extends Fetch>> fetchHandlers = registeredFetchHandlers.get(url.getProtocol());
		
		if (fetchHandlers != null) {
			for (Class<? extends Fetch> fetchClass : fetchHandlers) {
				Fetch fetch = null;
				
				if (synchronous != null) {
					fetch = instantizeFetch(fetchClass, context, url, synchronous);
				} else {
					fetch = instantizeFetch(fetchClass, context, url);
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
	
	private Fetch instantizeFetch(Class<? extends Fetch> fetchClass, BrowsingContext context, URL url, boolean synchronous) {
		Fetch fetch = null;
		
		try {
			Constructor<? extends Fetch> contructor = fetchClass.getConstructor(BrowsingContext.class, URL.class, boolean.class);
			Object newInstance = contructor.newInstance(context, url, synchronous);
			
			if (newInstance instanceof Fetch) {
				fetch = (Fetch)newInstance;
			}
		} catch (Exception e) {
		}
		
		return fetch;
	}
	
	private Fetch instantizeFetch(Class<? extends Fetch> fetchClass, BrowsingContext context, URL url) {
		Fetch fetch = null;
		
		try {
			Constructor<? extends Fetch> contructor = fetchClass.getConstructor(BrowsingContext.class, URL.class);
			Object newInstance = contructor.newInstance(context, url);
			
			if (newInstance instanceof Fetch) {
				fetch = (Fetch)newInstance;
			}
		} catch (Exception e) {
		}
		
		return fetch;
	}
}
