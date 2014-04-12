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

import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.exceptions.LifetimeEndedException;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.fit.cssbox.scriptbox.script.ScriptSettingsStack;

import com.google.common.base.Predicate;

public class EventLoop {
	protected class TaskWrapper extends Task {
		protected Task wrappedTask;
				
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
	
	protected class ExecutionThread extends Thread {
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
	};
	
	protected class SpinEventLoopResumeTask extends Task {
		protected ScriptSettingsStack oldScriptSettingsStack;
		protected Executable actionAfter;
		
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
	
	protected static final LifetimeEndedException ABORTED_EXCEPTION = new LifetimeEndedException("Event loop has been aborted!");
	
	protected TaskQueuesScheduler _taskScheduler;
	protected BrowsingUnit _browsingUnit;
	protected Task _runningTask;
	protected boolean _aborted;
	
	protected ExecutionThread executionThread;
		
	public EventLoop(BrowsingUnit browsingUnit, TaskQueuesScheduler taskScheduler) {
		_taskScheduler = taskScheduler;
		_browsingUnit = browsingUnit;

		executionThread = new ExecutionThread();
		executionThread.start();
	}
	
	public EventLoop(BrowsingUnit browsingUnit) {
		this(browsingUnit, new RoundRobinScheduler());
	}
			
	public synchronized Thread getEventThread() {
		return executionThread;
	}
	
	/*
	 * throws InterruptedException if we are aborting current thread or if it occurs on join
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
	
	public synchronized boolean isAborted() {
		return _aborted;
	}
	
	public synchronized boolean isRunning() {
		return (!_aborted)? executionThread.isAlive() : false;
	}
	
	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#spin-the-event-loop
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

	public synchronized void spin(Executable actionAfter) throws TaskAbortedException {
		spinForCondition(new Runnable() {
			@Override
			public void run() {}
		}, actionAfter);
	}
	
	public synchronized void queueTask(Task task) {
		testForAbort();
		_taskScheduler.queueTask(new TaskWrapper(task));
	}
	
	public void queueTaskAndWait(Task task) throws InterruptedException {
		Task blockingTask = new TaskWrapper(task);
		
		queueTask(blockingTask);

		blockingTask.join();
	}
	
	public synchronized void removeFirstTask(Task task) {
		testForAbort();
		_taskScheduler.removeFirstTask(task);
	}
	
	public synchronized void removeAllTasks(Task task) {
		testForAbort();
		_taskScheduler.removeAllTasks(task);
	}
	
	public synchronized void removeAllTasksWithDocument(final Html5DocumentImpl document) {
		testForAbort();
		_taskScheduler.filter(new Predicate<Task>() {
			@Override
			public boolean apply(Task task) {
				return task.getDocument() != document;
			}
		});
	}
	
	public synchronized void filter(TaskSource source, Predicate<Task> predicate) {
		testForAbort();
		_taskScheduler.filter(source, predicate);
	}
	
	public synchronized void filter(Predicate<Task> predicate) {
		testForAbort();
		_taskScheduler.filter(predicate);
	}
	
	public synchronized Task getRunningTask() {
		testForAbort();
		return _runningTask;
	}
	
	public synchronized TaskQueuesScheduler getTaskScheduler() {
		testForAbort();
		return _taskScheduler;
	}
	
	protected void eventLoop() throws InterruptedException {
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
	
	protected Task pullTask() throws InterruptedException {
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
	
	protected void executeTask(Task task) throws InterruptedException {
		try {
			_taskScheduler.onTaskStarted(_runningTask);
			_runningTask.execute();
		} catch (TaskAbortedException e) {
			// It is OK, task only ended earlier
		} catch (LifetimeEndedException e) {
			throw new InterruptedException();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			_taskScheduler.onTaskFinished(_runningTask);
		}
		
		if (executionThread.isInterrupted()) {
			throw new InterruptedException();
		}
	}
	
	protected void testForAbort() {
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
			
	protected synchronized void cleanupJobs() {
		_runningTask = null;
		
		try {
			_taskScheduler.abort(false);
		} catch (InterruptedException e) {
		}
	}
}
