package precisefloating.continuedfractions;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class ERadicalExpansion extends ContinuedFraction {

    private final BigInteger p;

    public ERadicalExpansion(long p) {
        this(BigInteger.valueOf(p));
    }

    public ERadicalExpansion(BigInteger p) {
        if (p.compareTo(BigInteger.valueOf(2)) < 0) {
            throw new IllegalArgumentException("The radical directionIndex must be at least 2");
        }

        this.p = p;
    }

    public PartialQuotients partialQuotients() {
        return new ERadicalPartialQuotients(p);
    }

}
