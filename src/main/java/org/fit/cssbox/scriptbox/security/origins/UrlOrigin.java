package org.fit.cssbox.scriptbox.security.origins;

import java.net.URL;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UrlOrigin extends Origin<URL> {

	private int _port;
	private String _protocol;
	private String _host;
	
	public UrlOrigin(URL originSource) {
		super(originSource);
		
		_port = originSource.getPort();
		_protocol = originSource.getProtocol();
		_host = originSource.getHost();
	}

	@Override
	protected int originHashCode() {
		return new HashCodeBuilder(12, 32).
			append(_port).
			append(_protocol).
			append(_host).
			toHashCode();
	}

	@Override
	protected boolean originEquals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof UrlOrigin))
			return false;

		UrlOrigin rhs = (UrlOrigin) obj;
		return new EqualsBuilder().
			append(_port, rhs._port).
			append(_protocol, rhs._protocol).
			append(_host, rhs._host).
			isEquals();
	}

}
