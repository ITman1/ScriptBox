package org.fit.cssbox.scriptbox.events;

import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;

public interface Executable {
	public void execute() throws TaskAbortedException, InterruptedException;
}
