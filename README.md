================================================================================
                            SCRIPTBOX LIBRARY
================================================================================

This project implements the extension for scripting inside (X)HTML documents [1]. 
It introduces simple non-UI user agent [2] which implements general scripting 
support for arbitrary scripting language. In the basic implementation is 
included scripting support for JavaScript scripting language which is ensured 
by Rhino [5]. In addition to that project implements also UI user agent based 
on SwingBox rendering component [4].

[1] http://www.w3.org/html/wg/drafts/html/CR/scripting-1.html
[2] http://www.w3.org/html/wg/drafts/html/CR/browsers.html
[3] http://www.w3.org/html/wg/drafts/html/CR/webappapis.html
[4] https://developer.mozilla.org/en-US/docs/Rhino
[5] http://cssbox.sourceforge.net/swingbox/

Status of project: (in development)
Implemented scripting engines: JavaScript

Development suite: Eclipse (Kepler Service Release 1)

--------------------------------------------------------------------------------
                           BUILDING THE LIBRARY

REQUIREMENTS:

  1) Have installed JDK 1.6 or newer 1.7 (recommended)
  2) Have installed Maven build manager [1]
  3) Have set system variable JAVA_HOME to directory with installed JDK and have
     in the system variable PATH its binary directory [2]
     (eg. on Windows add to PATH path %JAVA_HOME%\bin)
  4) Have in the system variable PATH the directory with Maven installation
  5) Have cloned latest version of CSSBox [3], CSSParser[4] and SwingBox[5] repository
  6) Have builded and installed the CSSBox, CSSParser and SwingBox packages 
     into the local Maven repository (see [6])
     Note: Other dependency packages will be reached automatically from public repositories

BUILDING:

  Simply run command: mvn package

[1] http://maven.apache.org/download.cgi#Installation_Instructions
[2] http://maven.apache.org/download.cgi
[3] https://github.com/radkovo/CSSBox
[4] https://github.com/radkovo/jStyleParser
[5] https://github.com/radkovo/SwingBox
[6] http://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html

--------------------------------------------------------------------------------
                           USING THE LIBRARY

REQUIREMENTS:

  Have builded library (see previous section) and have it specified on classpath

DEMOS:
  
  Library package contains some demos located on org.fit.cssbox.scriptbox.demo.
  
  List of demos:
    1) JavaScriptTesterController
       - Simple web browser which adds debugging components for testing scripts

EXAMPLE OF USING LIBRARY:

  import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
  import org.fit.cssbox.scriptbox.browser.UserAgent;

  UserAgent userAgent = new UserAgent();
  BrowsingUnit browsingUnit = userAgent.openBrowsingUnit();
  browsingUnit.navigate("http://cssbox.sourceforge.net/");

--------------------------------------------------------------------------------
                             KNOWN ISSUES

Project is still in phase of development and targets the experimental frame
of new HTML 5.1 specification which has not been released yet, so bugs may 
occur in the current implementation or also in specification itself.

If run into any bug, please report on: 
   https://github.com/ITman1/ScriptBox/issues

ISSUES LIST:
   *) error: annotation XYZ is missing value for the attribute <clinit>
      - This error may occur if you are running Sun JDK compiler
      - It is known bug: 
          http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6857918
      - Solution: 
          use JDK 8
          use different compiler than javac eg. Edifact Java Compiler (EJC)

--------------------------------------------------------------------------------
                           CONTACT/CREDITS
                             
Author:    Radim Loskot
gmail.com: radim.loskot (e-mail)

--------------------------------------------------------------------------------
                               CREDITS

Project leader:
   Radek Burget <burgetr@fit.vutbr.cz>