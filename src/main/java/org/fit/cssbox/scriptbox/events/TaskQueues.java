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
			tasks = new ArrayList<Task>();
			Iterables.addAll(tasks, Iterables.filter(tasks, predicate));
		}
		
		put(source, tasks);
	}
	
	public void removeTask(Task task) {
		List<Task> tasks = get(task.getTaskSource());
		
		if (tasks != null) {
			tasks.remove(task);
		}
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
