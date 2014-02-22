package org.fit.cssbox.scriptbox.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;

public class EventLoop extends Thread {
	private Object _pauseMonitor;
	protected TaskQueues _taskQueues;
	protected Thread executionThread;
	protected int sourcesListPosition;
	
	protected static List<TaskSource> sourcesList;
	
	static {
		sourcesList = new ArrayList<TaskSource>();
		sourcesList.add(TaskSource.DOM_MANIPULATION);
		sourcesList.add(TaskSource.HISTORY_TRAVERSAL);
		sourcesList.add(TaskSource.NETWORKING);
		sourcesList.add(TaskSource.USER_INTERACTION);
	}
	
	public EventLoop() {
		_pauseMonitor = new Object();
		sourcesListPosition = -1;
		
		start();
	}
	
	public void run() {
		while (true) {
			synchronized (_pauseMonitor) {
				while (_taskQueues.isEmpty()) {
					try {
						_pauseMonitor.wait();
					} catch (Exception e) {}
			    }
			}
			
			Task task = pullTask();
			
			task.execute();
		}
	}
	
	public synchronized void queueTask(Task task) {
		synchronized (_pauseMonitor) {
			_taskQueues.queueTask(task);
			_pauseMonitor.notifyAll();
		}
	}
	
	/*
	 * Not thread safe.
	 */
	public void filter(TaskSource source, Predicate<Task> predicate) {
		
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
