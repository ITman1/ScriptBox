/**
 * MouseEventAdapter.java
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

package org.fit.cssbox.scriptbox.dom.events.adapters;

import org.apache.xerces.dom.events.MouseEventImpl;
import org.fit.cssbox.scriptbox.dom.events.script.AdaptedMouseEvent;
import org.fit.cssbox.scriptbox.script.adapter.Adapter;

/**
 * Adapter which adapts xerces implementation of the mouse event ({@link MouseEventImpl}) 
 * into script adapted mouse event ({@link AdaptedMouseEvent}).
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class MouseEventAdapter implements Adapter {

	@Override
	public Object getProvider(Object obj) {
		if (obj instanceof MouseEventImpl) {
			return new AdaptedMouseEvent<MouseEventImpl>((MouseEventImpl)obj);
		}
		return null;
	}

	@Override
	public Class<?> getAdapteeClass() {
		return MouseEventImpl.class;
	}

	@Override
	public Class<?> getResultClass() {
		return AdaptedMouseEvent.class;
	}

}
