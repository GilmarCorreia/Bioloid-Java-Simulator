package precisefloating.continuedfractions;

import java.math.BigInteger;

/**
 * Could be derived from PartialQuotientsDecorator.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class BeforePartialQuotients extends PartialQuotients {

    private PartialQuotients iterator;
    private BigInteger first;

    protected BigInteger computeNext() {
        if (first != null) {
            BigInteger x = first;
            first = null;
            return x;
        } else {
            return (BigInteger) iterator.next();
        }
    }

    public boolean hasNext() {
        return iterator.hasNext() || first != null;
    }

    public BeforePartialQuotients(BigInteger first, PartialQuotients iterator) {
        this.iterator = iterator;
        this.first = first;
    }

}
