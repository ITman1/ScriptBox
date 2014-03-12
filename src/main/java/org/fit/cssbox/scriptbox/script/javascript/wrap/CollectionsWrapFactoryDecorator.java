package org.fit.cssbox.scriptbox.script.javascript.wrap;

import java.util.AbstractList;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

public class CollectionsWrapFactoryDecorator extends WrapFactoryDecorator {
	
	public CollectionsWrapFactoryDecorator() {}
	
	public CollectionsWrapFactoryDecorator(WrapFactoryDecorator decorator) {
		super(decorator);
	}
	
	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
		
		final Class<?> type = javaObject.getClass();
		
        if (AbstractList.class.isAssignableFrom(type)) {
        	return new NativeJavaObject(scope, javaObject, staticType) {
        		
				private static final long serialVersionUID = -369637419233477403L;

				@Override
        		public Object get(int index, Scriptable start) {
        			return ((AbstractList<?>)unwrap()).get(index);
        		}
        	};
        }
        
        return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
	}
}
