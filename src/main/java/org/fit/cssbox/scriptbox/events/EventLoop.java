package org.fit.cssbox.scriptbox.events;

import java.util.ArrayList;

public class EventLoop extends Thread {
	private Object _pauseMonitor;
	protected ArrayList<Task> _taskQueue;
	protected Thread executionThread;
	
	public EventLoop() {
		_pauseMonitor = new Object();
		
		start();
	}
	
	public void run() {
		while (true) {
			synchronized (_pauseMonitor) {
				while (_taskQueue.isEmpty()) {
					try {
						_pauseMonitor.wait();
					} catch (Exception e) {}
			    }
			}
			
			Task task = pullTask();
			
			task.execute();
		}
	}
	
	public synchronized void appendTask(Task task) {
		synchronized (_pauseMonitor) {
			_taskQueue.add(task);
			_pauseMonitor.notifyAll();
		}
	}
	
	protected synchronized Task pullTask() {
		synchronized (_pauseMonitor) {
			return _taskQueue.remove(0);
		}
	}
}
