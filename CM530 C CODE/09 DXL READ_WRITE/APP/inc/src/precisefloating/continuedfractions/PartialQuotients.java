package precisefloating.continuedfractions;

import java.math.BigInteger;
import java.util.NoSuchElementException;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public abstract class PartialQuotients extends IndexedIterator {

    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * During the first call, the iterator sees himself not yet started.
     * 
     * @return 
     */
    protected abstract BigInteger computeNext();

    public final BigInteger nextPartialQuotient() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        BigInteger x = computeNext();
        index++;
        started = true; // after computeNext() returns

        return x;

    }

    public final Object next() {
        return nextPartialQuotient();
    }

    public void checkNotStartedAndHasNext(String s) {
        if (isStarted()) {
            throw new IllegalStateException(s + " has already started");
        }

        if (!hasNext()) {
            throw new IllegalStateException(s + " has no more elements");
        }
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
