package org.fit.cssbox.scriptbox.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SandboxingHelper {
	public static final String ALLOW_POPUPS_KEYWORD = "allow-popups";
	public static final String ALLOW_TOP_NAVIGATION = "allow-top-navigation";
	public static final String ALLOW_SAME_ORIGIN = "allow-same-origin";
	public static final String ALLOW_FORMS = "allow-forms";
	public static final String ALLOW_POINTER_LOCK = "allow-pointer-lock";
	public static final String ALLOW_SCRIPTS = "allow-scripts";
	
	public static Set<SandboxingFlag> parseSandboxingDirective(String sandboxingDirective) {
		return parseSandboxingDirective(sandboxingDirective, false);
	}
	
	public static Set<SandboxingFlag> parseSandboxingDirective(String sandboxingDirective, boolean allowFullScreen) {	
		String[] tokensArr = sandboxingDirective.split("\\s+");
		Set<String> tokens = new HashSet<String>(Arrays.asList(tokensArr));

		HashSet<SandboxingFlag> flags = new HashSet<SandboxingFlag>();
		
		flags.add(SandboxingFlag.NAVIGATION_BROWSING_CONTEXT_FLAG);
		
		if (!tokens.contains(ALLOW_POPUPS_KEYWORD)) {
			flags.add(SandboxingFlag.AUXILARY_NAVIGATION_BROWSING_CONTEXT_FLAG);
		}
		
		if (!tokens.contains(ALLOW_TOP_NAVIGATION)) {
			flags.add(SandboxingFlag.TOPLEVEL_NAVIGATION_BROWSING_CONTEXT_FLAG);
		}
		
		flags.add(SandboxingFlag.PLUGINS_BROWSING_CONTEXT_FLAG);
		flags.add(SandboxingFlag.SEAMLESS_IFRAMES_FLAG);
		
		if (!tokens.contains(ALLOW_SAME_ORIGIN)) {
			flags.add(SandboxingFlag.ORIGIN_BROWSING_CONTEXT_FLAG);
		}
		
		if (!tokens.contains(ALLOW_FORMS)) {
			flags.add(SandboxingFlag.FORMS_BROWSING_CONTEXT_FLAG);
		}
		
		if (!tokens.contains(ALLOW_POINTER_LOCK)) {
			flags.add(SandboxingFlag.POINTER_LOCK_BROWSING_CONTEXT_FLAG);
		}
		
		if (!tokens.contains(ALLOW_SCRIPTS)) {
			flags.add(SandboxingFlag.SCRIPTS_BROWSING_CONTEXT_FLAG);
			flags.add(SandboxingFlag.AUTOMATIC_FEATURES_BROWSING_CONTEXT_FLAG);
		}
		
		if (!allowFullScreen) {
			flags.add(SandboxingFlag.FULLSCREEN_BROWSING_CONTEXT_FLAG);
		}
		
		flags.add(SandboxingFlag.DOCUMENT_DOMAIN_BROWSING_CONTEXT_FLAG);
		
		return flags;
	}
}
