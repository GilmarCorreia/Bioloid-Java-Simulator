package precisefloating.continuedfractions;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class PeriodicContinuedFraction extends ContinuedFraction {

    private final ContinuedFraction start, period;

    public ContinuedFraction getStart() {
        return start;
    }

    public ContinuedFraction getPeriod() {
        return period;
    }

    public PeriodicContinuedFraction(BigInteger[] start, BigInteger[] period) {
        this(Arrays.asList(start), Arrays.asList(period));
    }

    public PeriodicContinuedFraction(BigInteger[] period) {
        this(null, Arrays.asList(period));
    }

    public PeriodicContinuedFraction(List period) {
        this(null, period);
    }

    public PeriodicContinuedFraction(List start, List period) {
        this(start != null ? new BufferedContinuedFraction(start) : null,
                new BufferedContinuedFraction(period));
    }

    public PeriodicContinuedFraction(ContinuedFraction period) {
        this(null, period);
    }

    public PeriodicContinuedFraction(ContinuedFraction start,
            ContinuedFraction period) {
        this.start = start;
        this.period = period;
    }

    public PartialQuotients partialQuotients() {
        return new PeriodicPartialQuotients(
                start != null ? start.partialQuotients() : null, period);
    }

}
