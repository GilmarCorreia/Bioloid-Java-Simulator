package precisefloating.continuedfractions;

import precisefloating.Formulas;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class SharedPartialQuotients extends PartialQuotientsDecorator {

    private final IndexedBoundedFifoBuffer buffer;
    private static final Logger log = Logger.getLogger(SharedPartialQuotients.class.getName());

    public static SharedPartialQuotients[] share(PartialQuotients x,
            PartialQuotients y, int bufferSize) {
        SharedPartialQuotients[] a = new SharedPartialQuotients[2];
        share(x, y, bufferSize, a, 0);
        return a;
    }

    public static void share(PartialQuotients x, PartialQuotients y,
            int bufferSize, SharedPartialQuotients[] a, int start) {
        IndexedBoundedFifoBuffer buffer = new IndexedBoundedFifoBuffer(bufferSize);
        a[start] = new SharedPartialQuotients(x, buffer);
        a[start + 1] = new SharedPartialQuotients(y, buffer);
    }

    protected SharedPartialQuotients(PartialQuotients iterator,
            IndexedBoundedFifoBuffer buffer) {
        super(iterator);

        if (!buffer.isEmpty()) {
            throw new IllegalArgumentException("buffer must be empty");
        }

        if (buffer.getStart() != -1) {
            throw new IllegalArgumentException("buffer directionIndex must be -1");
        }

        this.buffer = buffer;
    }

    protected BigInteger computeNext() {
        // log.info(buffer.toString() + this);

        assert !buffer.isShared() || scrollSize() >= 0
                : "shared = " + buffer.isShared() + " scrollSize() = " + scrollSize();

        BigInteger next;

        if (!buffer.isShared()) {
            buffer.unshare(); // not actually needed
            next = super.computeNext();
        } else {
            if (buffer.isEmpty()) {
                next = super.computeNext();
                buffer.add(next);
            } else {
                if (buffer.getStart() == index) {
                    next = (BigInteger) buffer.remove();

                    if (index == Long.MAX_VALUE) {
                        buffer.unshare();
                    } else {
                        buffer.incrementStart();
                    }
                } else {
                    next = super.computeNext();

                    if (buffer.getStart() + buffer.size() == index) {
                        if (buffer.size() == buffer.getCapacity()) {
                            if (log.isLoggable(Level.INFO)) {
                                log.info("not enough capacity " + buffer.getCapacity());
                            }
                            buffer.unshare();
                        } else {
                            buffer.add(next);
                        }
                    } else {
                        assert Formulas.inClosedInterval(index + buffer.getCapacity(), 0, buffer.getStart());
                        buffer.unshare();
                        scroll();
                    }
                }
            }
        }

        assert !buffer.isShared() || scrollSize() >= -1
                : "shared = " + buffer.isShared() + " scrollSize() = " + scrollSize();

        return next;
    }

    private void scroll() {
        assert buffer.isShared();

        if (iterator instanceof ScrollableIterator) {
            // log.info("scrolling to" + directionIndex + this);
            ScrollableIterator scrollable = (ScrollableIterator) iterator;
            scrollable.setIndex(index);
        } else {
            long scrollSize = scrollSize();
            assert scrollSize >= 0 : "scrollSize = " + scrollSize;

            if (scrollSize > 0) {
                // log.info("scrolling " + scrollSize + this);
                for (int i = 0; i < scrollSize; i++) {
                    iterator.next();
                }
            }
        }
    }

    private long scrollSize() {
        return index - ((PartialQuotients) iterator).getIndex();
    }

    public boolean hasNext() {
        boolean hasNext;

        if (!buffer.isShared()) {
            hasNext = super.hasNext();
        } else {
            if (buffer.getStart() == index && !buffer.isEmpty()) {
                hasNext = true;
            } else {
                assert buffer.getStart() + buffer.size() == index
                        : buffer.getStart() + " + " + buffer.size() + " != " + index ;
                scroll();
                hasNext = super.hasNext();
            }
        }

        // log.info(hasNext + "" + this);

        return hasNext;
    }

}
