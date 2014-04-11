package org.fit.cssbox.scriptbox.ui;

public abstract class ScrollBarsProp extends BarProp {
	public abstract void scroll(int xCoord, int yCoord);
	public abstract boolean scrollToFragment(String fragment);
}
