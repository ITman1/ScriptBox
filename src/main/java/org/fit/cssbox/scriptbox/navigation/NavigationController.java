package org.fit.cssbox.scriptbox.navigation;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.scriptbox.history.SessionHistoryEvent;
import org.fit.cssbox.scriptbox.history.SessionHistoryListener;
import org.fit.cssbox.scriptbox.navigation.NavigationControllerEvent.EventType;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

public class NavigationController {
	private NavigationAttemptListener navigationAttemptListener = new NavigationAttemptListener() {
		
		@Override
		public void onMatured(NavigationAttempt attempt) {

		}
		
		@Override
		public void onEffectiveDestinationContextSelected(NavigationAttempt attempt, BrowsingContext context) {
			NavigationController controller = context.getNavigationController();
			
			if (controller != null) {
				controller.addNavigationAttempt(attempt);
				controller.fireNavigationAttemptEvent(attempt, NavigationControllerEvent.EventType.NAVIGATION_NEW);
			}
		}
		
		@Override
		public void onCancelled(NavigationAttempt attempt) {
			synchronized (NavigationController.this) {
				removeNavigationAttempt(attempt);
				fireNavigationAttemptEvent(attempt, EventType.NAVIGATION_CANCELLED);
			}
		}

		@Override
		public void onCompleted(NavigationAttempt attempt) {
			synchronized (NavigationController.this) {
				removeNavigationAttempt(attempt);
				fireNavigationAttemptEvent(attempt, EventType.NAVIGATION_COMPLETED);
			}
		}
	};
	
	private BrowsingContext context;
	private List<NavigationAttempt> navigationAttempts;
	private Set<NavigationControllerListener> listeners;

	
	public NavigationController(BrowsingContext context) {
		this.context = context;
		this.listeners = new HashSet<NavigationControllerListener>();
		
		this.navigationAttempts = new ArrayList<NavigationAttempt>();
	}
	

	public synchronized NavigationAttempt update(SessionHistoryEntry entry) {
		UpdateNavigationAttempt attempt = new UpdateNavigationAttempt(this, entry);
			
		attempt.perform(navigationAttemptListener);
		
		return attempt;
	}
	
	public synchronized NavigationAttempt navigate(BrowsingContext sourceBrowsingContext, URL url, boolean exceptionEnabled, boolean explicitSelfNavigationOverride, boolean replacementEnabled) {
		NewNavigationAttempt attempt = new NewNavigationAttempt(this, sourceBrowsingContext, url, exceptionEnabled, explicitSelfNavigationOverride, replacementEnabled);
		
		attempt.perform(navigationAttemptListener);
		
		return attempt;
	}
	
	public synchronized void cancelAllNavigationAttempts() {
		ImmutableList<NavigationAttempt> attempts = ImmutableList.copyOf(navigationAttempts);
		
		for (NavigationAttempt attempt : attempts) {
			attempt.cancel();
		}
	}
	
	public synchronized void cancelNavigationAttempts(Predicate<NavigationAttempt> attemptPredicate) {
		synchronized (navigationAttempts) {
			for (NavigationAttempt attempt : navigationAttempts) {
				if (attemptPredicate.apply(attempt)) {
					attempt.cancel();
				}
			}
		}
	}
	
	public boolean existsNavigationAttempt(Predicate<NavigationAttempt> attemptPredicate) {
		synchronized (navigationAttempts) {
			for (NavigationAttempt attempt : navigationAttempts) {
				if (attemptPredicate.apply(attempt)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public BrowsingContext getBrowsingContext() {
		return context;
	}
	
	public void addListener(NavigationControllerListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(NavigationControllerListener listener) {
		listeners.remove(listener);
	}
	
	private void fireNavigationAttemptEvent(NavigationAttempt attempt, NavigationControllerEvent.EventType eventType) {
		NavigationControllerEvent event = new NavigationControllerEvent(this, eventType, attempt);
		
		for (NavigationControllerListener listener : listeners) {
			listener.onNavigationEvent(event);
		}
	}
	
	private synchronized void addNavigationAttempt(NavigationAttempt attempt) {
		navigationAttempts.add(attempt);		
	}

	private synchronized void removeNavigationAttempt(NavigationAttempt attempt) {
		navigationAttempts.remove(attempt);
	}
}
