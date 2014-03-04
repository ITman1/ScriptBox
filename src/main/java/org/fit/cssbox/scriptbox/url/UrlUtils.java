package org.fit.cssbox.scriptbox.url;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

public class UrlUtils {
	public enum UrlComponent {
		PROTOCOL,
		HOST,
		PORT,
		PATH,
		QUERY,
		REF
	};
	
	static public URL setComponent(URL url, UrlComponent compoenent, Object arg1) {
		return setComponent(url, compoenent, arg1, null);
	}
	
	static public URL setComponent(URL url, UrlComponent compoenent, Object arg1, Object arg2) {
		UriBuilder builder;
		try {
			builder = UriBuilder.fromUri(url.toURI());
			
			switch (compoenent) {
			case PROTOCOL:
				builder.scheme((String)arg1);
				break;
			case HOST:
				builder.host((String)arg1);
				break;
			case PORT:
				builder.port((Integer)arg1);
				break;
			case PATH:
				builder.path((String)arg1);
				break;
			case QUERY:
				builder.queryParam((String)arg1, (String)arg2);
				break;
			case REF:
				builder.fragment((String)arg1);
				break;
			}
			
			
			url = builder.build().toURL();
		} catch (IllegalArgumentException e) {
		} catch (URISyntaxException e) {
		} catch (UriBuilderException e) {
		} catch (MalformedURLException e) {
		}
		
		return url;
	}
	
	static public boolean identicalComponents(URL url1, URL url2) {
		return identicalComponents(url1, url2, UrlComponent.PROTOCOL, UrlComponent.HOST, UrlComponent.PORT, UrlComponent.PATH, UrlComponent.QUERY, UrlComponent.REF);
	}
	
	static public boolean identicalComponents(URL url1, URL url2, UrlComponent ...components) {
		Set<UrlComponent> componentsSet = new HashSet<UrlComponent>();
		
		if (componentsSet.contains(UrlComponent.PROTOCOL) && !url1.getProtocol().equals(url2.getProtocol())) {
			return false;
		}
		
		if (componentsSet.contains(UrlComponent.HOST) && !url1.getHost().equals(url2.getHost())) {
			return false;
		}
		
		if (componentsSet.contains(UrlComponent.PORT) && url1.getPort() != url2.getPort()) {
			return false;
		}
		
		if (componentsSet.contains(UrlComponent.PATH) && !url1.getPath().equals(url2.getPath())) {
			return false;
		}
		
		if (componentsSet.contains(UrlComponent.QUERY) && !url1.getQuery().equals(url2.getQuery())) {
			return false;
		}
		
		if (componentsSet.contains(UrlComponent.REF) && !url1.getRef().equals(url2.getRef())) {
			return false;
		}
		
		return true;
	}
	

	public static boolean registerUrlHandlerPackage(String handlerPackageName) {
		try{
			String handlerPkgs = System.getProperty("java.protocol.handler.pkgs");
		      if ((handlerPkgs != null) && !(handlerPkgs.isEmpty())){
		    	  handlerPkgs = handlerPkgs + "|" + handlerPackageName;
		      }
		      System.setProperty("java.protocol.handler.pkgs",handlerPackageName);
		    }
		    catch(Exception e){
		      return false;
		    }
		return true;
	}
}
