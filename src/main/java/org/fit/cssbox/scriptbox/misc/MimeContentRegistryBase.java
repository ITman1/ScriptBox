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


public class MimeContentRegistryBase<MimeContentFactory extends MimeContentFactoryBase<? extends MimeContent>, MimeContent> {
	protected Map<String, Set<MimeContentFactory>> explicitRegisteredFactories;
	
	protected MimeContentRegistryBase() {
		explicitRegisteredFactories = new HashMap<String, Set<MimeContentFactory>>();
	}
	
	public boolean registerMimeContentFactory(Class<? extends MimeContentFactory> factoryClass, Object ...args) {	
		synchronized (this) {

			MimeContentFactory factory = instantizeMimeContentFactory(factoryClass, args);
			
			if (factory == null) {
				return false;
			}
			
			List<String> mimeTypes = factory.getExplicitlySupportedMimeTypes();
			
			for (String mimeType : mimeTypes) {
				Set<MimeContentFactory> factories = explicitRegisteredFactories.get(mimeType);
				
				if (factories == null) {
					factories = new HashSet<MimeContentFactory>();
				}
				
				factories.add(factory);
				explicitRegisteredFactories.put(mimeType, factories);		
			}
		}
		
		return true;
	}
	
	public void unregisterMimeContentFactory(Class<? extends MimeContentFactoryBase<?>> factoryClass) {
		synchronized (this) {
			Set<Entry<String, Set<MimeContentFactory>>> factoriesEntrySet = explicitRegisteredFactories.entrySet();
			
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
	
	public MimeContent getContent(String mimeType, Object ...args) {
		MimeContentFactory factory = getFirstMimeContentFactory(mimeType);

		return (factory == null)? null : factory.getContent(args);
	}
	
	public MimeContentFactory getFirstMimeContentFactory(String mimeType) {
		Set<MimeContentFactory> factories = getMimeContentFactories(mimeType);
		
		return (factories == null)? null : factories.iterator().next();
	}
	
	public Set<MimeContentFactory> getMimeContentFactories(String mimeType) {
		Set<MimeContentFactory> matchedFactories = new HashSet<MimeContentFactory>();
		Set<MimeContentFactory> explicitFactories = getExplicitMimeContentFactories(mimeType);
		Set<MimeContentFactory> implicitFactories = getImplicitMimeContentFactories(mimeType);
		
		matchedFactories.addAll(explicitFactories);
		matchedFactories.addAll(implicitFactories);
		
		return matchedFactories;
	}
	
	public Set<MimeContentFactory> getExplicitMimeContentFactories(String mimeType) {
		Set<MimeContentFactory> factories = explicitRegisteredFactories.get(mimeType);
		
		return (factories == null)? null : Collections.unmodifiableSet(factories);
	}
	
	public Set<MimeContentFactory> getImplicitMimeContentFactories(String mimeType) {
		Set<MimeContentFactory> matchedFactories = new HashSet<MimeContentFactory>();
		synchronized (this) {
			Set<Entry<String, Set<MimeContentFactory>>> factoriesEntrySet = explicitRegisteredFactories.entrySet();
			
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
	
	public boolean isSupported(String mimeType) {
		return isExplicitlySupported(mimeType) || isImplicitlySupported(mimeType);
	}
	
	public boolean isExplicitlySupported(String mimeType) {
		return explicitRegisteredFactories.containsKey(mimeType);
	}
	
	public boolean isImplicitlySupported(String mimeType) {
		synchronized (this) {
			Set<Entry<String, Set<MimeContentFactory>>> factoriesEntrySet = explicitRegisteredFactories.entrySet();
			
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