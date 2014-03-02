package org.fit.cssbox.scriptbox.events;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
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
		protected Executable actionAfter;
		
		public SpinEventLoopResumeTask(Task oldTask, ScriptSettingsStack oldScriptSettingsStack, Executable actionAfter) {
			super(oldTask.getTaskSource(), oldTask.getDocument());
			
			this.actionAfter = actionAfter;
		}

		@Override
		public void execute() throws InterruptedException  {
			ScriptSettingsStack stack = _browsingUnit.getScriptSettingsStack();
			stack.importScriptSettingsStack(oldScriptSettingsStack);
			
			actionAfter.execute();
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
	protected Task _runningTask;
	protected boolean _aborted;
	
	protected ExecutionThread executionThread;
		
	public EventLoop(BrowsingUnit browsingUnit) {
		_pauseMonitor = new Object();
		_taskQueues = new TaskQueues();
		_browsingUnit = browsingUnit;
		sourcesListPosition = -1;

		executionThread = new ExecutionThread();
		executionThread.start();
	}
			
	/*
	 * throws InterruptedException if we are aborting current thread or if it occurs on join
	 */
	public synchronized void abort(boolean join) throws InterruptedException {		
		if (!executionThread.isAlive() || _aborted) {
			return;
		}
		
		Thread currentThread = Thread.currentThread();
		boolean currentThreadInterrupted = currentThread.equals(executionThread);
		
		_aborted = true;
		executionThread.interrupt();

		if (currentThreadInterrupted) {
			throw new InterruptedException();
		} else if (join) {
			executionThread.join();
		}		
	}
	
	public synchronized boolean isAborted() {
		return _aborted;
	}
	
	public synchronized boolean isRunning() {
		return (executionThread != null && !_aborted)? executionThread.isAlive() : false;
	}
	
	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#spin-the-event-loop
	 */
	public synchronized void spinForCondition(final Runnable conditionRunnable, final Executable actionAfter) throws InterruptedException {
		if (_aborted) {
			return;
		}
		
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
					conditionRunnable.run();
					queueTask(new SpinEventLoopResumeTask(runningTask, oldScriptSettingsStack, actionAfter));
				}
			};
			conditionThread.start();
			executionThread.interrupt();
			throw new InterruptedException();
		}
	}
	

	public synchronized void spinForAmountTime(final int ms, Executable actionAfter) throws InterruptedException {
		spinForCondition(new Runnable() {
			
			@Override
			public void run() {
				Object waitObj = new Object();		
				synchronized (waitObj) {
					try {
						waitObj.wait(ms);
					} catch (InterruptedException e) {
					}
				}
			}
		}, actionAfter);
	}
	
	public synchronized void queueTask(Task task) {
		if (_aborted) {
			return;
		}
		
		synchronized (_pauseMonitor) {
			_taskQueues.queueTask(task);
			_pauseMonitor.notifyAll();
		}
	}
	
	public synchronized void removeTask(Task task) {
		if (_aborted) {
			return;
		}
		_taskQueues.removeTask(task);
	}
	
	public synchronized void filter(TaskSource source, Predicate<Task> predicate) {
		if (_aborted) {
			return;
		}
		_taskQueues.filter(source, predicate);
	}
	
	public synchronized Task getRunningTask() {
		if (_aborted) {
			return null;
		}
		return _runningTask;
	}
	
	protected void eventLoop() {
		while (!_aborted) {
			synchronized (_pauseMonitor) {
				while (_taskQueues.isEmpty() && !_aborted) {
					try {
						_pauseMonitor.wait();
					} catch (InterruptedException e) {
					}
				}
			}
			
			if (_aborted) {
				break;
			}
			
			synchronized (this) {
				_runningTask = pullTask();
			}
			
			try {
				_runningTask.execute();
			} catch (InterruptedException e) {
			}
			
			if (_aborted) {
				break;
			}
			
			synchronized (this) {
				_runningTask = null;
			}
		}
		
		cleanupJobs();
		return;
	}
	
	protected long getCpuTime( ) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
		return bean.isCurrentThreadCpuTimeSupported()? bean.getCurrentThreadCpuTime( ) : 0L;
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
