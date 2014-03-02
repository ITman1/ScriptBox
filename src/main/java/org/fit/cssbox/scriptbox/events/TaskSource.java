package org.fit.cssbox.scriptbox.events;

public enum TaskSource {
	DOM_MANIPULATION(15),
	USER_INTERACTION(55),
	NETWORKING(15),
	HISTORY_TRAVERSAL(15);
	
	private int priority;
	
	TaskSource(int priority) {
		this.priority = priority;
	}
	
	public int getPriority() {
		return priority;
	}
}
