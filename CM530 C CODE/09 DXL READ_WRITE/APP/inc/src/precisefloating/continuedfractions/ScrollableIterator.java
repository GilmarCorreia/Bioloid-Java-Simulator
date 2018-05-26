package precisefloating.continuedfractions;

import java.util.Iterator;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public interface ScrollableIterator extends Iterator {

    /**
     * If the old directionIndex is equal to <code>directionIndex</code>, than this method must be very cheap.
     * 
     * @param index -1 means before start.
     */
    void setIndex(long index);

}
