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
		
	void push(ScriptSettings<?> settings) {
		push(settings, false);
	}
	
	void push(ScriptSettings<?> settings, boolean candidateEntry) {
		_stack.push(new ScriptSettingsEntry(settings, candidateEntry));
	}
	
	public ScriptSettings<?> pop() {
		return popIncumbentScriptSettings();
	}
	
	public void clean() {
		_stack.clear();
		isEmptyCheckpoint();
	}
	
	@Override
	public ScriptSettingsStack clone() {
		ScriptSettingsStack clonedStack = new ScriptSettingsStack();
		clonedStack._stack.addAll(_stack);
		return clonedStack;
	}
	
	public void importScriptSettingsStack(ScriptSettingsStack stack) {
		_stack.addAll(stack._stack);
	}
	
	public ScriptSettings<?> getIncumbentScriptSettings() {
		ScriptSettingsEntry settings = _stack.peek();
		return (settings != null)? settings.scriptSettings : null;
	}
	
	public ScriptSettings<?> getEntryScriptSettings() {
		int size = _stack.size();
		for (int i = size; i >= 0; i--) {
			ScriptSettingsEntry entry = _stack.get(i);
			if (entry.candidateEntry) {
				return entry.scriptSettings;
			}
		}
		return null;
	}
	
	public ScriptSettings<?> popIncumbentScriptSettings() {
		ScriptSettingsEntry settings = _stack.pop();
		isEmptyCheckpoint();
		return (settings != null)? settings.scriptSettings : null;
	}
	
	public boolean isEmpty() {
		return _stack.isEmpty();
	}
	
	protected void isEmptyCheckpoint() {
		if (_stack.isEmpty()) {
			runCleanupJobs();
			performMicrotaskCheckpoint();
		}
	}
	
	protected void runCleanupJobs() {
		for (Runnable r : _globalScriptCleanupJobs) {
			r.run();
		}
	}
	
	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#perform-a-microtask-checkpoint
	 */
	protected void performMicrotaskCheckpoint() {
		
	}
}
