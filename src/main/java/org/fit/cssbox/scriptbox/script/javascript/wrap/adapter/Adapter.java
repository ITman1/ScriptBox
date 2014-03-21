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
