package org.fit.cssbox.scriptbox.url;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.fit.cssbox.scriptbox.url.URLUtilsHelper.UrlComponent;

/**
 * Implements abstract ParserURL class.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://url.spec.whatwg.org/#concept-url">URL</a>
 */
public class WrappedURL extends ParserURL {
	protected java.net.URL wrappedUrl;

	/**
	 * Constructs {@link ParserURL} from the URL.
	 * 
	 * @param wrappedUrl URL from which to create {@link ParserURL}.
	 */
	public WrappedURL(java.net.URL wrappedUrl) {
		this.wrappedUrl = wrappedUrl;
	}

	/**
	 * Returns wrapped URL.
	 * 
	 * @return Wrapped URL.
	 */
	public java.net.URL getWrappedURL() {
		return wrappedUrl;
	}
	
	@Override
	public String getScheme() {
		if (wrappedUrl == null) {
			return "";
		}
		
		return wrappedUrl.getProtocol();
	}

	@Override
	public void setScheme(String scheme) {
		if (wrappedUrl == null) {
			return;
		}
		
		wrappedUrl = URLUtilsHelper.setComponent(wrappedUrl, UrlComponent.PROTOCOL, scheme);
	}

	@Override
	public String getSchemeData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSchemeData(String schemeData) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getUsername() {
		if (wrappedUrl == null) {
			return "";
		}
		
		String userInfo = wrappedUrl.getUserInfo();
		
		String username = "";
		
		if (userInfo != null) {
			int colonPos = userInfo.indexOf(':');
			
			if (colonPos != -1) {
				username = userInfo.substring(0, colonPos);
			} else {
				username = userInfo;
			}
		}
		
		return username;
	}

	@Override
	public void setUsername(String username) {
		if (wrappedUrl == null) {
			return;
		}
		
		String userInfo = username + ":" + getPassword();
		
		wrappedUrl = URLUtilsHelper.setComponent(wrappedUrl, UrlComponent.USER_INFO, userInfo);
	}

	@Override
	public String getPassword() {
		if (wrappedUrl == null) {
			return "";
		}
		
		String userInfo = wrappedUrl.getUserInfo();

		String password = "";
		
		if (userInfo != null) {
			int colonPos = userInfo.indexOf(':');
			
			if (colonPos != -1) {
				password = userInfo.substring(colonPos + 1);
			}
		}
		
		return password;
	}

	@Override
	public void setPassword(String password) {
		if (wrappedUrl == null) {
			return;
		}
		
		String userInfo = getUsername() + ":" + password;
		
		wrappedUrl = URLUtilsHelper.setComponent(wrappedUrl, UrlComponent.USER_INFO, userInfo);
	}

	@Override
	public String getHost() {
		if (wrappedUrl == null) {
			return "";
		}
		
		return wrappedUrl.getHost();
	}

	@Override
	public void setHost(String host) {
		if (wrappedUrl == null) {
			return;
		}
		
		wrappedUrl = URLUtilsHelper.setComponent(wrappedUrl, UrlComponent.HOST, host);
	}

	@Override
	public String getPort() {
		if (wrappedUrl == null) {
			return "";
		}
		
		int port = wrappedUrl.getPort();
		
		return (port == -1)? "" : new Integer(port).toString();
	}

	@Override
	public void setPort(String port) {
		if (wrappedUrl == null) {
			return;
		}
		
		try {
			Integer portInteger = new Integer(port);
			wrappedUrl = URLUtilsHelper.setComponent(wrappedUrl, UrlComponent.PORT, portInteger);
		} catch (NumberFormatException e) {
			return;
		}	
	}

	@Override
	public List<String> getPath() {
		if (wrappedUrl == null) {
			return new ArrayList<String>();
		}
		
		String path = wrappedUrl.getPath();
		
		String[] pathArr = path.split("/");
		List<String> pathList = new ArrayList<String>();
		
		for (String pathElem : pathArr) {
			pathList.add(pathElem);
		}
		
		return pathList;
	}

	@Override
	public void setPath(List<String> path) {
		if (wrappedUrl == null) {
			return;
		}
		
		String pathString = StringUtils.join(path, "/");
		
		wrappedUrl = URLUtilsHelper.setComponent(wrappedUrl, UrlComponent.PATH, pathString);
	}

	@Override
	public String getQuery() {
		if (wrappedUrl == null) {
			return "";
		}
		
		return wrappedUrl.getQuery();
	}

	@Override
	public void setQuery(String query) {
		if (wrappedUrl == null) {
			return;
		}
		
		wrappedUrl = URLUtilsHelper.setComponent(wrappedUrl, UrlComponent.QUERY, query);
	}

	@Override
	public String getFragment() {
		if (wrappedUrl == null) {
			return "";
		}
		
		return wrappedUrl.getRef();
	}

	@Override
	public void setFragment(String fragment) {
		if (wrappedUrl == null) {
			return;
		}
		
		wrappedUrl = URLUtilsHelper.setComponent(wrappedUrl, UrlComponent.REF, fragment);
	}

	@Override
	public String serialize() {
		if (wrappedUrl == null) {
			return "";
		}
		
		return wrappedUrl.toString();
	}
	
	
}
