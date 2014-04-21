package tests.events;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.UserAgent;
import org.fit.cssbox.scriptbox.events.EventLoop;
import org.fit.cssbox.scriptbox.events.Executable;
import org.fit.cssbox.scriptbox.events.RoundRobinScheduler;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskQueuesScheduler;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.fit.cssbox.scriptbox.window.WindowBrowsingContext;
import org.junit.Test;

public class EventLoopTests {
	private final static UserAgent userAgent;

	static {
		userAgent = new UserAgent();
	}

	private class Task_A_Duration_3  extends Task {
		protected List<String> results;
		public Task_A_Duration_3(List<String> results, TaskSource source, BrowsingContext context) {
			super(source, context);
			
			this.results = results;
		}
		
		@Override
		public void execute() throws TaskAbortedException, InterruptedException {
			results.add("Task_A_start");
			waitFor(3000);
			results.add("Task_A_end");
		}
	};
	
	private class Task_B_Duration_1  extends Task_A_Duration_3 {

		public Task_B_Duration_1(List<String> results, TaskSource source, BrowsingContext context) {
			super(results, source, context);
		}

		@Override
		public void execute() throws TaskAbortedException, InterruptedException {
			results.add("Task_B_start");
			waitFor(1000);
			results.add("Task_B_end");
		}
	};
	
	private class Task_TaskSource_Duration_1  extends Task_A_Duration_3 {

		public Task_TaskSource_Duration_1(List<String> results, TaskSource source, BrowsingContext context) {
			super(results, source, context);
		}

		@Override
		public void execute() throws TaskAbortedException, InterruptedException {
			results.add(getTaskSource().toString());
			waitFor(1000);
		}
	};
	
	private class Task_C_EventLoopSpin  extends Task_A_Duration_3 {

		public Task_C_EventLoopSpin(List<String> results, TaskSource source, BrowsingContext context) {
			super(results, source, context);
		}

		@Override
		public void execute() throws TaskAbortedException, InterruptedException {
			results.add("Task_C_start");
			getEventLoop().spinForAmountTime(1000, new Executable() {
				@Override
				public void execute() throws TaskAbortedException, InterruptedException {
					getEventLoop().queueTask(new Task_B_Duration_1(results, TaskSource.NETWORKING, getBrowsingContext()));
					results.add("Task_C_end");
				}
			});
			results.add("Task_C_end");
		}
	};
	
	protected void waitFor(int ms) throws InterruptedException {
		Object waitLock = new Object();
		synchronized (waitLock) {
			waitLock.wait(ms);
		}
	}
	
	@Test
	public void TestSpinEventLoopOrder() throws InterruptedException {
		BrowsingUnit browsingUnit = userAgent.openBrowsingUnit();
		WindowBrowsingContext context = browsingUnit.getWindowBrowsingContext();
		EventLoop eventLoop = browsingUnit.getEventLoop();
		
		List<String> results = new ArrayList<String>();
		eventLoop.queueTask(new Task_B_Duration_1(results, TaskSource.NETWORKING, context));
		eventLoop.queueTask(new Task_C_EventLoopSpin(results, TaskSource.NETWORKING, context));
		eventLoop.queueTask(new Task_A_Duration_3(results, TaskSource.NETWORKING, context));
		
		waitFor(8000);
		String expecteds[] = {"Task_B_start", "Task_B_end", "Task_C_start", "Task_A_start", "Task_A_end", "Task_C_end", "Task_B_start", "Task_B_end"};
		String[] actuals = results.toArray(new String[results.size()]);

		assertArrayEquals(expecteds, actuals);
	}
	
	@Test
	public void TestSpinEventLoopAbort() throws InterruptedException {
		BrowsingUnit browsingUnit = userAgent.openBrowsingUnit();
		WindowBrowsingContext context = browsingUnit.getWindowBrowsingContext();
		EventLoop eventLoop = browsingUnit.getEventLoop();
		List<String> results = new ArrayList<String>();
		
		eventLoop.queueTask(new Task_A_Duration_3(results, TaskSource.NETWORKING, context));
		eventLoop.queueTask(new Task_B_Duration_1(results, TaskSource.NETWORKING, context));
		eventLoop.queueTask(new Task_B_Duration_1(results, TaskSource.NETWORKING, context));
		
		assertTrue(eventLoop.isRunning());
		
		waitFor(1000);
		try {
			eventLoop.abort(false);
		} catch (InterruptedException e) {
		}
		waitFor(4000);
		
		String expecteds[] = {"Task_A_start"};
		String[] actuals = results.toArray(new String[results.size()]);
		assertArrayEquals(expecteds, actuals);
		
		assertFalse(eventLoop.isRunning());
	}
	
	@Test
	public void TestRoundRobinScheduler() throws InterruptedException {
		BrowsingUnit browsingUnit = userAgent.openBrowsingUnit();
		WindowBrowsingContext context = browsingUnit.getWindowBrowsingContext();
		EventLoop eventLoop = browsingUnit.getEventLoop();
		TaskQueuesScheduler scheduler = eventLoop.getTaskScheduler();
		List<String> results = new ArrayList<String>();
		
		if (!(scheduler instanceof RoundRobinScheduler)) {
			return;
		}
		
		Task domTask = new Task_TaskSource_Duration_1(results, TaskSource.DOM_MANIPULATION, context);
		Task historyTask = new Task_TaskSource_Duration_1(results, TaskSource.HISTORY_TRAVERSAL, context);
		Task networkTask = new Task_TaskSource_Duration_1(results, TaskSource.NETWORKING, context);
		Task userTask = new Task_TaskSource_Duration_1(results, TaskSource.USER_INTERACTION, context);
		
		/* Only to establish sources list */
		eventLoop.queueTask(domTask);
		eventLoop.queueTask(historyTask);
		eventLoop.queueTask(networkTask);
		eventLoop.queueTask(userTask);
		waitFor(6000);
		results.clear();
		
		/* This should be now pure round robin */
		eventLoop.queueTask(domTask);
		eventLoop.queueTask(domTask);
		eventLoop.queueTask(domTask);
		eventLoop.queueTask(domTask);
		eventLoop.queueTask(historyTask);
		eventLoop.queueTask(networkTask);
		eventLoop.queueTask(userTask);
		
		waitFor(9000);
		
		assertTrue(results.get(0).equals(TaskSource.DOM_MANIPULATION.toString()));
		assertTrue(results.indexOf(historyTask) < results.indexOf(TaskSource.HISTORY_TRAVERSAL.toString()));
		assertTrue(results.indexOf(domTask) < results.indexOf(TaskSource.NETWORKING.toString()));
		assertTrue(results.indexOf(networkTask) < results.indexOf(TaskSource.USER_INTERACTION.toString()));
		assertTrue(results.get(6).equals(TaskSource.DOM_MANIPULATION.toString()));
	}
}
