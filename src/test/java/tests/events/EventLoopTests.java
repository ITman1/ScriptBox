package tests.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.UserAgent;
import org.fit.cssbox.scriptbox.browser.WindowBrowsingContext;
import org.fit.cssbox.scriptbox.events.EventLoop;
import org.fit.cssbox.scriptbox.events.Executable;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.junit.Test;

public class EventLoopTests {
	private final static UserAgent userAgent;

	static {
		userAgent = new UserAgent();
	}

	private class Task_A_Duration_3  extends Task {
		protected List<String> results;
		public Task_A_Duration_3(List<String> results, BrowsingContext context) {
			super(TaskSource.NETWORKING, context);
			
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

		public Task_B_Duration_1(List<String> results, BrowsingContext context) {
			super(results, context);
		}

		@Override
		public void execute() throws TaskAbortedException, InterruptedException {
			results.add("Task_B_start");
			waitFor(1000);
			results.add("Task_B_end");
		}
	};
	
	private class Task_C_EventLoopSpin  extends Task_A_Duration_3 {

		public Task_C_EventLoopSpin(List<String> results, BrowsingContext context) {
			super(results, context);
		}

		@Override
		public void execute() throws TaskAbortedException, InterruptedException {
			results.add("Task_C_start");
			getEventLoop().spinForAmountTime(1000, new Executable() {
				@Override
				public void execute() throws TaskAbortedException, InterruptedException {
					getEventLoop().queueTask(new Task_B_Duration_1(results, getBrowsingContext()));
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
		eventLoop.queueTask(new Task_B_Duration_1(results, context));
		eventLoop.queueTask(new Task_C_EventLoopSpin(results, context));
		eventLoop.queueTask(new Task_A_Duration_3(results, context));
		
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
		
		eventLoop.queueTask(new Task_A_Duration_3(results, context));
		eventLoop.queueTask(new Task_B_Duration_1(results, context));
		eventLoop.queueTask(new Task_B_Duration_1(results, context));
		
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
}
