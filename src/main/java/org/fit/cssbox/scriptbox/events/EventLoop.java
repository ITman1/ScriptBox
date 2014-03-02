package org.fit.cssbox.scriptbox.events;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;

public class EventLoop {
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
	protected boolean _aborted;
	
	protected Thread executionThread = new Thread() {
		@Override
		public void run() {
			eventLoop();
		};
	};
		
	public EventLoop() {
		_pauseMonitor = new Object();
		_taskQueues = new TaskQueues();
		sourcesListPosition = -1;
		
		executionThread.start();
	}
			
	public void abort() {
		_aborted = true;
		
		synchronized (_pauseMonitor) {
			_pauseMonitor.notifyAll();
		}
	}
	
	public synchronized void spinForAmountTime(int ms, Runnable actionAfter) {
		
	}
	
	public synchronized void queueTask(Task task) {
		synchronized (_pauseMonitor) {
			_taskQueues.queueTask(task);
			_pauseMonitor.notifyAll();
		}
	}
	
	public synchronized void filter(TaskSource source, Predicate<Task> predicate) {
		_taskQueues.filter(source, predicate);
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
			
			Task task = pullTask();
			
			task.run();
		}
	}
	
	protected void cleanupJobs() {
		
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
	

}
