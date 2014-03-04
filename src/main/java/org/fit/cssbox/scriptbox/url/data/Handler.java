package org.fit.cssbox.scriptbox.url.data;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler {
	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		return new UserURLConnection(url);
	}
	
	private static class UserURLConnection extends URLConnection {
		public UserURLConnection(URL url) {
			super(url);
		}
		
		@Override
		public void connect() throws IOException {
		}
		
		@Override
		public InputStream getInputStream() throws IOException {
			return null;
		}
	}
}