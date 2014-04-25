/**
 * MimeContentRegistryBase.java
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

package org.fit.cssbox.scriptbox.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang3.reflect.ConstructorUtils;

/**
 * Base class for all registry/managers which collects all {@link MimeContentFactoryBase}.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class MimeContentRegistryBase<MimeContentFactory extends MimeContentFactoryBase<? extends MimeContent>, MimeContent> {
	protected Map<String, Set<MimeContentFactory>> registeredFactories;
	
	protected MimeContentRegistryBase() {
		registeredFactories = new HashMap<String, Set<MimeContentFactory>>();
	}
	
	/**
	 * Registers new MIME content factory.
	 * 
	 * @param factoryClass Class of the MIME content factory
	 * @param args Arguments for the construction the given MIME content factory.
	 * @return Constructed and registered MIME content factory on success, otherwise null.
	 */
	public MimeContentFactory registerMimeContentFactory(Class<? extends MimeContentFactory> factoryClass, Object ...args) {	
		MimeContentFactory factory = null;
		
		synchronized (this) {

			factory = instantizeMimeContentFactory(factoryClass, args);
			
			if (factory == null) {
				return null;
			}
			
			List<String> mimeTypes = factory.getExplicitlySupportedMimeTypes();
			
			for (String mimeType : mimeTypes) {
				Set<MimeContentFactory> factories = registeredFactories.get(mimeType);
				
				if (factories == null) {
					factories = new HashSet<MimeContentFactory>();
				}
				
				factories.add(factory);
				registeredFactories.put(mimeType, factories);		
			}
		}
		
		return factory;
	}
	
	/**
	 * Unregisters given factory from this registry.
	 * 
	 * @param factoryClass Class of the MIME content factory which should be unregistered.
	 */
	public void unregisterMimeContentFactory(Class<? extends MimeContentFactoryBase<?>> factoryClass) {
		synchronized (this) {
			Set<Entry<String, Set<MimeContentFactory>>> factoriesEntrySet = registeredFactories.entrySet();
			
			List<String> mimeTypesToRemove = new ArrayList<String>();
			for (Entry<String, Set<MimeContentFactory>> entry : factoriesEntrySet) {
				Set<MimeContentFactory> factories = entry.getValue();
				MimeContentFactoryBase<?> factoryToRemove = null;
				
				for (MimeContentFactory factory : factories) {
					if (factory.getClass().equals(factoryClass)) {
						factoryToRemove = factory;
					}
				}
				
				if (factories.size() == 1 && factoryToRemove != null) {
					mimeTypesToRemove.add(entry.getKey());
				}
				
				factories.remove(factoryToRemove);
			}
		}
	}
	
	/**
	 * Returns all registered MIME content factories.
	 * 
	 * @return All registered MIME content factories.
	 */
	public Set<MimeContentFactory> getAllMimeContentFactories() {
		Set<MimeContentFactory> factories = new HashSet<MimeContentFactory>();
		
		for (Map.Entry<String, Set<MimeContentFactory>> registeredEntry : registeredFactories.entrySet()) {
			Set<MimeContentFactory> entryFactories = registeredEntry.getValue();
			factories.addAll(entryFactories);
		}
		
		return factories;
	}
	
	/**
	 * Returns content for given MIME type and arguments.
	 * 
	 * @param mimeType MIME type of which content should be constructed.
	 * @param args Arguments for construction content of the given MIME type.
	 * @return New constructed content, or null on failure.
	 */
	public MimeContent getContent(String mimeType, Object ...args) {
		MimeContentFactory factory = getFirstMimeContentFactory(mimeType);

		return (factory == null)? null : factory.getContent(args);
	}
	
	/**
	 * Returns first registered MIME content factory for a given MIME type.
	 * 
	 * @param mimeType MIME content type of which should returned the MIME content factory.
	 * @return First MIME content factory for a given MIME type.
	 */
	public MimeContentFactory getFirstMimeContentFactory(String mimeType) {
		Set<MimeContentFactory> factories = getMimeContentFactories(mimeType);
		
		return (factories.isEmpty())? null : factories.iterator().next();
	}
	
	/**
	 * Returns all registered MIME content factories for a given MIME type.
	 * 
	 * @param mimeType MIME content type of which should returned the MIME content factories.
	 * @return All registered MIME content factories for a given MIME type.
	 */
	public Set<MimeContentFactory> getMimeContentFactories(String mimeType) {
		Set<MimeContentFactory> matchedFactories = new HashSet<MimeContentFactory>();
		Set<MimeContentFactory> explicitFactories = getExplicitMimeContentFactories(mimeType);
		Set<MimeContentFactory> implicitFactories = getImplicitMimeContentFactories(mimeType);
		
		matchedFactories.addAll(explicitFactories);
		matchedFactories.addAll(implicitFactories);
		
		return matchedFactories;
	}
	
	/**
	 * Returns all registered MIME content factories that supports explicitly given MIME type.
	 * 
	 * @param mimeType MIME content type of which should returned the MIME content factories.
	 * @return All registered MIME content factories that supports explicitly given MIME type.
	 */
	public Set<MimeContentFactory> getExplicitMimeContentFactories(String mimeType) {
		Set<MimeContentFactory> factories = registeredFactories.get(mimeType);
		
		return (factories == null)? new HashSet<MimeContentFactory>() : Collections.unmodifiableSet(factories);
	}
	
	/**
	 * Returns all registered MIME content factories that supports implicitly given MIME type.
	 * 
	 * @param mimeType MIME content type of which should returned the MIME content factories.
	 * @return All registered MIME content factories that supports implicitly given MIME type.
	 */
	public Set<MimeContentFactory> getImplicitMimeContentFactories(String mimeType) {
		Set<MimeContentFactory> matchedFactories = new HashSet<MimeContentFactory>();
		synchronized (this) {
			Set<Entry<String, Set<MimeContentFactory>>> factoriesEntrySet = registeredFactories.entrySet();
			
			for (Entry<String, Set<MimeContentFactory>> entry : factoriesEntrySet) {
				Set<MimeContentFactory> factories = entry.getValue();
				
				for (MimeContentFactory factory : factories) {
					if (!matchedFactories.contains(factory) && factory.isImplicitlySupported(mimeType)) {
						matchedFactories.add(factory);
					}
				}
			}
			
			return matchedFactories;
		}
	}
	
	/**
	 * Tests whether is given MIME type supported.
	 * 
	 * @param mimeType MIME type to be tested.
	 * @return True if is given MIME type supported, otherwise false.
	 */
	public boolean isSupported(String mimeType) {
		return isExplicitlySupported(mimeType) || isImplicitlySupported(mimeType);
	}
	
	/**
	 * Tests whether is given MIME type explicitly supported.
	 * 
	 * @param mimeType MIME type to be tested.
	 * @return True if is given MIME type explicitly supported, otherwise false.
	 */
	public boolean isExplicitlySupported(String mimeType) {
		return registeredFactories.containsKey(mimeType);
	}
	
	/**
	 * Tests whether is given MIME type implicitly supported.
	 * 
	 * @param mimeType MIME type to be tested.
	 * @return True if is given MIME type implicitly supported, otherwise false.
	 */
	public boolean isImplicitlySupported(String mimeType) {
		synchronized (this) {
			Set<Entry<String, Set<MimeContentFactory>>> factoriesEntrySet = registeredFactories.entrySet();
			
			for (Entry<String, Set<MimeContentFactory>> entry : factoriesEntrySet) {
				Set<MimeContentFactory> factories = entry.getValue();
				
				for (MimeContentFactory factory : factories) {
					if (factory.isImplicitlySupported(mimeType)) {
						return true;
					}
				}
			}
			
			return false;
		}
	}
	
	/**
	 * Constructs MIME content factory from a given class and constructor arguments.
	 * 
	 * @param factoryClass MIME content factory class of which should be constructed the instance.
	 * @param args Constructor arguments for factory.
	 * @return Constructed MIME content factory on success, otherwise null.
	 */
	protected MimeContentFactory instantizeMimeContentFactory(Class<? extends MimeContentFactory> factoryClass, Object ...args) {
		MimeContentFactory mimeContentFactory = null;
		
		try {
			/*List<Class<?>> constructorClassesList = new ArrayList<Class<?>>();
			
			for (Object arg : args) {
				constructorClassesList.add(arg.getClass());
			}
			
			Class<?> constructorClasses[] = constructorClassesList.toArray(new Class<?>[constructorClassesList.size()]);
			
			Constructor<? extends MimeContentFactoryBase<?>> mimeContentFactoryConstructor = factoryClass.getConstructor(constructorClasses);
			mimeContentFactory = mimeContentFactoryConstructor.newInstance(args);*/
			
			Object newInstance = ConstructorUtils.invokeConstructor(factoryClass, args);
			mimeContentFactory = factoryClass.cast(newInstance);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mimeContentFactory;
	}
}
