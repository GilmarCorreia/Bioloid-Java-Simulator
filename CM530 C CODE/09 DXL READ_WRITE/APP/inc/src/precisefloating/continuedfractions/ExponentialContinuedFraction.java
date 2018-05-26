package precisefloating.continuedfractions;

import precisefloating.Rational;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class ExponentialContinuedFraction extends ContinuedFraction {

    protected final Rational exponent;

    public ExponentialContinuedFraction(long exponent) {
        this(Rational.valueOf(exponent));
    }

    public ExponentialContinuedFraction(BigInteger exponent) {
        this(Rational.valueOf(exponent));
    }

    public ExponentialContinuedFraction(Rational exponent) {
        this.exponent = exponent.reduced();
    }

    public PartialQuotients partialQuotients() {

        PartialQuotients pqi;

        if (exponent.equals(Rational.ZERO)) {
            pqi = new SingletonPartialQuotient(BigInteger.ONE);
        } else {
            if (exponent.abs().equals(Rational.ONE)) {
                pqi = ContinuedFractionConstants.E.partialQuotients();
            } else {
                pqi = new ExponentialPartialQuotients(exponent.abs());
            }

            if (exponent.signum() == -1) {
                pqi = new InversePartialQuotients(pqi);
            }
        }

        return pqi;
    }

}

