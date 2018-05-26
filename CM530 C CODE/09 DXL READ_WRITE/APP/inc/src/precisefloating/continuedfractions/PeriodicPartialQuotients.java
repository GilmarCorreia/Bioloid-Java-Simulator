package precisefloating.continuedfractions;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class PeriodicPartialQuotients extends InfinitePartialQuotients {

    private ContinuedFraction period;
    private final PartialQuotients startIterator;
    private PartialQuotients periodIterator;

    /**
     * @param startIterator can be null
     * @param period        the period
     */
    public PeriodicPartialQuotients(PartialQuotients startIterator,
            ContinuedFraction period) {
        this.startIterator = startIterator;
        this.period = period;
    }

    public ContinuedFraction getPeriod() {
        return period;
    }

    protected BigInteger computeNext() {
        BigInteger next;

        if (startIterator != null && startIterator.hasNext()) {
            assert periodIterator == null;
            next = startIterator.nextPartialQuotient();
        } else {
            if (periodIterator == null) {
                periodIterator = period.partialQuotients();
            }

            next = periodIterator.nextPartialQuotient();

            if (!periodIterator.hasNext()) {
                periodIterator = period.partialQuotients();
            }
        }

        return next;
    }

}
