package precisefloating.continuedfractions;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class RepeatedPartialQuotients extends PartialQuotients {

    public RepeatedPartialQuotients(PartialQuotients inner) {
        this.inner = inner;
    }

    protected final PartialQuotients inner;
    BigInteger last;

    protected BigInteger computeNext() {
        BigInteger next;

        if (last != null) {
            next = last;
            last = null;
        } else {
            last = inner.nextPartialQuotient();
            next = last;
        }

        return next;
    }

    public boolean hasNext() {
        return inner.hasNext() || last != null;
    }

}
