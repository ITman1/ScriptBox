/**
 * TaskQueues.java
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

package org.fit.cssbox.scriptbox.events;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class TaskQueues extends HashMap<TaskSource, List<Task>> {

	private static final long serialVersionUID = -6406790067694059818L;

	public synchronized void queueTask(Task task) {
		List<Task> tasks = get(task.getTaskSource());
		
		if (tasks == null) {
			tasks = new LinkedList<Task>();
		}
		
		tasks.add(task);
		
		put(task.getTaskSource(), tasks);
	}
	
	public synchronized Task pullTask(TaskSource source) {
		List<Task> tasks = get(source);
		
		if (tasks != null && !tasks.isEmpty()) {
			Task task = tasks.remove(0);
			
			if (tasks.isEmpty()) {
				remove(source);
			}
			
			return task;
		}
		
		return null;
	}
	
	public synchronized void filter(Predicate<Task> predicate) {
		Set<TaskSource> sources = keySet();
		
		for (TaskSource source : sources) {
			filter(source, predicate);
		}
	}
	
	public synchronized void filter(TaskSource source, Predicate<Task> predicate) {
		List<Task> tasks = get(source);
		
		if (tasks != null) {
			List<Task> filteredTasks = new LinkedList<Task>();
			Iterables.addAll(filteredTasks, Iterables.filter(tasks, predicate));
			
			if (filteredTasks.isEmpty()) {
				remove(source);
			} else {
				put(source, filteredTasks);
			}
		}
	}
	
	public synchronized boolean removeFirstTask(Task task) {
		List<Task> tasks = get(task.getTaskSource());
		
		if (tasks != null) {
			if (tasks.remove(task)) {
				if (tasks.isEmpty()) {
					TaskSource source = task.getTaskSource();
					remove(source);
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	public synchronized void removeAllTasks(Task task) {
		while (removeFirstTask(task)) {}
	}
	
	public synchronized boolean isEmpty() {
		for (Map.Entry<TaskSource, List<Task>> entry : entrySet()) {
			if (!entry.getValue().isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	public synchronized boolean isEmpty(TaskSource source) {
		return get(source) == null;
	}
}
