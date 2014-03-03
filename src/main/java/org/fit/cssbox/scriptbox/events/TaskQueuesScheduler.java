package org.fit.cssbox.scriptbox.events;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.fit.cssbox.scriptbox.events.EventLoop.ExecutionThread;
import org.fit.cssbox.scriptbox.exceptions.LifetimeEndedException;

import com.google.common.base.Predicate;

public class TaskQueuesScheduler {	
	protected class ExecutionThread extends Thread {
		@Override
		public void run() {
			try {
				schedulerLoop();
			} catch (InterruptedException e) {
			}
			
			synchronized (TaskQueuesScheduler.this) {
				aborted = true;
			}
		};
	};
	
	protected static List<TaskSource> sourcesList;

	static {
		sourcesList = new ArrayList<TaskSource>();
		sourcesList.add(TaskSource.DOM_MANIPULATION);
		sourcesList.add(TaskSource.HISTORY_TRAVERSAL);
		sourcesList.add(TaskSource.NETWORKING);
		sourcesList.add(TaskSource.USER_INTERACTION);
	}

	protected static final LifetimeEndedException ABORTED_EXCEPTION = new LifetimeEndedException("Task queues scheduler has been aborted!");
	protected List<Task> inputTasks;
	protected List<Task> scheduledTasks;
	protected TaskQueues taskQueues;
	protected boolean aborted;
	protected Object pauseMonitor;
	
	protected int sourcesListPosition;
	protected Task lastStartedTask;
	protected long lastStartedTaskTime;
	
	protected ExecutionThread executionThread;
	
	public TaskQueuesScheduler() {
		this.taskQueues = new TaskQueues();
		this.scheduledTasks = new LinkedList<Task>();
		this.pauseMonitor = new Object();
		this.sourcesListPosition = -1;
		
		executionThread = new ExecutionThread();
		executionThread.start();
	}
	
	public Task pullTask() throws InterruptedException {
		if (aborted) {
			throw ABORTED_EXCEPTION;
		}
		
		synchronized (this) {
			while (scheduledTasks.isEmpty()) {
				wait();
			}
		
			return scheduledTasks.remove(0);
		}
	}
	
	public void queueTask(Task task) {
		if (aborted) {
			throw ABORTED_EXCEPTION;
		}
		
		synchronized (this) {
			taskQueues.queueTask(task);
		}
		
		synchronized (pauseMonitor) {
			pauseMonitor.notifyAll();
		}
		
	}
	
	public synchronized boolean isAborted() {
		return aborted;
	}
	
	public void onTaskStarted(Task task) {
		if (aborted) {
			throw ABORTED_EXCEPTION;
		}
		
		synchronized (this) {
			lastStartedTask = task;
			lastStartedTaskTime = getCpuTime();
		}
	}
	
	public void onTaskFinished(Task task) {
		long currentTaskTime = getCpuTime();
		
		if (aborted) {
			throw ABORTED_EXCEPTION;
		}
		
		// TODO: Store taskDuration somewhere and use it then in scheduling
		long taskDuration = 0L;
		synchronized (this) {
			if (lastStartedTask != task) {
				return;
			}
			taskDuration = currentTaskTime - lastStartedTaskTime;
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
	
	protected void schedulerLoop() throws InterruptedException {
		while (true) {
			waitUntilQueueAnyTask();

			while (!taskQueues.isEmpty()) {
				Task scheduledTask = scheduleTask();
				insertScheduledTask(scheduledTask);
			}
		}
	}
	
	protected void insertScheduledTask(Task scheduledTask) {
		if (scheduledTask != null) {
			synchronized (this) {
				scheduledTasks.add(scheduledTask);
				notifyAll();
			}
		}
	}
	
	protected Task scheduleTask() {
		/* FIXME: Fix it to something more sophisticated than round robin. */
		sourcesListPosition = (sourcesListPosition + 1) % sourcesList.size();
		TaskSource source = sourcesList.get(sourcesListPosition);
			
		synchronized (this) {
			Task task = taskQueues.pullTask(source);
			return task;
		}
	}
	
	protected void waitUntilQueueAnyTask() throws InterruptedException {
		synchronized (pauseMonitor) {
			boolean queuesEmpty;
			do {
				synchronized (this) {
					queuesEmpty = taskQueues.isEmpty();
				}
				
				if (queuesEmpty) {
					pauseMonitor.wait();
				}
			} while (queuesEmpty);
		}
	}
	
	protected long getCpuTime() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
		return bean.isCurrentThreadCpuTimeSupported()? bean.getCurrentThreadCpuTime() : 0L;
	}
}
