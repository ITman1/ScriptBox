package org.fit.cssbox.scriptbox.dom.events;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.w3c.dom.events.Event;

public class DispatcherTask extends Task {
	protected org.w3c.dom.events.EventTarget target;
	protected Event event;
	
	public DispatcherTask(Html5DocumentImpl document, org.w3c.dom.events.EventTarget target, Event event) {
		super(TaskSource.USER_INTERACTION, document);
		
		this.target = target;
		this.event = event;
	}

	@Override
	public void execute() throws TaskAbortedException, InterruptedException {
		target.dispatchEvent(event);
	}

}
