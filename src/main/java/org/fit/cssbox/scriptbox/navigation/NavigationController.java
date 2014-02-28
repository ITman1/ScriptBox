package org.fit.cssbox.scriptbox.navigation;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

public class NavigationController {
	private NavigationAttemptListener navigationAttemptListener = new NavigationAttemptListener() {
		
		@Override
		public void onMatured(NavigationAttempt attempt) {
			removeNavigationAttempt(attempt);
		}
		
		@Override
		public void onEffectiveDestinationContextSelected(NavigationAttempt attempt, BrowsingContext context) {
			NavigationController controller = context.getNavigationController();
			
			if (controller != null) {
				controller.addNavigationAttempt(attempt);
			}
		}
		
		@Override
		public void onCancelled(NavigationAttempt attempt) {
			removeNavigationAttempt(attempt);
		}
	};
	
	private BrowsingContext context;
	private List<NavigationAttempt> navigationAttempts;

	
	public NavigationController(BrowsingContext context) {
		this.context = context;
		
		this.navigationAttempts = new ArrayList<NavigationAttempt>();
	}
	

	public void update(SessionHistoryEntry entry) {
		UpdateNavigationAttempt attempt = new UpdateNavigationAttempt(this, entry);
		
		attempt.perform(navigationAttemptListener);
	}
	
	public void navigate(BrowsingContext sourceBrowsingContext, URL url, boolean exceptionEnabled, boolean explicitSelfNavigationOverride) {
		NewNavigationAttempt attempt = new NewNavigationAttempt(this, sourceBrowsingContext, url, exceptionEnabled, explicitSelfNavigationOverride);
		
		attempt.perform(navigationAttemptListener);
	}
	
	public synchronized void cancelAllNavigationAttempts() {
		ImmutableList<NavigationAttempt> attempts = ImmutableList.copyOf(navigationAttempts);
		
		for (NavigationAttempt attempt : attempts) {
			attempt.cancel();
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
	
	private void addNavigationAttempt(NavigationAttempt attempt) {
		synchronized (navigationAttempts) {
			navigationAttempts.add(attempt);
		}
		
	}

	private void removeNavigationAttempt(NavigationAttempt attempt) {
		synchronized (navigationAttempts) {
			navigationAttempts.remove(attempt);
		}
	}
}
