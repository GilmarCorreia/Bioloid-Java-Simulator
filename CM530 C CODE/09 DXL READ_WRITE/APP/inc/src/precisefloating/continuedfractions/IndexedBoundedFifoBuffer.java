package precisefloating.continuedfractions;

import org.apache.commons.collections.BoundedFifoBuffer;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
class IndexedBoundedFifoBuffer extends BoundedFifoBuffer {

    private long start = -1;
    private boolean shared;

    public int getCapacity() {
        return capacity;
    }

    private final int capacity;

    public void setStart(long start) {
        checkShared();
        assert -1 <= start;
        this.start = start;
    }

    private void checkShared() {
        if (!isShared()) {
            throw new IllegalStateException("this buffer has been unshared");
        }
    }

    public long getStart() {
        return start;
    }

    public IndexedBoundedFifoBuffer(int size) {
        super(size);
        this.capacity = size;
        shared = true;
    }

    public IndexedBoundedFifoBuffer() {
        this(32);
    }

    public boolean add(Object object) {
        checkShared();
        return super.add(object);
    }

    public Object remove() {
        checkShared();
        return super.remove();
    }

    public void incrementStart() {
        checkShared();
        assert -1 <= start;
        start++;
    }

    public void unshare() {
        // can't set m_elements to null as it is private
        clear();
        shared = false;
    }

    public boolean isShared() {
        return shared;
    }

}