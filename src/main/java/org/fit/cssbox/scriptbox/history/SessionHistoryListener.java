package org.fit.cssbox.scriptbox.history;

import java.util.EventListener;

public interface SessionHistoryListener extends EventListener {
	public void onHistoryEvent(SessionHistoryEvent event);
}
