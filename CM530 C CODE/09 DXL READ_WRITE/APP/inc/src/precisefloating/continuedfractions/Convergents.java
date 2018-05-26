package precisefloating.continuedfractions;

import precisefloating.Rational;

import java.util.NoSuchElementException;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public abstract class Convergents extends IndexedIterator {

    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * During the first call, the iterator sees himself not yet started.
     * 
     * @return 
     */
    protected abstract Rational computeNext();

    public final Rational nextConvergent() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        Rational x = computeNext();
        index++;
        started = true; // after computeNext() returns

        return x;

    }

    public final Object next() {
        return nextConvergent();
    }

    public void checkNotStartedAndHasNext(String s) {
        if (isStarted()) {
            throw new IllegalStateException(s + " has already started");
        }

        if (!hasNext()) {
            throw new IllegalStateException(s + " has no more elements");
        }
    }

}
