/**
 * UrlOrigin.java
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

	@Override
	public String toString() {
		String urlStr = "";
		
		if (_protocol != null) {
			urlStr += _protocol + "://";
		}
		
		if (_host != null) {
			urlStr += _host;
		}
		
		if (_port != -1) {
			urlStr += ":" + _port;
		}
		
		return urlStr;
	}
}
