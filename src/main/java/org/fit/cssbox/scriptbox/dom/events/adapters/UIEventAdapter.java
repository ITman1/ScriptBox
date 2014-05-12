/**
 * UIEventAdapter.java
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

import org.apache.xerces.dom.events.UIEventImpl;
import org.fit.cssbox.scriptbox.dom.events.script.AdaptedUIEvent;
import org.fit.cssbox.scriptbox.script.adapter.Adapter;

/**
 * Adapter which adapts xerces implementation of the UI event ({@link UIEventImpl}) 
 * into script adapted UI event ({@link AdaptedUIEvent}).
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class UIEventAdapter implements Adapter {
	@Override
	public Object getProvider(Object obj) {
		if (obj instanceof UIEventImpl) {
			return new AdaptedUIEvent<UIEventImpl>((UIEventImpl)obj);
		}
		return null;
	}

	@Override
	public Class<?> getAdapteeClass() {
		return UIEventImpl.class;
	}

	@Override
	public Class<?> getResultClass() {
		return AdaptedUIEvent.class;
	}
}
