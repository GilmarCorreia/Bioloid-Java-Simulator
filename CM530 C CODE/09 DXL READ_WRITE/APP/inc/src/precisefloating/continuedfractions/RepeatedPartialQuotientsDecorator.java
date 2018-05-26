package precisefloating.continuedfractions;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
class RepeatedPartialQuotientsDecorator extends PartialQuotients {

    boolean hasNext = true;

    public RepeatedPartialQuotientsDecorator(RepeatedPartialQuotients prqi) {
        this.prqi = prqi;
        assert prqi.hasNext();
    }

    private final RepeatedPartialQuotients prqi;

    protected BigInteger computeNext() {
        BigInteger latest = prqi.computeNext();
        hasNext = prqi.inner.hasNext();
        return latest;
    }

    public boolean hasNext() {
        return hasNext;
    }

}