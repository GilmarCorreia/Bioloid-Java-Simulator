package precisefloating.continuedfractions;

import precisefloating.Rational;
import precisefloating.SignAware;

/**
 * Unique simple continued fraction expansion of a rational number with the last partial quotient
 * being greater than one if the rational number is not an integral one.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class RationalExpansion extends ContinuedFraction implements SignAware {
    private final Rational r;

    public RationalExpansion(Rational r) {
        this.r = r.reduced();
    }

    public PartialQuotients partialQuotients() {
        return new RationalPartialQuotients(r);
    }

    public Convergents convergents() {
        return new ComputedConvergents(partialQuotients()) {

            /**
             * Overridden in order to assert that the last convergent is the fraction itself.
             */
            public Rational computeNext() {
                Rational cvg = super.computeNext();
                // the last convergent is the fraction itself
                assert hasNext() || cvg.equals(r);
                return cvg;
            }

        };
    }

    public Rational getValue() {
        return r;
    }

    public Boolean tryIsNegative() {
        return r.tryIsNegative();
    }

    public Boolean tryIsPositive() {
        return r.tryIsPositive();
    }

    public Boolean tryIsZero() {
        return r.tryIsZero();
    }

    public Integer trySignum() {
        return new Integer(r.signum());
    }

}
