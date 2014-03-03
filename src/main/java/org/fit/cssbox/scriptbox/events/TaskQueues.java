package org.fit.cssbox.scriptbox.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class TaskQueues extends HashMap<TaskSource, List<Task>> {

	private static final long serialVersionUID = -6406790067694059818L;

	public void queueTask(Task task) {
		List<Task> tasks = get(task.getTaskSource());
		
		if (tasks == null) {
			tasks = new ArrayList<Task>();
		}
		
		tasks.add(task);
		
		put(task.getTaskSource(), tasks);
	}
	
	public Task pullTask(TaskSource source) {
		List<Task> tasks = get(source);
		
		if (tasks != null) {
			if (tasks.isEmpty()) {
				remove(source);
				return null;
			} else {
				return tasks.remove(0);
			}
		}
		
		return null;
	}
	
	public void filter(TaskSource source, Predicate<Task> predicate) {
		List<Task> tasks = get(source);
		
		if (tasks != null) {
			List<Task> filteredTasks = new ArrayList<Task>();
			Iterables.addAll(filteredTasks, Iterables.filter(tasks, predicate));
			
			if (filteredTasks.isEmpty()) {
				remove(source);
			} else {
				put(source, filteredTasks);
			}
		}
	}
	
	public boolean removeFirstTask(Task task) {
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
	
	public void removeAllTasks(Task task) {
		while (removeFirstTask(task)) {}
	}
	
	public boolean isEmpty() {
		for (Map.Entry<TaskSource, List<Task>> entry : entrySet()) {
			if (!entry.getValue().isEmpty()) {
				return false;
			}
		}
		return true;
	}
}
