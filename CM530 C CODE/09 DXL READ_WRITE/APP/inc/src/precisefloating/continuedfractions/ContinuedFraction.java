package precisefloating.continuedfractions;

import precisefloating.Rational;
import precisefloating.SignAware;
import precisefloating.directedrounding.DirectedNumber;
import precisefloating.directedrounding.RoundNumberBase;
import precisefloating.directedrounding.RoundingMode;

import java.math.BigInteger;


/**
 * Unique simple continued fraction expansion. If the expansion is finite and has
 * a length greater than one, than the last partial quotient is greater than one.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public abstract class ContinuedFraction extends DirectedNumber implements Comparable {

    public abstract PartialQuotients partialQuotients();

    protected RoundNumberBase createRoundNumber() {
        return new RoundContinuedFraction(this);
    }

    public ContinuedFraction() {
        super(RoundingMode.ROUND_HALF_EVEN);
    }

    /**
     * It can be overriden in certain cases for performance or error checking.
     */
    public Convergents convergents() {
        return new ComputedConvergents(partialQuotients());
    }

    /** Will be used to optimize the division operation. */
    private ContinuedFraction inverse;

    public ContinuedFraction inverse() {
        if (inverse == null) {
            inverse = new InverseContinuedFraction(this);

            if (negate != null && negate.inverse != null) {
                inverse.negate = negate.inverse;
            }
        }

        return inverse;
    }

    /** Will be used to optimize the addition operation. */
    private ContinuedFraction negate;

    public ContinuedFraction negate() {
        if (negate == null) {
            negate = new NegateContinuedFraction(this);

            if (inverse != null && inverse.negate != null) {
                negate.inverse = inverse.negate;
            }
        }

        return negate;
    }

    public ContinuedFraction add(ContinuedFraction y) {
        return new AddContinuedFraction(this, y);
    }

    public ContinuedFraction subtract(ContinuedFraction y) {
        return new SubtractContinuedFraction(this, y);
    }

    public ContinuedFraction multiply(ContinuedFraction y) {
        return new MultiplyContinuedFraction(this, y);
    }

    public ContinuedFraction square() {
        return new SquareContinuedFraction(this);
    }

    public ContinuedFraction power(long exponent) {
        return new PowerContinuedFraction(this, BigInteger.valueOf(exponent));
    }

    /**
     * 0^0 throws here or at the first call on either of the iterators.
     */
    public ContinuedFraction power(BigInteger exponent) {
        return new PowerContinuedFraction(this, exponent);
    }

    public ContinuedFraction divide(ContinuedFraction y) {
        return new DivideContinuedFraction(this, y);
    }

    public BigInteger floor() {
        return partialQuotients().nextPartialQuotient();
    }

    public BigInteger ceil() {
        PartialQuotients pqi = partialQuotients();
        BigInteger first = pqi.nextPartialQuotient();

        if (!pqi.hasNext()) {
            return first;
        } else {
            return first.add(BigInteger.ONE);
        }
    }

    public static ContinuedFraction exponential(long exponent) {
        return new ExponentialContinuedFraction(exponent);
    }

    public static ContinuedFraction exponential(BigInteger exponent) {
        return new ExponentialContinuedFraction(exponent);
    }

    public static ContinuedFraction exponential(double exponent) {
        return new ExponentialContinuedFraction(Rational.create(exponent));
    }

    public static ContinuedFraction exponential(Rational exponent) {
        return new ExponentialContinuedFraction(exponent);
    }

    static void tryCheckPositive(ContinuedFraction x, String name) {
        if (x instanceof SignAware) {
            SignAware signAware = (SignAware) x;
            Boolean negative = signAware.tryIsNegative();
            if (negative != null && negative.booleanValue()) {
                throw new ArithmeticException(name
                        + " is negative and we cannot operate on such values");
            }
        }
    }

    static void illegalLastPartialQuotient() {
        throw new IllegalStateException("the last (and not first) partial"
                + " quotient in xIterator is one, and that is not permitted");
    }

    static void checkNotFirstPartialQuotient(BigInteger partialQuotient,
            PartialQuotients xPartialQuotients) {
        if (!xPartialQuotients.isStarted()) {
            throw new IllegalStateException("xPartialQuotients has not been started, "
                    + "so how did you take the given partial quotient ?");
        }

        int cmp = partialQuotient.compareTo(BigInteger.ONE);

        if (cmp < 0) {
            throw new IllegalStateException("partialQuotient must be positive");
        } else {
            if (cmp == 0 && !xPartialQuotients.hasNext()) {
                illegalLastPartialQuotient();
            }
        }
    }

    /**
     * Quote from <a href="http://www.tweedledum.com/rwg/cfup.htm">http://www.tweedledum.com/rwg/cfup.htm</a>:
     * <br>
     * <i>Comparison rule:  If we call the integer part of a continued fraction the 0th
     * term, then we can say that the a (regular) continued fraction is an increasing
     * function of its even numbered terms, and a decreasing function of its odd
     * numbered terms.  Thus, to compare two continued fractions, compare the terms
     * at the first position where they differ, then reverse the decision if this
     * position is odd numbered.  If one continued fraction terminates before
     * differing from the other, regard the shorter one to be suffixed by an infinite term.</i>
     * <br>
     * The default implementation requires an infinite time if the two continued fractions to
     * compare are equal irrational numbers; otherwise it finishes in finite time.
     * 
     * @link http://www.tweedledum.com/rwg/cfup.htm
     */
    public int compareTo(Object o) {
        ContinuedFraction that = (ContinuedFraction) o;

        PartialQuotients thisPqi = partialQuotients(), thatPqi = that.partialQuotients();

        boolean inv = true;
        int cmp = 0;

        while (thisPqi.hasNext() && thatPqi.hasNext() && cmp == 0) {
            inv = !inv;
            BigInteger thisPartialQuoeficient = thisPqi.nextPartialQuotient(),
                    thatPartialQuoeficient = thatPqi.nextPartialQuotient();
            cmp = thisPartialQuoeficient.compareTo(thatPartialQuoeficient);
        }

        if (cmp != 0) {
            if (inv) {
                // works well even for Integer.MIN_VALUE
                cmp = -(cmp | (cmp >>> 1));
            }
        } else {
            assert !thisPqi.hasNext() || !thatPqi.hasNext();

            if (thisPqi.hasNext()) {
                assert !thatPqi.hasNext();

                if (inv) {
                    cmp = -1;
                } else {
                    cmp = +1;
                }
            } else {
                if (thatPqi.hasNext()) {
                    if (inv) {
                        cmp = +1;
                    } else {
                        cmp = -1;
                    }
                }
            }
        }

        return cmp;
    }

}
