/**
 * UrlUtils.java
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

package org.fit.cssbox.scriptbox.url;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.UriBuilder;

public class URLUtilsHelper {
	public enum UrlComponent {
		PROTOCOL,
		HOST,
		PORT,
		PATH,
		QUERY,
		REF,
		USER_INFO,
		QUERY_PARAM
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
				builder.replaceQuery((String)arg1);
				break;
			case QUERY_PARAM:
				builder.queryParam((String)arg1, (String)arg2);
				break;
			case REF:
				builder.fragment((String)arg1);
				break;
			case USER_INFO:
				builder.userInfo((String)arg1);
				break;
			}
			
			
			url = builder.build().toURL();
		} catch (Exception e) {
			url = null;
		}
		
		return url;
	}
	
	static public boolean identicalComponents(URL url1, URL url2) {
		return identicalComponents(url1, url2, UrlComponent.PROTOCOL, UrlComponent.HOST, UrlComponent.PORT, UrlComponent.PATH, UrlComponent.QUERY, UrlComponent.REF);
	}
	
	static public boolean identicalComponents(URL url1, URL url2, UrlComponent ...components) {
		Set<UrlComponent> componentsSet = new HashSet<UrlComponent>();
		
		for (UrlComponent component : components) {
			componentsSet.add(component);
		}
		
		String protocol1 = url1.getProtocol();
		String protocol2 = url2.getProtocol();
		if (componentsSet.contains(UrlComponent.PROTOCOL) && (protocol1 != null || protocol2 != null)) {
			if ((protocol1 != null && protocol2 == null && !protocol1.equals(protocol2)) || 
					(protocol1 == null && protocol2 != null && !protocol2.equals(protocol1)) || !protocol2.equals(protocol1)) {
				return false;
			}
		}
		
		String host1 = url1.getHost();
		String host2 = url2.getHost();
		if (componentsSet.contains(UrlComponent.HOST) && (host1 != null || host2 != null)) {
			if ((host1 != null && host2 == null && !host1.equals(host2)) || 
					(host1 == null && host2 != null && !host2.equals(host1)) || !host2.equals(host1)) {
				return false;
			}
		}
		
		int port1 = url1.getPort();
		int port2 = url2.getPort();
		if (componentsSet.contains(UrlComponent.PORT) && port1 != port2) {
			return false;
		}
		
		String path1 = url1.getPath();
		String path2 = url2.getPath();
		if (componentsSet.contains(UrlComponent.PATH) && (path1 != null || path2 != null)) {
			if ((path1 != null && path2 == null && !path1.equals(path2)) || 
					(path1 == null && path2 != null && !path2.equals(path1)) || !path2.equals(path1)) {
				return false;
			}
		}
		
		String query1 = url1.getQuery();
		String query2 = url2.getQuery();
		if (componentsSet.contains(UrlComponent.QUERY) && (query1 != null || query2 != null)) {
			if ((query1 != null && query2 == null && !query1.equals(query2)) || 
					(query1 == null && query2 != null && !query2.equals(query1)) || !query2.equals(query1)) {
				return false;
			}
		}
		
		String ref1 = url1.getRef();
		String ref2 = url2.getRef();
		if (componentsSet.contains(UrlComponent.REF) && (ref1 != null || ref2 != null)) {
			if ((ref1 != null && ref2 == null && !ref1.equals(ref2)) || 
					(ref1 == null && ref2 != null && !ref2.equals(ref1)) || !ref2.equals(ref1)) {
				return false;
			}
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
