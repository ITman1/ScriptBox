/**
 * TaskQueuesScheduler.java
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
	
	public synchronized void filter(Predicate<Task> predicate) {
		testForAbort();
		taskQueues.filter(predicate);
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
	
	protected void onTaskCompletedExecution(Task task, long taskDuration) {
	}
	
	protected abstract Task scheduleTask();

}
