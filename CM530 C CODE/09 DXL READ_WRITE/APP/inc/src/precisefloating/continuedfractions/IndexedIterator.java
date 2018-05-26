package precisefloating.continuedfractions;

import java.util.Iterator;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public abstract class IndexedIterator implements Iterator {

    protected long index = -1;
    protected boolean started = false;

    /**
     * If the directionIndex overflows, it can be negative, but it always has the correct parity
     * because Long.MAX_VALUE is odd and Long.MIN_VALUE is even.
     */
    public long getIndex() {
        return index;
    }

    /**
     * We cannot use the directionIndex sign because it can overflow, so a separate started boolean value
     * is used.
     */
    public boolean isStarted() {
        return started;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

}
