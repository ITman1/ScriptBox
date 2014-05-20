SCRIPTBOX LIBRARY
======

This project implements the extension for [scripting](http://www.w3.org/html/wg/drafts/html/CR/scripting-1.html) inside (X)HTML documents . 
It introduces simple non-UI [user agent](http://www.w3.org/html/wg/drafts/html/CR/browsers.html) which implements general scripting 
support and [Web API](http://www.w3.org/html/wg/drafts/html/CR/webappapis.html) for arbitrary scripting language. In the basic implementation is 
included scripting support for JavaScript scripting language which is ensured 
by [Rhino](https://developer.mozilla.org/en-US/docs/Rhino). In addition to that project implements also UI user agent based 
on [SwingBox](https://github.com/radkovo/SwingBox) rendering component .


**Status of project:** (in development)  
**Implemented scripting engines:** JavaScript  
**Development suite:** Eclipse (Kepler Service Release 1)

## Building library

### Requirements

1. Have installed JDK 1.6 or newer - JDK 1.8 is recommended
2. Have installed [Maven build manager](http://maven.apache.org/download.cgi#Installation_Instructions)
3. Have set system variable `JAVA_HOME` to directory with installed JDK and have
  in the system variable `PATH` its binary directory - eg. on Windows add to `PATH` variable `%JAVA_HOME%\bin` (more [here](http://maven.apache.org/download.cgi))
4. Have in the system variable `PATH` the directory with Maven installation
5. Have cloned latest version of [CSSBox](https://github.com/radkovo/CSSBox), [CSSParser](https://github.com/radkovo/jStyleParser) and [SwingBox](https://github.com/radkovo/SwingBox) repository
6. Have builded and installed the CSSBox, CSSParser and SwingBox packages 
  into the local Maven repository (see [guide](http://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html))
  Note: Other dependency packages will be reached automatically from public repositories

### Building

Simply run command: `mvn package`


## Using the library

### Requirements

Have builded library (see previous section) and have it specified on classpath

### Demos
  
Library package contains some demos located on `org.fit.cssbox.scriptbox.demo`.
  
**List of demos:**
  
1. `SimpleUserAgent` - Simple web browser that have only location bar and history traversal buttons
2. `JavaScriptTesterUserAgent` - Application that adds simple debugging components for testing the scripts

### Example of using library

    import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
    import org.fit.cssbox.scriptbox.browser.UserAgent;
    
    UserAgent userAgent = new UserAgent();
    BrowsingUnit browsingUnit = userAgent.openBrowsingUnit();
    browsingUnit.navigate("http://cssbox.sourceforge.net/");


# Known issues

Project is still in phase of development and targets the experimental frame
of new HTML 5.1 specification which has not been released yet, so bugs may 
occur in the current implementation or also in specification itself.

If you run into any bug, please report on:  
   https://github.com/ITman1/ScriptBox/issues

## Issue list:

1. Thrown error during `javac` build: `error: annotation XYZ is missing value for the attribute <clinit>`  
      - This error may occur if you are running Sun JDK compiler  
      - It is known bug: 
          http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6857918
      - **Solution:** use JDK 8 or use different compiler than `javac` eg. Edifact Java Compiler (EJC)

## Contact and credits
                             
**Author:**    Radim Loskot  
**gmail.com:** radim.loskot (e-mail)

### Credits

- **Radek Burget** - <burgetr@fit.vutbr.cz> (project leader)
