package org.fit.cssbox.scriptbox.demo;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import org.fit.cssbox.scriptbox.browser.UserAgent;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.fit.cssbox.scriptbox.ui.BarProp;

public class JavaScriptTesterUserAgent extends UserAgent {
	
	public static class LocationBarProp extends BarProp {
		@ScriptGetter
		@Override
		public boolean getVisible() {
			return true;
		}
	}
	
	public static class ScrollBarsProp extends BarProp {
		private JScrollPane scrollPane;
		
		ScrollBarsProp(JScrollPane scrollPane) {
			this.scrollPane = scrollPane;
		}
		
		@ScriptGetter
		@Override
		public boolean getVisible() {
			JScrollBar vsb = scrollPane.getVerticalScrollBar();
			JScrollBar hsb = scrollPane.getHorizontalScrollBar();
			return (vsb != null && vsb.isVisible()) || (hsb != null && hsb.isVisible());
		}
	}
	
	protected JavaScriptTester tester;
	
	protected JScrollPane scrollPane;
	
	public JavaScriptTesterUserAgent(JavaScriptTester tester) {
		this.tester = tester;
		this.scrollPane = tester.getScriptBrowserScrollPane();
	}
	
	@Override
	public BarProp getLocationbar() {
		return new LocationBarProp();
	}
	
	@Override
	public BarProp getScrollbars() {
		return new ScrollBarsProp(scrollPane);
	}
}
