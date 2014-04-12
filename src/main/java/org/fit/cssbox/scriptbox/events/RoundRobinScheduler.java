/**
 * RoundRobinScheduler.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RoundRobinScheduler extends TaskQueuesScheduler {	
	protected class InactivityChecker extends Timer {
		class TaskSourceInactivityCheckTask extends TimerTask {
		    public void run() {
		    	performCheck();
		    }
		};
		
		protected Map<TaskSource, Long> taskSourcesExecutionTimes;
		protected long tasksExecuted;
		
		public InactivityChecker() {
			this.taskSourcesExecutionTimes = new HashMap<TaskSource, Long>();
		}
		
	   	public synchronized void taskCompleted(Task task) {
	   		long finishedTime = System.nanoTime();
   			taskSourcesExecutionTimes.put(task.getTaskSource(), finishedTime);
   			tasksExecuted++;
	   	}
	   	
	   	public synchronized void performCheck() {
	   		synchronized (RoundRobinScheduler.this) {
	    		List<TaskSource> taskSourcesCopy = new ArrayList<TaskSource>(queuedTaskSources);

	    		for (TaskSource source : taskSourcesCopy) {
	    			if (isInactiveTaskSource(source)) {
	    				System.out.println(System.identityHashCode(RoundRobinScheduler.this) + " - removed: " + source.toString());
	    				queuedTaskSources.remove(source);
	    				taskSourcesExecutionTimes.get(source);
	    				if (sourcesListPosition + 1 == queuedTaskSources.size()) {
	    					sourcesListPosition--;
	    				}
	    			}
	    		}
	    	}
	   		
	   		tasksExecuted = 0;
	   	}
	   	
		protected synchronized boolean isInactiveTaskSource(TaskSource taskSource) {
			long currentTime = System.nanoTime();
			Long lastTime;
			synchronized (this) {
				lastTime = taskSourcesExecutionTimes.get(taskSource);
			}
			
			if (lastTime != null) {
				if (taskQueues.isEmpty(taskSource)) {
					long inactiveTime = Math.abs(currentTime - lastTime);
					
					return (tasksExecuted > 3 && inactiveTime > 300000000L) || inactiveTime > 2000000000L; // Longer than 0,3sec if tasks was not busy if task queue is busy, then 2 seconds
				}
			}
			return false;
		}
	   	
	   	public synchronized void schedule(int ms) {
	   		schedule(new TaskSourceInactivityCheckTask(), 0, ms);
	   	}
	};
	
	protected int sourcesListPosition;
	protected List<TaskSource> queuedTaskSources;
	protected InactivityChecker inactivityChecker;
	
	RoundRobinScheduler() {
		this.sourcesListPosition = -1;
		this.queuedTaskSources = new ArrayList<TaskSource>();
		this.inactivityChecker = new InactivityChecker();
		this.inactivityChecker.schedule(500);
	}
	
	@Override
	public void queueTask(Task task) {
		TaskSource source = task.getTaskSource();
		synchronized (this) {
			if (!queuedTaskSources.contains(source)) {
				System.out.println(System.identityHashCode(RoundRobinScheduler.this) + " - added: " + source.toString());
				queuedTaskSources.add(source);
			} 			
		}
		
		super.queueTask(task);
	}
	
	@Override
	protected Task scheduleTask() {
		synchronized (this) {
			if (queuedTaskSources.isEmpty()) { // Division by zero check
				return null;
			}
			
			sourcesListPosition = (sourcesListPosition + 1) % queuedTaskSources.size();
			TaskSource scheduledTaskSource = queuedTaskSources.get(sourcesListPosition);
				
			return taskQueues.pullTask(scheduledTaskSource);
		}
	}

	@Override
	protected void onTaskCompletedExecution(Task task, long taskDuration) {
		inactivityChecker.taskCompleted(task);
	}
	
	@Override
	protected void cleanupJobs() {
		super.cleanupJobs();
		inactivityChecker.cancel();
	}
}
