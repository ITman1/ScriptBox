/**
 * EventLoop.java
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

import java.net.URL;
import java.util.Collection;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.exceptions.LifetimeEndedException;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.fit.cssbox.scriptbox.exceptions.WrappedException;
import org.fit.cssbox.scriptbox.script.ScriptSettingsStack;

import com.google.common.base.Predicate;

/**
 * Represents event loop to coordinate events, user interaction, scripts, 
 * rendering, networking, and so forth.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/webappapis.html#event-loop">Event loop</a>
 */
public class EventLoop {
	/**
	 * Wrapper for tasks which is currently executing. This wrapper ensures
	 * injecting/setting state properties inside task object and calling proper callback methods.
	 * 
	 * @author Radim Loskot
	 * @version 0.9
	 * @since 0.9 - 21.4.2014
	 */
	private class TaskWrapper extends Task {
		private Task wrappedTask;
				
		public TaskWrapper(Task wrappedTask) {
			super(wrappedTask.getTaskSource(), wrappedTask.getBrowsingContext());
			this.wrappedTask = wrappedTask;
		}

		@Override
		public void execute() throws TaskAbortedException, InterruptedException {
			try {
				synchronized (wrappedTask) {
					wrappedTask.onStarted();
				}
				wrappedTask.execute();
			} finally {
				synchronized (wrappedTask) {
					wrappedTask.onFinished();
				}
			}
		}
	}
	
	private class EventLoopThread extends Thread {
		@Override
		public void run() {
			try {
				eventLoop();
			} catch (InterruptedException e) {
			}
			
			synchronized (EventLoop.this) {
				_aborted = true;
				cleanupJobs();
			}
		};
		
		@Override
		public String toString() {
			BrowsingContext context = _browsingUnit.getWindowBrowsingContext();
			Html5DocumentImpl document = context.getActiveDocument();
			URL address = (document != null)? document.getAddress() : null;
			String sourceUrl = (address != null)? address.toExternalForm() : "(no url)";
			return "EventLoop Thread - " + sourceUrl;
		}
	};
	
	private class SpinEventLoopResumeTask extends Task {
		private ScriptSettingsStack oldScriptSettingsStack;
		private Executable actionAfter;
		
		public SpinEventLoopResumeTask(Task oldTask, ScriptSettingsStack oldScriptSettingsStack, Executable actionAfter) {
			super(oldTask.getTaskSource(), oldTask.getDocument());
			
			this.oldScriptSettingsStack = oldScriptSettingsStack;
			this.actionAfter = actionAfter;
		}

		@Override
		public void execute() throws TaskAbortedException, InterruptedException  {
			ScriptSettingsStack stack = _browsingUnit.getScriptSettingsStack();
			stack.importScriptSettingsStack(oldScriptSettingsStack);
			
			actionAfter.execute();
		}
	}
	
	private static final LifetimeEndedException ABORTED_EXCEPTION = new LifetimeEndedException("Event loop has been aborted!");
	
	private TaskQueuesScheduler _taskScheduler;
	private BrowsingUnit _browsingUnit;
	private Task _runningTask;
	private boolean _aborted;
	private int terminationNestingLevel;
	
	private EventLoopThread executionThread;
		
	/**
	 * Constructs new event loop for passed browsing unit which will use passed task scheduler.¨
	 * 
	 * @param browsingUnit Browsing unit which is owner of this event loop.
	 * @param taskScheduler Scheduler to be used for scheduling tasks.
	 */
	public EventLoop(BrowsingUnit browsingUnit, TaskQueuesScheduler taskScheduler) {
		_taskScheduler = taskScheduler;
		_browsingUnit = browsingUnit;

		executionThread = new EventLoopThread();
		executionThread.start();
	}
	
	/**
	 * Constructs new event loop for passed browsing unit.
	 * 
	 * @param browsingUnit Browsing unit which is owner of this event loop.
	 * @see #EventLoop(BrowsingUnit, TaskQueuesScheduler)
	 */
	public EventLoop(BrowsingUnit browsingUnit) {
		this(browsingUnit, new RoundRobinScheduler());
	}
			
	/**
	 * Returns thread which executes tasks.
	 * 
	 * @return Thread which executes tasks.
	 */
	public synchronized Thread getEventThread() {
		return executionThread;
	}
	
	/**
	 * Decrements termination nesting level.
	 * 
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#termination-nesting-level">Termination nesting level</a>
	 */
	public synchronized void decrementTerminationNestingLevel() {
		terminationNestingLevel--;
	}
	
	/**
	 * Increments termination nesting level.
	 * 
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#termination-nesting-level">Termination nesting level</a>
	 */
	public synchronized void incrementTerminationNestingLevel() {
		terminationNestingLevel++;
	}
	
