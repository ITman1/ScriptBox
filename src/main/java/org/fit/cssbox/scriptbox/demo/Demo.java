package org.fit.cssbox.scriptbox.demo;

import java.net.URL;

public class Demo {
	private String name;
	private URL url;
	
	public Demo(String name, URL url) {
		this.name = name;
		this.url = url;
	}
	
	public String getName() {
		return name;
	}

	public URL getUrl() {
		return url;
	}
}
