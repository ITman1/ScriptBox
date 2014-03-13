package tests.script.engine;

import org.fit.cssbox.scriptbox.script.javascript.GlobalObjectJavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.JavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.object.ObjectTopLevel;
import org.fit.cssbox.scriptbox.script.javascript.wrap.CollectionsWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.DefaultWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.WrapFactoryDecorator;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.TopLevel;

public class CollectionsWrapFactoryTests {
	public class CollectionsContextFactory extends ContextFactory {
				
		@Override
		protected Context makeContext() {
			Context cx = super.makeContext();
			
			WrapFactoryDecorator wrapFactoryDecorator = new DefaultWrapFactoryDecorator();
			wrapFactoryDecorator = new CollectionsWrapFactoryDecorator(wrapFactoryDecorator);

			cx.setWrapFactory(wrapFactoryDecorator);
			
			return cx;
		}
	}

	protected JavaScriptEngine getCollectionsScriptEngine(final Object object) {
		return new GlobalObjectJavaScriptEngine(null, null, new CollectionsContextFactory()) {
			@Override
			protected TopLevel initializeTopLevel() {
				return new ObjectTopLevel(object, this);
			}
		};
	}
	
}
