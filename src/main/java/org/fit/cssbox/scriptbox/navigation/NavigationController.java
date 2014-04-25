/**
 * NavigationController.java
 * (c) Radim Loskot and Radek Burget, 2013-2014
 *
 * ScriptBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ScriptBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with ScriptBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.fit.cssbox.scriptbox.navigation;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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

/**
 * Class that controls navigation of the browsing contexts.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class NavigationController {
	/*
	 * Listener for the navigation attempt, which drives dispatching of the navigation controller events.
	 */
	private NavigationAttemptListener navigationAttemptListener = new NavigationAttemptListener() {
		
		@Override
		public void onMatured(NavigationAttempt attempt) {
			synchronized (NavigationController.this) {
				removeNavigationAttempt(attempt);
				fireNavigationAttemptEvent(attempt, EventType.NAVIGATION_MATURED);
			}
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

	/**
	 * Constructs navigation controller for a given context.
	 * 
	 * @param context Browsing context for which should be constructed the constroller.
	 */
	public NavigationController(BrowsingContext context) {
		this.context = context;
		this.listeners = new HashSet<NavigationControllerListener>();
		
		this.navigationAttempts = new ArrayList<NavigationAttempt>();
	}
	
	/**
	 * Returns all navigation attempts running in this controller.
	 * 
	 * @return All navigation attempts that run in this controller.
	 */
	public synchronized List<NavigationAttempt> getAllNavigationAttempts() {
		return Collections.unmodifiableList(navigationAttempts);
	}
	
	/**
	 * Navigation for reason of the updating of the passed session history entry.
	 * 
	 * @param entry Session history entry to be reloaded.
	 * @return New constructed navigation attempt.
	 */
	public synchronized NavigationAttempt update(SessionHistoryEntry entry) {
		UpdateNavigationAttempt attempt = new UpdateNavigationAttempt(this, entry);
			
		attempt.perform(navigationAttemptListener);
		
		return attempt;
	}
	
	/**
	 * Creates new navigation attempt.
	 * 
	 * @param sourceBrowsingContext Browsing context that initiated this navigation.
	 * @param url URL to be navigated.
	 * @param exceptionEnabled If exceptions should be thrown for this navigation.
	 * @param explicitSelfNavigationOverride If should not be resolved effective destination 
	 *        context and use browsing context of this controller.
	 * @param replacementEnabled If current session entry should be replaced.
	 * @return New navigation attempt.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#navigate">Navigate algorithm</a>
	 */
	public synchronized NavigationAttempt navigate(BrowsingContext sourceBrowsingContext, URL url, boolean exceptionEnabled, boolean explicitSelfNavigationOverride, boolean replacementEnabled) {
		NewNavigationAttempt attempt = new NewNavigationAttempt(this, sourceBrowsingContext, url, exceptionEnabled, explicitSelfNavigationOverride, replacementEnabled);
		
		attempt.perform(navigationAttemptListener);
		
		return attempt;
	}
	
	/**
	 * Navigates to given hyperlink.
	 * 
	 * @param subject Element with the hyperlink.
	 */
	public static void followHyperlink(Element subject) {
       	followHyperlink(subject, null);
	}
	
	/**
	 * Navigates to given hyperlink with specified source browsing context.
	 * 
	 * @param subject Element with the hyperlink.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/links.html#following-hyperlinks">Following hyperlinks</a>
	 */
	public static void followHyperlink(Element subject, BrowsingContext specificSource) {
       	boolean _replace = false;
       	Document _subjectDocument = subject.getOwnerDocument();
       	
       	if (!(_subjectDocument instanceof Html5DocumentImpl)) {
       		return;
       	}
       	
       	Html5DocumentImpl subjectDocument = (Html5DocumentImpl)_subjectDocument;
       	final BrowsingContext source = subjectDocument.getBrowsingContext();
       	boolean _explicitSelfOverride = false;
       	BrowsingContext _target = null;
       	
       	String targetAttr = null;
       	HTMLBaseElement baseElement = subjectDocument.getBaseElement();
       	
       	if (specificSource != null) {
       		_target = specificSource;
       	} else if (isLinkableElement(subject) && (targetAttr = getTargetFromElement(subject)) != null) {
       		_target = source.chooseBrowsingContextByName(targetAttr);
       		_replace = source.isBlankBrowsingContext(targetAttr);
       		_explicitSelfOverride = source.isExplicitSelfNavigationOverride(targetAttr);
       	} else if (isLinkableElement(subject) && targetAttr == null && baseElement != null && (targetAttr = baseElement.getTarget()) != null) {
       		_target = source.chooseBrowsingContextByName(targetAttr);
       		_replace = source.isBlankBrowsingContext(targetAttr);
       		_explicitSelfOverride = source.isExplicitSelfNavigationOverride(targetAttr);
       	} else {
       		_target = source;
       	}
       	final BrowsingContext target = _target;
       	final boolean replace = _replace;
       	final boolean explicitSelfOverride = _explicitSelfOverride;
       	
       	String hrefString = getHrefFromElement(subject);
       	
       	URL baseUrl = subjectDocument.getAddress();
       	try {
			final URL resultUrl = new URL(baseUrl, hrefString);
			EventLoop eventLoop = subjectDocument.getEventLoop();
			eventLoop.queueTask(new Task(TaskSource.DOM_MANIPULATION, source) {
				
				@Override
				public void execute() throws TaskAbortedException, InterruptedException {
					NavigationController controller = target.getNavigationController();
					controller.navigate(source, resultUrl, false, explicitSelfOverride, replace);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
       	
	}
		
	/**
	 * Cancels all non matured yet navigation attempts for specific browsing context.
	 * 
	 * @param specifiedDesinationContext Browsing context for which to cancel the navigation attempts.
	 * @param excludeAttempt Navigation attempt that should be excluded from the canceling.
	 */
	public synchronized void cancelAllNonMaturedNavigationAttempts(final BrowsingContext specifiedDesinationContext, final NavigationAttempt excludeAttempt) {
		cancelNavigationAttempts(new Predicate<NavigationAttempt>() {
			
			@Override
			public boolean apply(NavigationAttempt attempt) {
				BrowsingContext destinationContext = attempt.getDestinationBrowsingContext();
				
				if (attempt == excludeAttempt) {
					return false;
				}
				
				if (destinationContext == specifiedDesinationContext && !attempt.isMatured()) {
					return true;
				}
				
				return false;
			}
		});
	}
	
	/**
	 * Cancels all non matured yet navigation attempts for specific browsing context.
	 * 
	 * @param specifiedDesinationContext Browsing context for which to cancel the navigation attempts.
	 * @see #cancelAllNonMaturedNavigationAttempts(BrowsingContext, NavigationAttempt)
	 */
	public synchronized void cancelAllNonMaturedNavigationAttempts(final BrowsingContext specifiedDesinationContext) {
		cancelAllNonMaturedNavigationAttempts(specifiedDesinationContext, null);
	}
	
	/**
	 * Cancels all navigation attempts.
	 */
	public synchronized void cancelAllNavigationAttempts() {
		ImmutableList<NavigationAttempt> attempts = ImmutableList.copyOf(navigationAttempts);
		
		for (NavigationAttempt attempt : attempts) {
			attempt.cancel();
		}
	}
	
	/**
	 * Cancels all attempts that satisfies passed predicate.
	 * 
	 * @param attemptPredicate Predicate according to which to determine whether yes, or not to cancel navigation attempt.
	 */
	public synchronized void cancelNavigationAttempts(Predicate<NavigationAttempt> attemptPredicate) {
		ImmutableList<NavigationAttempt> attempts = ImmutableList.copyOf(navigationAttempts);
		
		for (NavigationAttempt attempt : attempts) {
			if (attemptPredicate.apply(attempt)) {
				attempt.cancel();
			}
		}
	}
	
	/**
	 * Tests whether there exist any navigation attempt that satisfies given predicate.
	 * 
	 * @param attemptPredicate Predicate agains which to test each navigation attempts.
	 * @return True if there exists navigation attempt that satisfies given predicate, otherwise false.
	 */
	public synchronized boolean existsNavigationAttempt(Predicate<NavigationAttempt> attemptPredicate) {

		for (NavigationAttempt attempt : navigationAttempts) {
			if (attemptPredicate.apply(attempt)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns associated browsing context.
	 * 
	 * @return Associated browsing context.
	 */
	public BrowsingContext getBrowsingContext() {
		return context;
	}
	
	/**
	 * Registers event listener to this navigation controller.
	 * 
	 * @param listener New event listener to be registered.
	 */
	public void addListener(NavigationControllerListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes event listener to this navigation controller.
	 * 
	 * @param listener Event listener to be removed.
	 */
	public void removeListener(NavigationControllerListener listener) {
		listeners.remove(listener);
	}
	
	private void fireNavigationAttemptEvent(NavigationAttempt attempt, NavigationControllerEvent.EventType eventType) {
		NavigationControllerEvent event = new NavigationControllerEvent(this, eventType, attempt);
		Set<NavigationControllerListener> listenersCopy = new HashSet<NavigationControllerListener>(listeners);
				
		for (NavigationControllerListener listener : listenersCopy) {
			listener.onNavigationEvent(event);
		}
	}
	
	private synchronized void addNavigationAttempt(NavigationAttempt attempt) {
		navigationAttempts.add(attempt);		
	}

	private synchronized void removeNavigationAttempt(NavigationAttempt attempt) {
		navigationAttempts.remove(attempt);
	}
	
	/**
	 * Tests if is passed Element an element which may be followed.
	 * 
	 * @param element Element to be tested.
	 * @return True, if we can follow passed element.
	 */
	private static boolean isLinkableElement(Element element) {
   		if (element instanceof HTMLAnchorElement) {
   			return true;
   		} else if (element instanceof HTMLAreaElement) {
   			return true;
   		}
   		
   		return false;
	}
	
	/**
	 * Returns target attribute from the linkable element.
	 * 
	 * @param element Linkable element.
	 * @return Target attribute.
	 */
	private static String getTargetFromElement(Element element) {
   		String targetAttr = null;
   		if (element instanceof HTMLAnchorElement) {
   			targetAttr = ((HTMLAnchorElement)element).getTarget();
   		} else if (element instanceof HTMLAreaElement) {
   			targetAttr = ((HTMLAreaElement)element).getTarget();
   		}
   		
   		return targetAttr;
	}
	
	/**
	 * Returns href attribute from the linkable element.
	 * 
	 * @param element Linkable element.
	 * @return Href attribute.
	 */
	private static String getHrefFromElement(Element element) {
   		String targetAttr = null;
   		if (element instanceof HTMLAnchorElement) {
   			targetAttr = ((HTMLAnchorElement)element).getHref();
   		} else if (element instanceof HTMLAreaElement) {
   			targetAttr = ((HTMLAreaElement)element).getHref();
   		}
   		
   		return targetAttr;
	}
}
