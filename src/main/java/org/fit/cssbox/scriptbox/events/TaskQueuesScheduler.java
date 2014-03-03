package org.fit.cssbox.scriptbox.events;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.LinkedList;
import java.util.List;

import org.fit.cssbox.scriptbox.exceptions.LifetimeEndedException;

import com.google.common.base.Predicate;

public abstract class TaskQueuesScheduler {	
	protected class ExecutionThread extends Thread {
		@Override
		public void run() {
			try {
				schedulerLoop();
			} catch (InterruptedException e) {
			}
			
			synchronized (TaskQueuesScheduler.this) {
				aborted = true;
				cleanupJobs();
			}
		};
	};
	
	protected static final LifetimeEndedException ABORTED_EXCEPTION = new LifetimeEndedException("Task queues scheduler has been aborted!");
	protected List<Task> inputTasks;
	protected List<Task> scheduledTasks;
	protected TaskQueues taskQueues;
	protected boolean aborted;
	protected Object pauseMonitor;
	
	protected Task lastStartedTask;
	protected long lastStartedTaskTime;
	
	protected ExecutionThread executionThread;
	
	public TaskQueuesScheduler() {
		this.taskQueues = new TaskQueues();
		this.scheduledTasks = new LinkedList<Task>();
		this.pauseMonitor = new Object();
		
		executionThread = new ExecutionThread();
		executionThread.start();
	}
	
	public synchronized void abort(boolean join) throws InterruptedException {		
		testForAbort();
		
		Thread currentThread = Thread.currentThread();
		boolean currentThreadInterrupted = currentThread.equals(executionThread);
		
		executionThread.interrupt();

		if (currentThreadInterrupted) {
			throw new InterruptedException(); // Ensures returning from the schedule loop
		} else if (join) {
			executionThread.join();
		}		
	}
	
	public Task pullTask() throws InterruptedException {
		testForAbort();
		
		synchronized (this) {
			while (scheduledTasks.isEmpty()) {
				wait();
			}
		
			return scheduledTasks.remove(0);
		}
	}
	
	public void queueTask(Task task) {
		testForAbort();
		
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
		testForAbort();
		
		synchronized (this) {
			lastStartedTask = task;
			lastStartedTaskTime = getCpuTime();
		}
	}
	
	public void onTaskFinished(Task task) {
		long currentTaskTime = getCpuTime();
		
		testForAbort();
		
		long taskDuration = 0L;
		synchronized (this) {
			if (lastStartedTask != task) {
				return;
			}
			taskDuration = currentTaskTime - lastStartedTaskTime;
		}
		
		onTaskCompletedExecution(task, taskDuration);
	}
	
	public synchronized void removeFirstTask(Task task) {
		testForAbort();
		boolean isRemovedFromBuffer = scheduledTasks.remove(task);
		if (!isRemovedFromBuffer) {
			taskQueues.removeFirstTask(task);
		}
	}
	
	public synchronized void removeAllTasks(Task task) {
		testForAbort();
		while (scheduledTasks.remove(task));
		taskQueues.removeAllTasks(task);
	}
	
	public synchronized void filter(TaskSource source, Predicate<Task> predicate) {
		testForAbort();
		taskQueues.filter(source, predicate);
	}
	
	protected void testForAbort() {
		if (aborted) {
			throw ABORTED_EXCEPTION;
		}
		
		if (!executionThread.isAlive()) {
			aborted = true;
			throw ABORTED_EXCEPTION;
		}
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
	
	protected void cleanupJobs() {
	}
	
	protected abstract Task scheduleTask();
	protected abstract void onTaskCompletedExecution(Task task, long taskDuration);
}
