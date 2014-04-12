/**
 * Adapter.java
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

package org.fit.cssbox.scriptbox.script.javascript.wrap.adapter;

/**
 * The Adapter interface for creating adapters that is necessary for passing
 * adapters into {@link AdapterRegistry}.
 * 
 * @version 1.0
 * @see AdapterRegistry
 */
public interface Adapter {
    
    /**
     * Gets the provider object which should implement the specific conversion
     * method for adapting the adaptee object to the result object. 
     *
     * @param obj Adaptee object which should be adapted to another one.
     * @return The provider, which should implement the adapt method.
     */
    public Object getProvider(Object obj);
    
    /**
     * Gets the adaptee class, which should be adapted to the another one.
     * 
     * <p>Result of this method is used for registering the adapter in the 
     * adapter registry {@link AdapterRegistry}.
     *
     * @return The adaptee class, which should be adapted to the another one.
     */
    public Class<?> getAdapteeClass();
    
    /**
     * Gets the result class to which should be adaptee object adapted. It is 
     * proper to return here the class of the object which returns the adapt 
     * method of the provider or abstract/interface/base class of the provider.
     * 
     * <p>Result of this method is used for registering the adapter in the
     * adapter registry {@link AdapterRegistry}.
     *
     * @return The Result class to which should be adapted the adaptee object.
     */
    public Class<?> getResultClass();
}

// End of the file
