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
			
	public void abort() {
		abort(true);
	}
	
	public synchronized void spinForCondition(final Runnable conditionRunnable, Runnable actionAfter) {
		/* Method has to be invoked from the task */
		if (_runningTask != null) {
			ScriptSettingsStack scriptSettingsStack = _browsingUnit.getScriptSettingsStack();
			GlobalScriptCleanupJobs cleanupJobs = _browsingUnit.getGlobalScriptCleanupJobs();
			final TaskSource taskSource = _runningTask.getTaskSource();
			final ScriptSettingsStack oldScriptSettingsStack = scriptSettingsStack.clone();
			
			scriptSettingsStack.clean();
			cleanupJobs.runAll();
			
			
			Thread conditionThread = new Thread() {
				@Override
				public void run() {
					conditionRunnable.run();
				}
			};
			conditionThread.start();
			abort(false);
		}
	}
	
	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#spin-the-event-loop
	 */
	public synchronized void spinForAmountTime(final int ms, Runnable actionAfter) {
		spinForCondition(new Runnable() {
			
			@Override
			public void run() {
				Object waitObj = new Object();		
				try {
					waitObj.wait(ms);
				} catch (InterruptedException e) {
					// TODO: Throw an exception
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
	
	@SuppressWarnings("deprecation")
	protected synchronized void abort(boolean synced) {
		if (synced) {
			_aborted = true;
			
			synchronized (_pauseMonitor) {
				_pauseMonitor.notifyAll();
			}
		} else {
			if (_runningTask != null) {
				_runningTask.onCancellation();
			}

			executionThread.stop();
		}
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
