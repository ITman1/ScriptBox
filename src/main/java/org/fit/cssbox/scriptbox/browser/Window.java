package org.fit.cssbox.scriptbox.browser;

public abstract class Window {
/*
[Global]
interface Window : EventTarget {
  // the current browsing context
  [Unforgeable] readonly attribute WindowProxy window;
  [Replaceable] readonly attribute WindowProxy self;
  [Unforgeable] readonly attribute Document document;
           attribute DOMString name; 
  [PutForwards=href, Unforgeable] readonly attribute Location location;
  readonly attribute History history;
  [Replaceable] readonly attribute BarProp locationbar;
  [Replaceable] readonly attribute BarProp menubar;
  [Replaceable] readonly attribute BarProp personalbar;
  [Replaceable] readonly attribute BarProp scrollbars;
  [Replaceable] readonly attribute BarProp statusbar;
  [Replaceable] readonly attribute BarProp toolbar;
           attribute DOMString status;
  void close();
  readonly attribute boolean closed;
  void stop();
  void focus();
  void blur();

  // other browsing contexts
  [Replaceable] readonly attribute WindowProxy frames;
  [Replaceable] readonly attribute unsigned long length;
  [Unforgeable] readonly attribute WindowProxy top;
           attribute WindowProxy? opener;
  readonly attribute WindowProxy parent;
  readonly attribute Element? frameElement;
  WindowProxy open(optional DOMString url = "about:blank", optional DOMString target = "_blank", optional DOMString features = "", optional boolean replace = false);
  getter WindowProxy (unsigned long index);
  getter object (DOMString name);

  // the user agent
  readonly attribute Navigator navigator; 
  readonly attribute External external;
  readonly attribute ApplicationCache applicationCache;

  // user prompts
  void alert(optional DOMString message = "");
  boolean confirm(optional DOMString message = "");
  DOMString? prompt(optional DOMString message = "", optional DOMString default = "");
  void print();
  any showModalDialog(DOMString url, optional any argument);


};
Window implements GlobalEventHandlers;
Window implements WindowEventHandlers;
 */
}
