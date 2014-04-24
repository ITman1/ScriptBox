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

/**
 * Abstract class for scheduling tasks.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class TaskQueuesScheduler {	
	private class TaskQueuesSchedulerThread extends Thread {
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
		
		@Override
		public String toString() {
			return TaskQueuesScheduler.this.getClass().getSimpleName() + " Thread";
		}
	};
	
	protected static final LifetimeEndedException ABORTED_EXCEPTION = new LifetimeEndedException("Task queues scheduler has been aborted!");
	protected List<Task> inputTasks;
	protected List<Task> scheduledTasks;
	protected TaskQueues taskQueues;
	protected boolean aborted;
	protected Object pauseMonitor;
	
	protected Task lastStartedTask;
	protected long lastStartedTaskTime;
	
	protected TaskQueuesSchedulerThread executionThread;
	
	public TaskQueuesScheduler() {
		this.taskQueues = new TaskQueues();
		this.scheduledTasks = new LinkedList<Task>();
		this.pauseMonitor = new Object();
		
		executionThread = new TaskQueuesSchedulerThread();
		executionThread.start();
	}
	
	/**
	 * Aborts synchronously this scheduler
	 * 
	 * @param join If is set, then current thread waits until scheduler finishes.
	 * @throws InterruptedException if we are aborting current thread or if it occurs on join
	 */
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
	
	/**
	 * Pulls task - e.g. removes task from the scheduled tasks or 
	 * blocks if there is not any task to be pulled.
	 * 
	 * @return New task to be executed.
	 * 
	 * @throws InterruptedException It is thrown if current thread is interrupted.
	 */
	public Task pullTask() throws InterruptedException {
		testForAbort();
		
		synchronized (this) {
			while (scheduledTasks.isEmpty()) {
				wait();
			}
		
			return scheduledTasks.remove(0);
		}
	}
	
	/**
	 * Queues new task.
	 * 
	 * @param task New task to be queued.
	 */
	public void queueTask(Task task) {
		testForAbort();
		
		synchronized (this) {
			taskQueues.queueTask(task);
		}
		
		synchronized (pauseMonitor) {
			pauseMonitor.notifyAll();
		}
		
	}
	
	/**
	 * Tests whether has been this scheduler aborted.
	 * 
	 * @return True if is this scheduler aborted, otherwise false.
	 */
	public synchronized boolean isAborted() {
		return aborted;
	}
	
	/**
	 * Callback method to notify that task has just started in event loop.
	 * 
	 * @param task Task which has started while processing event loop.
	 */
	public void onTaskStarted(Task task) {
		testForAbort();
		
		synchronized (this) {
			lastStartedTask = task;
			lastStartedTaskTime = getCpuTime();
		}
	}
	
	/**
	 * Callback method to notify that task has just finished in event loop.
	 * 
	 * @param task Task which has finished while processing event loop.
	 */
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
	
	/**
	 * Removes first task which equals to the passed task.
	 * 
	 * @param task Task to be removed from the executing.
	 */
	public synchronized void removeFirstTask(Task task) {
		testForAbort();
		boolean isRemovedFromBuffer = scheduledTasks.remove(task);
		if (!isRemovedFromBuffer) {
			taskQueues.removeFirstTask(task);
		}
	}
	
	/**
	 * Removes all tasks which equal to the passed task.
	 * 
	 * @param task Task to be removed from the executing.
	 */
	public synchronized void removeAllTasks(Task task) {
		testForAbort();
		while (scheduledTasks.remove(task));
		taskQueues.removeAllTasks(task);
	}
	
	/**
	 * Filters given task source queue by a predicate.
	 * 
	 * @param source Task source that should be filtered.
	 * @param predicate Predicate which ensures filtering. On success
	 *        the task is left untouched, otherwise will be removed.
	 */
	public synchronized void filter(TaskSource source, Predicate<Task> predicate) {
		testForAbort();
		taskQueues.filter(source, predicate);
	}
	
	/**
	 * Filters all task sources by a predicate.
	 * 
	 * @param predicate Predicate which ensures filtering. On success
	 *        the task is left untouched, otherwise will be removed.
	 */
	public synchronized void filter(Predicate<Task> predicate) {
		testForAbort();
		taskQueues.filter(predicate);
	}
			
	/**
	 * Returns current CPU time.
	 * 
	 * @return Current CPU time.
	 */
	protected long getCpuTime() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
		return bean.isCurrentThreadCpuTimeSupported()? bean.getCurrentThreadCpuTime() : 0L;
	}
	
	/**
	 * Callback method called before scheduler stops.
	 */
	protected void cleanupJobs() {
	}
	
	/**
	 * Callback method which is called when specific task completed execution.
	 * 
	 * @param task Task which completed.
	 * @param taskDuration Time how long was task running.
	 */
	protected void onTaskCompletedExecution(Task task, long taskDuration) {
	}
	
	/**
	 * Method which should schedule next task if there is any.
	 * @return New scheduled task if there is any, or null.
	 */
	protected abstract Task scheduleTask();

	private void testForAbort() {
		if (aborted) {
			throw ABORTED_EXCEPTION;
		}
		
		if (!executionThread.isAlive()) {
			aborted = true;
			throw ABORTED_EXCEPTION;
		}
	}
	
	private void schedulerLoop() throws InterruptedException {
		while (true) {
			waitUntilQueueAnyTask();

			while (!taskQueues.isEmpty()) {
				Task scheduledTask = scheduleTask();
				insertScheduledTask(scheduledTask);
			}
		}
	}
	
	private void waitUntilQueueAnyTask() throws InterruptedException {
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
	
	private void insertScheduledTask(Task scheduledTask) {
		if (scheduledTask != null) {
			synchronized (this) {
				scheduledTasks.add(scheduledTask);
				notifyAll();
			}
		}
	}
}
