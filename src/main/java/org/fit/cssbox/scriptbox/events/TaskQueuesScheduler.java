package org.fit.cssbox.scriptbox.events;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

import org.fit.cssbox.scriptbox.exceptions.LifetimeEndedException;

import com.google.common.base.Predicate;

public class TaskQueuesScheduler {
	protected static List<TaskSource> sourcesList;
	static {
		sourcesList = new ArrayList<TaskSource>();
		sourcesList.add(TaskSource.DOM_MANIPULATION);
		sourcesList.add(TaskSource.HISTORY_TRAVERSAL);
		sourcesList.add(TaskSource.NETWORKING);
		sourcesList.add(TaskSource.USER_INTERACTION);
	}
	
	protected static final LifetimeEndedException ABORTED_EXCEPTION = new LifetimeEndedException("Task queues scheduler has been aborted!");
	protected List<Task> scheduledTasks;
	protected TaskQueues taskQueues;
	protected boolean aborted;
	protected int sourcesListPosition;
	
	public TaskQueuesScheduler() {
		this.taskQueues = new TaskQueues();
		this.scheduledTasks = new ArrayList<Task>();
		sourcesListPosition = -1;
	}
	
	public synchronized Task pullTask() throws InterruptedException {
		if (aborted) {
			throw ABORTED_EXCEPTION;
		}
		while (taskQueues.isEmpty() && !aborted) {
			wait();
		}
		while (true) {
			/* FIXME: Fix it to something more sophisticated than round robin. */
			sourcesListPosition = (sourcesListPosition + 1) % sourcesList.size();
			TaskSource source = sourcesList.get(sourcesListPosition);
			
			Task task = taskQueues.pullTask(source);
			
			if (task != null) {
				return task;
			}
		}
		
	}
	
	public synchronized void queueTask(Task task) {
		if (aborted) {
			throw ABORTED_EXCEPTION;
		}
		
		taskQueues.queueTask(task);
		notifyAll();
	}
	
	public synchronized boolean isAborted() {
		return aborted;
	}
	
	public synchronized void onTaskStarted(Task task) {
		if (aborted) {
			throw ABORTED_EXCEPTION;
		}
	}
	
	public synchronized void onTaskFinished(Task task) {
		if (aborted) {
			throw ABORTED_EXCEPTION;
		}
	}
	
	public synchronized void removeFirstTask(Task task) {
		if (aborted) {
			throw ABORTED_EXCEPTION;
		}
		boolean isRemovedFromBuffer = scheduledTasks.remove(task);
		if (!isRemovedFromBuffer) {
			taskQueues.removeFirstTask(task);
		}
	}
	
	public synchronized void removeAllTasks(Task task) {
		if (aborted) {
			throw ABORTED_EXCEPTION;
		}
		while (scheduledTasks.remove(task));
		taskQueues.removeAllTasks(task);
	}
	
	public synchronized void filter(TaskSource source, Predicate<Task> predicate) {
		if (aborted) {
			throw ABORTED_EXCEPTION;
		}
		taskQueues.filter(source, predicate);
	}
	
	protected void schedulingLoop() {
		while (!aborted) {
			

		}

	}
	
	protected long getCpuTime() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
		return bean.isCurrentThreadCpuTimeSupported()? bean.getCurrentThreadCpuTime() : 0L;
	}
}
