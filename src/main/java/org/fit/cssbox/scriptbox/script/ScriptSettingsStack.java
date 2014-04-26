/**
 * ScriptSettingsStack.java
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

package org.fit.cssbox.scriptbox.script;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Represents stack of script settings objects.
 * 
 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#stack-of-script-settings-objects">Stack of script settings objects</a>
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class ScriptSettingsStack implements Cloneable {
	private class ScriptSettingsEntry {
		ScriptSettingsEntry(ScriptSettings<?> scriptSettings, boolean candidateEntry) {
			this.scriptSettings = scriptSettings;
			this.candidateEntry = candidateEntry;
		}
		
		ScriptSettings<?> scriptSettings;
		boolean candidateEntry;
	};
	
	private Stack<ScriptSettingsEntry> _stack;
	private List<Runnable> _globalScriptCleanupJobs;
		
	public ScriptSettingsStack() {
		_stack = new Stack<ScriptSettingsEntry>();
		_globalScriptCleanupJobs = new ArrayList<Runnable>();
	}
		
	/**
	 * Pushes new script settings object onto stack.
	 * 
	 * @param settings New script settings to be pushed onto stack.
	 */
	void push(ScriptSettings<?> settings) {
		push(settings, false);
	}
	
	/**
	 * Pushes new script settings object onto stack.
	 * 
	 * @param settings New script settings to be pushed onto stack.
	 * @param candidateEntry If set then labels the passed settings as candidate entry settings object
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#candidate-entry-settings-object">Candidate entry settings objects</a>
	 */
	void push(ScriptSettings<?> settings, boolean candidateEntry) {
		_stack.push(new ScriptSettingsEntry(settings, candidateEntry));
	}
	
	/**
	 * Pops script settings object from the stack.
	 * 
	 * @return Popped script settings object.
	 */
	public ScriptSettings<?> pop() {
		return popIncumbentScriptSettings();
	}
	
	/**
	 * Cleans all script settings from the stack.
	 */
	public void clean() {
		_stack.clear();
		isEmptyCheckpoint();
	}
	
	/**
	 * Clones script settings stack.
	 */
	@Override
	public ScriptSettingsStack clone() {
		ScriptSettingsStack clonedStack = new ScriptSettingsStack();
		clonedStack._stack.addAll(_stack);
		return clonedStack;
	}
	
	/**
	 * Imports script settings from the passed script settings stack.
	 * 
	 * @param stack Stack to be imported into this one.
	 */
	public void importScriptSettingsStack(ScriptSettingsStack stack) {
		_stack.addAll(stack._stack);
	}
	
	/**
	 * Returns the incumbent settings object.
	 * 
	 * @return The incumbent settings object.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#incumbent-settings-object">The incumbent settings object</a>
	 */
	public ScriptSettings<?> getIncumbentScriptSettings() {
		ScriptSettingsEntry settings = _stack.peek();
		return (settings != null)? settings.scriptSettings : null;
	}
	
	/**
	 * Returns the candidate entry settings object.
	 * 
	 * @return The candidate entry settings settings object.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#candidate-entry-settings-object">Candidate entry settings objects</a>
	 */
	public ScriptSettings<?> getEntryScriptSettings() {
		int endPos = _stack.size() - 1;
		for (int i = endPos; i >= 0; i--) {
			ScriptSettingsEntry entry = _stack.get(i);
			if (entry.candidateEntry) {
				return entry.scriptSettings;
			}
		}
		return null;
	}
	
	/**
	 * Pops the incumbent settings object.
	 * 
	 * @return The incumbent settings object.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#incumbent-settings-object">The incumbent settings object</a>
	 */
	public ScriptSettings<?> popIncumbentScriptSettings() {
		ScriptSettingsEntry settings = _stack.pop();
		isEmptyCheckpoint();
		return (settings != null)? settings.scriptSettings : null;
	}
	
	/**
	 * Tests whether is this stack empty, or not.
	 * 
	 * @return True if is this stack empty, otherwise false.
	 */
	public boolean isEmpty() {
		return _stack.isEmpty();
	}
	
	/**
	 * Performs checkpoint for emptiness. If true, then performs clean-up jobs.
	 */
	protected void isEmptyCheckpoint() {
		if (_stack.isEmpty()) {
			runCleanupJobs();
			performMicrotaskCheckpoint();
		}
	}
	
	/* 
	 * TODO:
	 * Performs clean-up jobs.
	 */
	protected void runCleanupJobs() {
		for (Runnable r : _globalScriptCleanupJobs) {
			r.run();
		}
	}
	
	/*
	 * TODO:
	 * http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#perform-a-microtask-checkpoint
	 */
	protected void performMicrotaskCheckpoint() {
		
	}
}
