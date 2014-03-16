package tests.script.engine;

import org.fit.cssbox.scriptbox.script.javascript.wrap.CollectionsWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.DefaultWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.WrapFactoryDecorator;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

public class TestContextFactories {
	public static class DefaultContextFactory extends ContextFactory {
		
		@Override
		protected Context makeContext() {
			Context cx = super.makeContext();
			
			WrapFactoryDecorator wrapFactoryDecorator = new DefaultWrapFactoryDecorator();

			cx.setWrapFactory(wrapFactoryDecorator);
			
			return cx;
		}
	}
	
	public static class CollectionsContextFactory extends ContextFactory {
		
		@Override
		protected Context makeContext() {
			Context cx = super.makeContext();
			
			WrapFactoryDecorator wrapFactoryDecorator = new DefaultWrapFactoryDecorator();
			wrapFactoryDecorator = new CollectionsWrapFactoryDecorator(wrapFactoryDecorator);

			cx.setWrapFactory(wrapFactoryDecorator);
			
			return cx;
		}
	}
}