	/**
	 * Returns termination nesting level.
	 * 
	 * @return termination nesting level
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#termination-nesting-level">Termination nesting level</a>
	 */
	public synchronized int getTerminationNestingLevel() {
		return terminationNestingLevel;
	}
	
	/**
	 * Sets termination nesting level.
	 * 
	 * @param value New value of termination nesting level
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#termination-nesting-level">Termination nesting level</a>
	 */
	public synchronized void setTerminationNestingLevel(int value) {
		terminationNestingLevel = value;
	}
	
	/**
	 * Aborts synchronously this event loop.
	 * 
	 * @param join If is set, then current thread waits until event loop finishes.
	 * @throws InterruptedException if we are aborting current thread or if it occurs on join
	 */
	public synchronized void abort(boolean join) throws InterruptedException {		
		testForAbort();
		
		Thread currentThread = Thread.currentThread();
		boolean currentThreadInterrupted = currentThread.equals(executionThread);
		
		executionThread.interrupt();

		if (currentThreadInterrupted) {
			throw new InterruptedException(); // Ensures returning from the event loop
		} else if (join) {
			executionThread.join();
		}		
	}
	
	/**
	 * Tests whether is this event loop aborted.
	 * 
	 * @return True if is event loop aborted, otherwise false.
	 */
	public synchronized boolean isAborted() {
		return _aborted;
	}
	
	/**
	 * Tests if is this event loop running.
	 * 
	 * @return True if is event loop running, otherwise false.
	 */
	public synchronized boolean isRunning() {
		return (!_aborted)? executionThread.isAlive() : false;
	}
	
	/**
	 * Spins this event loop until some passed condition is met - after finishes conditionRunnable.
	 * 
	 * @param conditionRunnable Runnable that delays until some condition is met. 
	 * @param actionAfter Action which should executed after event loop places
	 *        spinned task into queue.
	 * @throws TaskAbortedException This exception is always thrown by this function. 
	 *         This ensures returning from the event loop stack trace.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/webappapis.html#spin-the-event-loop">Spin the event loop</a>
	 */
	public synchronized void spinForCondition(final Runnable conditionRunnable, final Executable actionAfter) throws TaskAbortedException {
		testForAbort();
		
		/* Method has to be invoked from the task thread */
		Thread currentThread = Thread.currentThread();
		final Task runningTask = _runningTask;
		if (currentThread.equals(executionThread) && runningTask != null) {
			ScriptSettingsStack scriptSettingsStack = _browsingUnit.getScriptSettingsStack();
			final ScriptSettingsStack oldScriptSettingsStack = scriptSettingsStack.clone();
			
			scriptSettingsStack.clean();
						
			Thread conditionThread = new Thread() {
				@Override
				public void run() {
					conditionRunnable.run();
					queueTask(new SpinEventLoopResumeTask(runningTask, oldScriptSettingsStack, actionAfter));
				}
			};
			conditionThread.start();
			throw new TaskAbortedException();
		}
	}
	
