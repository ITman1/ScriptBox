package org.fit.cssbox.scriptbox.document.script;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;

public abstract class ParserFinishedTask extends Task {
	protected Exception exception;

	public ParserFinishedTask(TaskSource source, Html5DocumentImpl document) {
		super(source, document);
	}

	public Exception getException() {
		return exception;
	}
}
