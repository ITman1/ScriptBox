package org.fit.cssbox.scriptbox.script.javascript.wrap.adapter;

public class Pair<L, R> {

    public L left;
    public R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int hashCode() {
        return left.hashCode() ^ right.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        
        if (!(o instanceof Pair)) {
            return false;
        }
        
        @SuppressWarnings("rawtypes")
		Pair o2 = (Pair) o;
        
        return this.left.equals(o2.left)
                && this.right.equals(o2.right);
    }
}
