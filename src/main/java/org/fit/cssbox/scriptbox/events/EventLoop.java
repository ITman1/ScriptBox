package org.fit.cssbox.scriptbox.events;

import java.util.ArrayList;
import java.util.List;

import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.script.ScriptSettingsStack;
import org.fit.cssbox.scriptbox.script.GlobalScriptCleanupJobs;

import com.google.common.base.Predicate;

public class EventLoop {
	protected class ExecutionThread extends Thread {
		@Override
		public void run() {
			eventLoop();
		};
	};
	
	protected class SpinEventLoopResumeTask extends Task {
		protected ScriptSettingsStack oldScriptSettingsStack;
		protected Runnable actionAfter;
		
		public SpinEventLoopResumeTask(Task oldTask, ScriptSettingsStack oldScriptSettingsStack, Runnable actionAfter) {
			super(oldTask.getTaskSource(), oldTask.getDocument());
		}

		@Override
		public void run() {
			ScriptSettingsStack stack = _browsingUnit.getScriptSettingsStack();
			stack.importScriptSettingsStack(oldScriptSettingsStack);
			
			actionAfter.run();
		}
	}
	
	protected static List<TaskSource> sourcesList;
	static {
		sourcesList = new ArrayList<TaskSource>();
		sourcesList.add(TaskSource.DOM_MANIPULATION);
		sourcesList.add(TaskSource.HISTORY_TRAVERSAL);
		sourcesList.add(TaskSource.NETWORKING);
		sourcesList.add(TaskSource.USER_INTERACTION);
	}
	
	protected Object _pauseMonitor;
	protected TaskQueues _taskQueues;
	protected int sourcesListPosition;
	protected BrowsingUnit _browsingUnit;
	protected boolean _aborted;
	protected Task _runningTask;
	
	protected ExecutionThread executionThread;
		
	public EventLoop(BrowsingUnit browsingUnit) {
		_pauseMonitor = new Object();
		_taskQueues = new TaskQueues();
		_browsingUnit = browsingUnit;
		sourcesListPosition = -1;
		
		executionThread = new ExecutionThread();
		executionThread.start();
	}
			
	public void abort(boolean join) {
		abort(true, join);
	}
	
	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#spin-the-event-loop
	 */
	public synchronized void spinForCondition(final Runnable conditionRunnable, final Runnable actionAfter) {
		/* Method has to be invoked from the task thread */
		Thread currentThread = Thread.currentThread();
		final Task runningTask = _runningTask;
		if (currentThread.equals(executionThread) && runningTask != null) {
			ScriptSettingsStack scriptSettingsStack = _browsingUnit.getScriptSettingsStack();
			GlobalScriptCleanupJobs cleanupJobs = _browsingUnit.getGlobalScriptCleanupJobs();
			final ScriptSettingsStack oldScriptSettingsStack = scriptSettingsStack.clone();
			
			scriptSettingsStack.clean();
			cleanupJobs.runAll();
						
			Thread conditionThread = new Thread() {
				@Override
				public void run() {
					EventLoop.this.start();
					conditionRunnable.run();
					queueTask(new SpinEventLoopResumeTask(runningTask, oldScriptSettingsStack, actionAfter));
				}
			};
			conditionThread.start();
			abort(false, false);
		}
	}
	

	public synchronized void spinForAmountTime(final int ms, Runnable actionAfter) {
		spinForCondition(new Runnable() {
			
			@Override
			public void run() {
				Object waitObj = new Object();		
				try {
					waitObj.wait(ms);
				} catch (InterruptedException e) {
				}
			}
		}, actionAfter);
	}
	
	public synchronized void queueTask(Task task) {
		synchronized (_pauseMonitor) {
			_taskQueues.queueTask(task);
			_pauseMonitor.notifyAll();
		}
	}
	
	public synchronized void removeTask(Task task) {
		_taskQueues.removeTask(task);
	}
	
	public synchronized void filter(TaskSource source, Predicate<Task> predicate) {
		_taskQueues.filter(source, predicate);
	}
	
	public synchronized Task getRunningTask() {
		return _runningTask;
	}
	
	protected void eventLoop() {
		while (true) {
			synchronized (_pauseMonitor) {
				while (_taskQueues.isEmpty() && !_aborted) {
					try {
						_pauseMonitor.wait();
					} catch (Exception e) {}
			    }
			}
			
			if (_aborted) {
				cleanupJobs();
				return;
			}
			
			synchronized (this) {
				_runningTask = pullTask();
			}
			
			_runningTask.run();
			
			synchronized (this) {
				_runningTask = null;
			}
		}
	}
	
	protected synchronized void start() {
		if (executionThread != null && executionThread.isAlive()) {
			abort(true, true);
		}
		executionThread = new ExecutionThread();
		executionThread.start();
	}
	
	@SuppressWarnings("deprecation")
	protected synchronized void abort(boolean synced, boolean join) {
		if (executionThread == null || !executionThread.isAlive()) {
			return;
		}
		
		if (synced) {
			_aborted = true;
			
			synchronized (_pauseMonitor) {
				_pauseMonitor.notifyAll();
			}
			
			if (join) {
				try {
					executionThread.join();
				} catch (InterruptedException e) {
				} finally {}
			}
		} else {
			if (_runningTask != null) {
				_runningTask.onCancellation();
			}
			executionThread.stop();
		}
		
		executionThread = null;
	}
	
	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#perform-a-microtask-checkpoint
	 */
	protected void performMicrotaskCheckpoint() {
		
	}
	
	protected synchronized Task pullTask() {
		synchronized (_pauseMonitor) {
			while (true) {
				/* FIXME: Fix it to something more sophisticated than round robin. */
				sourcesListPosition = (sourcesListPosition + 1) % sourcesList.size();
				TaskSource source = sourcesList.get(sourcesListPosition);
				
				Task task = _taskQueues.pullTask(source);
				
				if (task != null) {
					return task;
				}
			}
		}
	}
	
	protected void cleanupJobs() {
		
	}
}
