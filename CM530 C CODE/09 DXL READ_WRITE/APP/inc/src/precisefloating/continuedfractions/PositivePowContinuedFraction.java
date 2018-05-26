package precisefloating.continuedfractions;

import precisefloating.SignAware;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class PositivePowContinuedFraction extends ContinuedFraction {

    private final ContinuedFraction positiveX;
    private final BigInteger exponent;

    /**
     * @param positiveX If not positive, this constructor throws IllegalArgumentException or
     *                  the first call to next() in either iterator throws ArithmeticException.
     * @param exponent  
     */
    public PositivePowContinuedFraction(ContinuedFraction positiveX, BigInteger exponent) {
        if (exponent.signum() != +1) {
            throw new IllegalArgumentException("the exponent must be positive but it has the value"
                    + exponent);
        }

        if (positiveX instanceof SignAware) {
            SignAware signAware = (SignAware) positiveX;
            Boolean tryIsPositive = signAware.tryIsPositive();

            if (tryIsPositive != null && !tryIsPositive.booleanValue()) {
                throw new IllegalArgumentException("positiveX must be positive but it has the value"
                        + positiveX);
            }
        }

        this.exponent = exponent;
        this.positiveX = positiveX;
    }

    public PartialQuotients partialQuotients() {
        return null;
    }

}
