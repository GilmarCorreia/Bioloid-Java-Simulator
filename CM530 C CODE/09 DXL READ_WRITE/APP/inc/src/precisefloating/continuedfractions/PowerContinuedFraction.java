package precisefloating.continuedfractions;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class PowerContinuedFraction extends ContinuedFraction {

    private final ContinuedFraction x;
    protected final BigInteger exponent;

    /**
     * 0^0 throws here or at the first call on either of the iterators.
     */
    public PowerContinuedFraction(ContinuedFraction x, BigInteger exponent) {
        this.x = x;
        this.exponent = exponent;
    }

    public PartialQuotients partialQuotients() {
        PartialQuotients[] array;
        BigInteger absExponent = exponent.abs();

        if (absExponent.bitCount() > 0) {
            array = new PartialQuotients[exponent.abs().bitCount()];
        } else {
            array = new PartialQuotients[1];
        }

        for (int i = 0; i < array.length; i++) {
            array[i] = x.partialQuotients();
        }

        return createPartialQuotients(array);
    }

    protected PartialQuotients createPartialQuotients(PartialQuotients[] array) {
        return new PowerPartialQuotients(array, exponent);
    }

}
