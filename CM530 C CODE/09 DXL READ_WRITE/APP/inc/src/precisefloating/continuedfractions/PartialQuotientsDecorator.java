package precisefloating.continuedfractions;

import java.math.BigInteger;
import java.util.Iterator;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class PartialQuotientsDecorator extends PartialQuotients {

    /** Not final because is modified by the subclasses implementing cloning functionality. */
    protected Iterator iterator;

    public PartialQuotientsDecorator(Iterator iterator) {
        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("empty iterator");
        }

        this.iterator = iterator;
    }

    protected BigInteger computeNext() {
        return (BigInteger) iterator.next();
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

}
