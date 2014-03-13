package org.fit.cssbox.scriptbox.script.javascript.wrap;

import java.util.List;
import java.util.Map;

import org.fit.cssbox.scriptbox.script.javascript.java.ObjectGetter;
import org.fit.cssbox.scriptbox.script.javascript.js.NativeJavaCollection;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class CollectionsWrapFactoryDecorator extends WrapFactoryDecorator {
	
	public CollectionsWrapFactoryDecorator() {}
	
	public CollectionsWrapFactoryDecorator(WrapFactoryDecorator decorator) {
		super(decorator);
	}
	
	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
		final Class<?> type = javaObject.getClass();
		boolean isCollection = List.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type) || ObjectGetter.class.isAssignableFrom(type);
        
		if (isCollection) {
        	return new NativeJavaCollection(scope, javaObject, staticType);
        }
        
        return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
	}
}
