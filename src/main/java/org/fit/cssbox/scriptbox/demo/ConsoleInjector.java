package org.fit.cssbox.scriptbox.demo;

import java.awt.Color;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.StringUtils;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.javascript.JavaScriptInjector;
import org.mozilla.javascript.Undefined;

public class ConsoleInjector extends JavaScriptInjector {

	public static class Console {
		
		private StyledDocument doc;
		private JScrollPane scrollPane;
		private Map<String, Date> namedStartTimes;
		
		private final static SimpleAttributeSet warningKeyword;
		private final static SimpleAttributeSet errorKeyword;
		private final static SimpleAttributeSet infoKeyword;
		
		static {
			warningKeyword = new SimpleAttributeSet();
			StyleConstants.setForeground(warningKeyword, Color.BLUE);
			StyleConstants.setBackground(warningKeyword, Color.YELLOW);
			StyleConstants.setBold(warningKeyword, true);	
			
			errorKeyword = new SimpleAttributeSet();
			StyleConstants.setForeground(errorKeyword, Color.WHITE);
			StyleConstants.setBackground(errorKeyword, Color.RED);
			StyleConstants.setBold(errorKeyword, true);	
			
			infoKeyword = new SimpleAttributeSet();
			StyleConstants.setForeground(infoKeyword, Color.WHITE);
			StyleConstants.setBackground(infoKeyword, Color.BLUE);
			StyleConstants.setBold(infoKeyword, true);
		}
		
		Console(JTextPane textPane) {
			this.doc = textPane.getStyledDocument();
			this.namedStartTimes = new HashMap<String, Date>();
			
			Container container = textPane.getParent();
			if (container instanceof JScrollPane) {
				this.scrollPane = (JScrollPane)container;
			}
		}
		
		@ScriptFunction
		public void time(String name) {
			namedStartTimes.put(name, new Date());
		}
		
		@ScriptFunction
		public void timeEnd(String name) {
			Date previousDate = namedStartTimes.get(name);
			Date currentDate = new Date();
			
			if (previousDate != null) {
				long diffInMillies = currentDate.getTime() - previousDate.getTime();			
				log("Time for '" + name + "': " + Long.toString(diffInMillies) + " ms");
			} else {			
				log("Time for '" + name + "': " + "undefined name");
			}
		}
		
		@ScriptFunction
		public void clear() {
			try {
				doc.remove(0, doc.getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		
		@ScriptFunction
		public void warn(Object ...args) {
			printArgs(args, "WARN", warningKeyword);
		}
		
		@ScriptFunction
		public void info(Object ...args) {
			printArgs(args, "INFO", infoKeyword);
		}
		
		@ScriptFunction
		public void error(Object ...args) {
			printArgs(args, "ERROR", errorKeyword);
		}
		
		@ScriptFunction
		public void log(Object ...args) {
			printArgs(args, "LOG", null);
		}
		
		@ScriptFunction
		public void debug(Object ...args) {
			log(args);
		}
		
		private void printArgs(Object[] args, final String header, final SimpleAttributeSet attr) {
			List<String> argsList = new ArrayList<String>();
			if (args != null) {
				for (Object arg : args) {
					String printableString = printableString(arg);
					argsList.add(printableString);
				}
			}
			
			final String joinedList = StringUtils.join(argsList, " ");


			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						doc.insertString(doc.getLength(), " " + header + " ", attr);
						doc.insertString(doc.getLength(), " - " + joinedList + "\n", null);
						
						if (scrollPane != null) {
							JScrollBar vertical = scrollPane.getVerticalScrollBar();
							vertical.setValue(vertical.getMaximum());
						}
						
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
			});
		}
		
		private String printableString(Object arg) {			
			String printString;
			if (arg == null) {
				printString = "null";
			} else if (arg instanceof Undefined) {
				printString = "undefined";
			} else {
				printString = arg.toString();
			}

			return printString;
		}
	}
	
	protected JTextPane textPane;
	
	public ConsoleInjector(JTextPane textPane) {
		this.textPane = textPane;
	}
	
	@Override
	public boolean inject(ScriptContext context) {
		Console console = new Console(textPane);
		
		Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("console", console);
		
		return true;
	}

}
