/**
 * SandboxingFlag.java
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

package org.fit.cssbox.scriptbox.security;

/**
 * Enumeration of all sandboxing flags.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#sandboxing-flag-set">A sandboxing flag set</a>
 */
public enum SandboxingFlag {	
	NAVIGATION_BROWSING_CONTEXT_FLAG,
	AUXILARY_NAVIGATION_BROWSING_CONTEXT_FLAG,
	TOPLEVEL_NAVIGATION_BROWSING_CONTEXT_FLAG,
	PLUGINS_BROWSING_CONTEXT_FLAG,
	SEAMLESS_IFRAMES_FLAG,
	ORIGIN_BROWSING_CONTEXT_FLAG,
	FORMS_BROWSING_CONTEXT_FLAG,
	POINTER_LOCK_BROWSING_CONTEXT_FLAG,
	SCRIPTS_BROWSING_CONTEXT_FLAG,
	AUTOMATIC_FEATURES_BROWSING_CONTEXT_FLAG,
	FULLSCREEN_BROWSING_CONTEXT_FLAG,
	DOCUMENT_DOMAIN_BROWSING_CONTEXT_FLAG
}
