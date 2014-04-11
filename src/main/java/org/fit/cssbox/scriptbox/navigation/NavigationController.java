package org.fit.cssbox.scriptbox.navigation;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.EventLoop;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.scriptbox.navigation.NavigationControllerEvent.EventType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.html.HTMLAnchorElement;
import org.w3c.dom.html.HTMLAreaElement;
import org.w3c.dom.html.HTMLBaseElement;

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
	
	public static void followHyperlink(Element subject) {
       	followHyperlink(subject, null);
	}
	
	public static boolean isLinkableElement(Element element) {
   		if (element instanceof HTMLAnchorElement) {
   			return true;
   		} else if (element instanceof HTMLAreaElement) {
   			return true;
   		}
   		
   		return false;
	}
	
	public static String getTargetFromElement(Element element) {
   		String targetAttr = null;
   		if (element instanceof HTMLAnchorElement) {
   			targetAttr = ((HTMLAnchorElement)element).getTarget();
   		} else if (element instanceof HTMLAreaElement) {
   			targetAttr = ((HTMLAreaElement)element).getTarget();
   		}
   		
   		return targetAttr;
	}
	
	public static String getHrefFromElement(Element element) {
   		String targetAttr = null;
   		if (element instanceof HTMLAnchorElement) {
   			targetAttr = ((HTMLAnchorElement)element).getHref();
   		} else if (element instanceof HTMLAreaElement) {
   			targetAttr = ((HTMLAreaElement)element).getHref();
   		}
   		
   		return targetAttr;
	}
	
	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/links.html#following-hyperlinks
	 */
	public static void followHyperlink(Element subject, BrowsingContext specificSource) {
       	boolean _replace = false;
       	Document _subjectDocument = subject.getOwnerDocument();
       	
       	if (!(_subjectDocument instanceof Html5DocumentImpl)) {
       		return;
       	}
       	
       	Html5DocumentImpl subjectDocument = (Html5DocumentImpl)_subjectDocument;
       	final BrowsingContext source = subjectDocument.getBrowsingContext();
       	BrowsingContext _target = null;
       	
       	String targetAttr = null;
       	HTMLBaseElement baseElement = subjectDocument.getBaseElement();
       	
       	if (specificSource != null) {
       		_target = specificSource;
       	} else if (isLinkableElement(subject) && (targetAttr = getTargetFromElement(subject)) != null) {
       		_target = source.chooseBrowsingContextByName(targetAttr);
       		_replace = source.isBlankBrowsingContext(targetAttr);
       	} else if (isLinkableElement(subject) && targetAttr == null && baseElement != null && (targetAttr = baseElement.getTarget()) != null) {
       		_target = source.chooseBrowsingContextByName(targetAttr);
       		_replace = source.isBlankBrowsingContext(targetAttr);
       	} else {
       		_target = source;
       	}
       	final BrowsingContext target = _target;
       	final boolean replace = _replace;
       	
       	String hrefString = getHrefFromElement(subject);
       	
       	URL baseUrl = subjectDocument.getAddress();
       	try {
			final URL resultUrl = new URL(baseUrl, hrefString);
			EventLoop eventLoop = subjectDocument.getEventLoop();
			eventLoop.queueTask(new Task(TaskSource.DOM_MANIPULATION, source) {
				
				@Override
				public void execute() throws TaskAbortedException, InterruptedException {
					NavigationController controller = target.getNavigationController();
					controller.navigate(source, resultUrl, false, false, replace);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
       	
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