	/**
	 * Spins this event loop for a passed amount of time.
	 * 
	 * @param ms Amount of time how to long spin this event loop.
	 * @param actionAfter Action which should executed after time ran over.
	 * @throws TaskAbortedException Always thrown by this method.
	 * @see #spinForCondition(Runnable, Executable)
	 */
	public synchronized void spinForAmountTime(final int ms, Executable actionAfter) throws TaskAbortedException {
		spinForCondition(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(ms);
				} catch (InterruptedException e) {
					try {
						abort(false);
					} catch (InterruptedException e1) {
					}
				}
			}
		}, actionAfter);
	}

	/**
	 * Immediately spins this event. Similar to {@link #spinForAmountTime(int, Executable)}, but
	 * with zero time amount.
	 * 
	 * @param actionAfter Action which should executed after time ran over.
	 * @throws TaskAbortedException Always thrown by this method.
	 * @see #spinForCondition(Runnable, Executable)
	 */
	public synchronized void spin(Executable actionAfter) throws TaskAbortedException {
		spinForCondition(new Runnable() {
			@Override
			public void run() {}
		}, actionAfter);
	}
	
	/**
	 * Queues new task inside this event loop.
	 * 
	 * @param task New task to be queued.
	 */
	public synchronized void queueTask(Task task) {
		testForAbort();
		_taskScheduler.queueTask(new TaskWrapper(task));
	}
	
	/**
	 * Queues new task inside this event loop and pauses current thread.
	 * 
	 * @param task New task to be queued.
	 */
	public void queueTaskAndWait(Task task) throws InterruptedException {
		Task blockingTask = new TaskWrapper(task);
		
		queueTask(blockingTask);

		blockingTask.join();
	}
	
	/**
	 * Removes first task from the event loop which equals to the passed task.
	 * 
	 * @param task Task to be removed from the executing.
	 */
	public synchronized void removeFirstTask(Task task) {
		testForAbort();
		_taskScheduler.removeFirstTask(task);
	}
	
	/**
	 * Removes all tasks from the event loop which equal to the passed task.
	 * 
	 * @param task Task to be removed from the executing.
	 */
	public synchronized void removeAllTasks(Task task) {
		testForAbort();
		_taskScheduler.removeAllTasks(task);
	}
	
	/**
	 * Removes all tasks which have set passed document.
	 * 
	 * @param document Document which has task that should be removed.
	 */
	public synchronized void removeAllTasksWithDocument(final Html5DocumentImpl document) {
		testForAbort();
		_taskScheduler.filter(new Predicate<Task>() {
			@Override
			public boolean apply(Task task) {
				return task.getDocument() != document;
			}
		});
	}
	
	/**
	 * Filters given task source queue of this event loop by a predicate.
	 * 
	 * @param source Task source that should be filtered.
	 * @param predicate Predicate which ensures filtering. On success
	 *        the task is left untouched, otherwise will be removed.
	 */
	public synchronized void filter(TaskSource source, Predicate<Task> predicate) {
		testForAbort();
		_taskScheduler.filter(source, predicate);
	}
	
	/**
	 * Filters all task sources of this event loop by a predicate.
	 * 
	 * @param predicate Predicate which ensures filtering. On success
	 *        the task is left untouched, otherwise will be removed.
	 */
	public synchronized void filter(Predicate<Task> predicate) {
		testForAbort();
		_taskScheduler.filter(predicate);
	}
	
	/**
	 * Returns current running task.
	 * 
	 * @return Current running task of this event loop.
	 */
	public synchronized Task getRunningTask() {
		testForAbort();
		return _runningTask;
	}
	
	/**
	 * Returns associated task scheduler.
	 * 
	 * @return Task scheduler which drives this event loop.
	 */
	public synchronized TaskQueuesScheduler getTaskScheduler() {
		testForAbort();
		return _taskScheduler;
	}
	
	/**
	 * Removes all tasks with associated document that belongs to top-level document family.
	 */
	public void removeAllTopLevelDocumentFamilyTasks() {
		filter(TaskSource.HISTORY_TRAVERSAL, new Predicate<Task>() {
			@Override
			public boolean apply(Task task) {
				BrowsingContext topLevel = _browsingUnit.getWindowBrowsingContext();
				Collection<Html5DocumentImpl> documentFamily = topLevel.getDocumentFamily();
				return !documentFamily.contains(task.getDocument());
			}
		});
	}
	
	/**
	 * Inner mediating loop which executes tasks and pull tasks from the event queues.
	 *  
	 * @throws InterruptedException It is thrown if current thread is interrupted.
	 */
	private void eventLoop() throws InterruptedException {
		while (!executionThread.isInterrupted()) {	
			
			Task taskToRun = pullTask();
	
			synchronized (this) {
				_runningTask = taskToRun;
			}
			
			executeTask(taskToRun);
						
			synchronized (this) {
				_runningTask = null;
			}
		}
	}
	
	/**
	 * Pulls task from the task queues.
	 * 
	 * @return New task to be executed.
	 * 
	 * @throws InterruptedException It is thrown if current thread is interrupted.
	 */
	private Task pullTask() throws InterruptedException {
		Task task = null;
		try {
			task = _taskScheduler.pullTask();
		} catch (LifetimeEndedException e) {
			throw new InterruptedException();
		}
		
		if (executionThread.isInterrupted()) {
			throw new InterruptedException();
		}
		
		if (task == null) {
			throw new InterruptedException();
		}
		
		return task;
	}
	
	private void executeTask(Task task) throws InterruptedException {
		try {
			_taskScheduler.onTaskStarted(_runningTask);
			//System.out.println("Started task source: " + task.getTaskSource());
			_runningTask.execute();
		} catch (TaskAbortedException e) {
			// It is OK, task only ended earlier
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception e) {
			reportException(e);
		} finally {
			_taskScheduler.onTaskFinished(_runningTask);
		}
		
		if (executionThread.isInterrupted()) {
			throw new InterruptedException();
		}
	}
	
	private void reportException(Exception e) {
		if (e instanceof WrappedException) {
			e = ((WrappedException)e).unwrap();
		}
		
		e.printStackTrace();
	}
	
	private void testForAbort() {
		if (_aborted) {
			throw ABORTED_EXCEPTION;
		}
		
		if (_taskScheduler.isAborted()) {
			try {
				abort(false);
			} catch (InterruptedException e) {
				_aborted = true;
			}
			throw ABORTED_EXCEPTION;
		}
		
		if (!executionThread.isAlive()) {
			_aborted = true;
			throw ABORTED_EXCEPTION;
		}
	}
			
	private synchronized void cleanupJobs() {
		_runningTask = null;
		
		try {
			_taskScheduler.abort(false);
		} catch (InterruptedException e) {
		}
	}
}
