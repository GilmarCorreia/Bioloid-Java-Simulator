package precisefloating;

import precisefloating.continuedfractions.ContinuedFraction;
import precisefloating.continuedfractions.RationalExpansion;
import precisefloating.directedrounding.RoundingMode;
import precisefloating.directedrounding.RoundNumberBase;
import precisefloating.directedrounding.DirectedNumber;

import java.math.BigInteger;
import java.util.Random;

/**
 * Immutable and thread safe class. Reduced rationals usually have better performance.
 * If the numerator is zero, than the denominator has the value one. The denominator is always
 * positive. If one tries to use a denominator equal to zero, the methods of this class
 * throw DivideByZeroException. Otherwise, getNumerator() and getDenonimator() return whatever
 * values were passed in the constructor.
 * <pre>Implementation note:
 * <br>The number methods are synchronized.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class Rational extends DirectedNumber implements SignAware, Cloneable, Comparable {

    private final BigInteger numerator;
    private final BigInteger denominator;

    private BigInteger gcd;

    /** More instances of ZERO may exist, but none of them has a nonzero signum. */
    public static final Rational ZERO = new Rational(BigInteger.ZERO);

    /** More instances of ONE may exist. */
    public static final Rational ONE = new Rational(BigInteger.ONE, BigInteger.ONE);

    public static final Rational DOUBLE_MIN_VALUE = create(Double.MIN_VALUE);
    public static final Rational DOUBLE_HALF_MIN_VALUE = DOUBLE_MIN_VALUE.multiplyTwoPower(-1);
    public static final Rational DOUBLE_MAX_SUBNORMAL = create(Formulas.DOUBLE_MAX_SUBNORMAL);
    public static final Rational DOUBLE_MIN_NORMAL = create(Formulas.DOUBLE_MIN_NORMAL);
    public static final Rational DOUBLE_MAX_VALUE = create(Double.MAX_VALUE);
    public static final Rational DOUBLE_MAX_NEAREST_THRESHOLD =
            DOUBLE_MAX_VALUE.add(Rational.ONE.multiplyTwoPower(
                    Formulas.DOUBLE_MAX_TWO_EXPONENT - Formulas.N_DOUBLE), true);
    public static final Rational DOUBLE_NORMAL_NEAREST_THRESHOLD =
            DOUBLE_MAX_SUBNORMAL.add(DOUBLE_HALF_MIN_VALUE, true);

    public static final Rational FLOAT_MIN_VALUE = create(Float.MIN_VALUE);
    public static final Rational FLOAT_HALF_MIN_VALUE = FLOAT_MIN_VALUE.multiplyTwoPower(-1);
    public static final Rational FLOAT_MAX_SUBNORMAL = create(Formulas.FLOAT_MAX_SUBNORMAL);
    public static final Rational FLOAT_MIN_NORMAL = create(Formulas.FLOAT_MIN_NORMAL);
    public static final Rational FLOAT_MAX_VALUE = create(Float.MAX_VALUE);
    public static final Rational FLOAT_MAX_NEAREST_THRESHOLD =
            FLOAT_MAX_VALUE.add(Rational.ONE.multiplyTwoPower(
                    Formulas.FLOAT_MAX_TWO_EXPONENT - Formulas.N_FLOAT), true);
    public static final Rational FLOAT_NORMAL_NEAREST_THRESHOLD =
            FLOAT_MAX_SUBNORMAL.add(FLOAT_HALF_MIN_VALUE, true);

    static {
        ZERO.exactDoubleValue = new Double(0);
        ONE.exactDoubleValue = new Double(1);
        FLOAT_HALF_MIN_VALUE.exactDoubleValue = new Double(Float.MIN_VALUE / 2.0);
        FLOAT_MAX_NEAREST_THRESHOLD.exactDoubleValue = new Double((double)Float.MAX_VALUE
                + Formulas.pow2(Formulas.FLOAT_MAX_TWO_EXPONENT - Formulas.N_FLOAT));
        FLOAT_NORMAL_NEAREST_THRESHOLD.exactDoubleValue = new Double(Formulas.FLOAT_MAX_SUBNORMAL
                + FLOAT_HALF_MIN_VALUE.getExactDoubleValue().doubleValue());
    }

    /**
     * Guaranteed to be set if the Rational.create(double) method is used and in all the
     * public static final fields in this class that are indeed exact double values.
     * Otherwise, the only guarantee is that if this field is set, then this rational number
     * is exactly representable as the returned double value.
     */
    public Double getExactDoubleValue() {
        // for better testing
        // return null;
        return exactDoubleValue;
    }

    private Double exactDoubleValue;

    /**
     * Constructs a exactly reduced fraction with the given exact value.
     */
    public static Rational create(double d) {
        Rational r;

        if (d == 0) {
            r = ZERO;
        } else {
            DoublePrecisionNo dpn = new DoublePrecisionNo(d);

            // -1022 - 52 <= computedExponent <= 1075 - 52
            // -1074 <= computedExponent <= 1023
            // 2 ^ (e - N + 1)
            int computedExponent = dpn.getExponent() - Formulas.N_DOUBLE + 1;

            BigInteger oddM = BigInteger.valueOf(dpn.getMantissa());

            if (computedExponent < 0) {
                assert -computedExponent < 1075;
                r = create(oddM, Formulas.bigintPow2(-computedExponent));
            } else {
                r = create(leftShift(oddM, computedExponent), BigInteger.ONE);
            }

            r.exactDoubleValue = new Double(d);
        }

        return r;
    }

    private static BigInteger leftShift(BigInteger oddM, int n) {
        if (oddM.equals(BigInteger.ONE)) {
            return Formulas.bigintPow2(n);
        } else {
            return oddM.shiftLeft(n);
        }
    }

    private Rational(BigInteger numerator) {
        this(numerator, BigInteger.ONE);
    }

    /**
     * Cheap check.
     */
    private static final BigInteger checkNotNull(BigInteger x, String s) {
        if (x == null) {
            throw new NullPointerException(s + " must not be null");
        }

        return x;
    }

    public int hashCode() {
        if (isReduced()) {
            return numerator.hashCode() ^ denominator.hashCode();
        } else {
            return reduced().hashCode();
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (this.getClass() != obj.getClass()) {
            return false;
        }

        Rational that = (Rational) obj;

        Rational reducedThis = reduced(), reducedThat = that.reduced();
        return reducedThis.numerator.equals(reducedThat.numerator)
                && reducedThis.denominator.equals(reducedThat.denominator);
    }

    public String toString() {
        return numerator + "/" + denominator;
    }

    /**
     * The numerator and denominator are generated with
     * new BigInteger(numBits, rnd).add(BigInteger.ONE).
     * This constructor always constructs a positive BigInteger.
     * 
     * @param rnd source of randomness to be used in computing the numerator and the denominator
     */
    public Rational(int numBits, Random rnd) {
        this(new BigInteger(numBits, rnd).add(BigInteger.ONE),
                new BigInteger(numBits, rnd).add(BigInteger.ONE));
    }

    private Rational(BigInteger numerator, BigInteger denominator) {
        super(RoundingMode.ROUND_HALF_EVEN);

        checkNotNull(numerator, "numerator");
        checkNotNull(denominator, "denominator");

        if (denominator.signum() == 0) {
            throw new DivideByZeroException("denominator must be non zero");
        }

        if (numerator.signum() == 0) {
            if (!denominator.equals(BigInteger.ONE)) {
                throw new IllegalArgumentException("zero has a unique representation: 0/1");
            }
        }

        if (denominator.signum() == -1) {
            numerator = numerator.negate();
            denominator = denominator.negate();
        }

        this.numerator = numerator;
        this.denominator = denominator;

        assert denominator.signum() == 1;
    }

    public static Rational valueOf(long numerator) {
        return create(numerator, 1);
    }

    public static Rational valueOf(BigInteger numerator) {
        return create(numerator, BigInteger.ONE);
    }

    public static Rational create(long numerator, long denominator) {
        return create(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    public static Rational create(BigInteger numerator, BigInteger denominator) {
        if (numerator.signum() == 0) {
            if (denominator.signum() == 0) {
                throw new DivideByZeroException("denominator must be non zero");
            } else {
                return Rational.ZERO;
            }
        }

        return new Rational(numerator, denominator);
    }

    public BigInteger getNumerator() {
        return numerator;
    }

    public BigInteger getDenominator() {
        return denominator;
    }

    /**
     * Is this fraction in lowest terms ?
     */
    public boolean isReduced() {
        if (gcd == null) {
            gcd = numerator.gcd(denominator);
        }

        boolean isReduced = gcd.equals(BigInteger.ONE);
        return isReduced;
    }

    private Rational reduced;

    public Rational reduced() {
        if (reduced == null) {
            if (isReduced()) {
                reduced = this;
            } else {
                reduced = Rational.create(numerator.divide(gcd), denominator.divide(gcd));
                reduced.reduced = reduced;
                reduced.gcd = BigInteger.ONE;
                reduced.exactDoubleValue = exactDoubleValue;
            }
        }

        return reduced;
    }

    private BigInteger quotient;

    protected BigInteger getQuotient() {
        if (quotient == null) {
            quotient = numerator.divide(denominator);
        }

        return quotient;
    }

    public Rational add(Rational that) {
        return add(that, false);
    }

    public Rational add(Rational that, boolean reduce) {
        if (!reduce) {
            return Rational.create(
                    numerator.multiply(that.denominator).add(denominator.multiply(that.numerator)),
                    denominator.multiply(that.denominator));
        } else {
            Rational thisReduced = reduced(), thatReduced = that.reduced();
            // a/b + c/exactDoubleValue
            BigInteger a = thisReduced.numerator, b = thisReduced.denominator,
                    c = thatReduced.numerator, d = thatReduced.denominator;

            return Rational.create(a.multiply(d).add(b.multiply(c)), b.multiply(d)).reduced();
        }
    }

    public Rational subtract(Rational that) {
        return subtract(that, false);
    }

    public Rational subtract(Rational that, boolean reduce) {
        return add(that.negate(), reduce);
    }

    public Rational multiply(Rational that) {
        return multiply(that, false);
    }

    public Rational multiply(Rational that, boolean reduce) {
        if (!reduce) {
            return Rational.create(numerator.multiply(that.numerator),
                    denominator.multiply(that.denominator));
        } else {
            Rational thisReduced = reduced(), thatReduced = that.reduced();
            // a/b * c/d
            BigInteger a = thisReduced.numerator, b = thisReduced.denominator,
                    c = thatReduced.numerator, d = thatReduced.denominator;

            BigInteger adGcd = a.gcd(d);
            if (!adGcd.equals(BigInteger.ONE)) {
                a = a.divide(adGcd);
                d = d.divide(adGcd);
            }

            BigInteger bcGcd = b.gcd(c);
            if (!bcGcd.equals(BigInteger.ONE)) {
                b = b.divide(bcGcd);
                c = c.divide(bcGcd);
            }

            return create(a.multiply(c), b.multiply(d));
        }
    }

    /**
     * Does not keep any references to the result.
     */
    public Rational square() {
        return square(false);
    }

    /**
     * Does not keep any referenced to the result.
     */
    public Rational square(boolean reduce) {
        Rational square;

        if (!reduce) {
            square = multiply(this);
        } else {
            square = Rational.create(reduced().numerator.multiply(reduced().numerator),
                    reduced().denominator.multiply(reduced().denominator));
        }

        assert square.signum() >= 0;
        return square;
    }

    /**
     * Does not keep any referenced to the result.
     * 
     * @throws ArithmeticException 0 ^ 0
     */
    public Rational power(int exponent) {
        return power(exponent, false);
    }

    /**
     * Does not keep any referenced to the result.
     * 
     * @throws ArithmeticException 0 ^ 0 or 0 ^ x, x < o
     */
    public Rational power(int exponent, boolean reduce) {
        Rational power;

        if (exponent == 0) {
            if (signum() == 0) {
                // BigInteger can't handle it, it would return 0 rather than throwing an exception
                throw new ArithmeticException("0 ^ 0");
            } else {
                // x ^ 0, x != 0
                power = Rational.ONE;
            }
        } else {
            if (exponent < 0) {
                if (!reduce) {
                    power = Rational.create(denominator.pow(-exponent), numerator.pow(-exponent));
                } else {
                    power = Rational.create(reduced().denominator.pow(-exponent),
                            reduced().numerator.pow(-exponent));
                }
            } else {
                assert exponent > 0;

                if (!reduce) {
                    power = Rational.create(numerator.pow(exponent), denominator.pow(exponent));
                } else {
                    power = Rational.create(reduced().numerator.pow(exponent),
                            reduced().denominator.pow(exponent));
                }
            }
        }

        return power;
    }

    public Rational divide(Rational that) {
        return divide(that, false);
    }

    public Rational divide(Rational that, boolean reduce) {
        return multiply(that.inverse(), reduce);
    }

    private Rational inverse;

    public Rational inverse() {
        if (inverse == null) {
            inverse = Rational.create(denominator, numerator);
            inverse.inverse = this;

            if (negate != null && negate.inverse != null) {
                inverse.negate = negate.inverse;
            }
        }

        return inverse;
    }

    private Rational negate;

    public Rational negate() {
        if (negate == null) {
            negate = Rational.create(numerator.negate(), denominator);
            negate.negate = this;

            if (inverse != null && inverse.negate != null) {
                negate.inverse = inverse.negate;
            }
        }

        return negate;
    }

    /**
     * Returns the signum function of this rational number.
     * 
     * @return -1, 0 or 1 as the value of this Rational is negative, zero or positive.
     */
    public int signum() {
        return numerator.signum();
    }

    /**
     * Compares this Rational with the specified Rational.
     * 
     * @param that Rational to which this Rational is to be compared.
     * @return -1, 0 or 1 as this Rational is numerically less than, equal
     *         to, or greater than <tt>val</tt>.
     */
    public int compareTo(Rational that) {
        if (signum() < that.signum()) {
            return -1;
        } else {
            if (signum() > that.signum()) {
                return +1;
            } else {
                assert signum() == that.signum();

                if (this.equals(that)) {
                    return 0;
                } else {
                    assert signum() != 0 && that.signum() != 0;
                    Rational division = divide(that, true);
                    assert division.signum() == +1;
                    return division.numerator.compareTo(division.denominator) * signum();
                }
            }
        }

    }

    public Rational abs() {
        if (signum() >= 0) {
            return this;
        } else {
            return negate();
        }
    }

    public int compareTo(Object o) {
        Rational that = (Rational) o;
        return compareTo(that);
    }

    private BigInteger numeratorModDenominator;

    public BigInteger numeratorModDenominator() {
        if (numeratorModDenominator == null) {
            numeratorModDenominator = numerator.mod(denominator);
        }

        return numeratorModDenominator;
    }

    private BigInteger floor;

    /**
     * The ceil function can happen to be computed as a side effect.
     * 
     * @link http://mathworld.wolfram.com/FloorFunction.html
     */
    public BigInteger floor() {
        if (floor == null) {
            BigInteger[] divRem = numerator.divideAndRemainder(denominator);

            if (divRem[1].signum() == 0) {
                floor = ceil = divRem[0];
            } else {
                // truncation
                if (numerator.signum() == +1) {
                    floor = divRem[0];
                    // ceil = floor.add(BigInteger.ONE);
                } else {
                    assert numerator.signum() == -1;
                    ceil = divRem[0];
                    floor = ceil.subtract(BigInteger.ONE);
                }
            }
        }

        return floor;
    }

    private Rational inverseFractionalValue;

    /**
     * @throws IllegalStateException when <code>numerator.mod(denominator)</code> is zero
     */
    public Rational inverseFractionalValue() {
        if (inverseFractionalValue == null) {
            if (numeratorModDenominator().signum() == 0) {
                throw new DivideByZeroException("numerator.mod(denominator) is zero");
            }
            inverseFractionalValue = Rational.create(denominator, numeratorModDenominator());
        }

        return inverseFractionalValue;
    }

    private Rational fractionalValue;

    /**
     * @return fractional value or {x}
     * @link http://mathworld.wolfram.com/FloorFunction.html
     */
    public Rational fractionalValue() {
        if (fractionalValue == null) {
            fractionalValue = Rational.create(numeratorModDenominator(), denominator);
            fractionalValue.fractionalValue = fractionalValue;
        }

        return fractionalValue;
    }

    private Rational fractionalPart;

    /**
     * Rarely used.
     * integerPart(x) + fractionalPart(x) = x
     * 
     * @link http://mathworld.wolfram.com/FractionalPart.html
     */
    public Rational fractionalPart() {
        if (fractionalPart == null) {
            if (this.equals(Rational.ZERO)) {
                fractionalPart = Rational.ZERO;
            } else {
                assert signum() != 0;
                Rational p = abs().fractionalValue();
                if (signum() == -1) {
                    p = p.negate();
                }
                fractionalPart = p;
            }

        }

        return fractionalPart;
    }

    private BigInteger integerPart;

    /**
     * Rarely used.
     * 
     * @link http://mathworld.wolfram.com/IntegerPart.html
     */
    public BigInteger integerPart() {
        if (integerPart == null) {
            if (signum() >= 0) {
                integerPart = floor();
            } else {
                integerPart = ceil();
            }
        }

        return integerPart;
    }

    private BigInteger ceil;

    /**
     * @link http://mathworld.wolfram.com/CeilingFunction.html
     */
    public BigInteger ceil() {
        if (ceil == null) {
            if (numeratorModDenominator().signum() == 0) {
                ceil = floor();
            } else {
                ceil = floor().add(BigInteger.ONE);
            }
        }

        return ceil;
    }

    public ContinuedFraction expansion() {
        return new RationalExpansion(this);
    }

    public Boolean tryIsNegative() {
        return Boolean.valueOf(signum() == -1);
    }

    public Boolean tryIsPositive() {
        return Boolean.valueOf(signum() == +1);
    }

    public Boolean tryIsZero() {
        return Boolean.valueOf(signum() == 0);
    }

    public Integer trySignum() {
        return new Integer(signum());
    }

    public boolean isFloorEqualTo(BigInteger fl) {
        if (floor != null) {
            return floor.equals(fl);
        } else {
            BigInteger fld = denominator.multiply(fl);

            if (fld.compareTo(numerator) <= 0 && numerator.subtract(fld).compareTo(denominator) < 0) {
                floor = fl;
                return true;
            } else {
                return false;
            }
        }
    }

    private int log2Low = Integer.MAX_VALUE, log2High = Integer.MIN_VALUE;
    private boolean log2Computed;

    /**
     * If this rational is an exact two power (this = pow2(n)), then a[0] = a[1] = n. Otherwise,
     * the a[0] and a[1] values are computed such that a[1] = a[0] + 1
     * and pow2(a[0]) < this < pow2(1[1]).
     * Safe for use in concurrent threads.
     */
    public void log2Interval(int[] a) {
        if (signum() <= 0) {
            throw new ArithmeticException("log2(x) only for x > 0");
        }

        if (!log2Computed) {
            synchronized (this) {
                if (!log2Computed) {
                    // could be cached
                    int cmp = numerator.compareTo(denominator);

                    int shcmp;

                    switch (cmp) {
                        case -1:
                            BigInteger shiftedNumerator = numerator.shiftLeft(
                                    denominator.bitLength() - numerator.bitLength());
                            shcmp = shiftedNumerator.compareTo(denominator);
                            break;
                        case 0:
                            shcmp = 0;
                            assert numerator.bitLength() == denominator.bitLength();
                            break;
                        case +1:
                            BigInteger shiftedDenominator = denominator.shiftLeft(
                                    numerator.bitLength() - denominator.bitLength());
                            shcmp = numerator.compareTo(shiftedDenominator);
                            break;
                        default:
                            throw new InternalError("cmp = " + cmp);
                    }

                    int bitLengthDiff = numerator.bitLength() - denominator.bitLength();

                    switch (shcmp) {
                        case -1:
                            log2Low = bitLengthDiff - 1;
                            log2High = bitLengthDiff;
                            break;
                        case 0:
                            log2Low = log2High = bitLengthDiff;
                            break;
                        case +1:
                            log2Low = bitLengthDiff;
                            log2High = bitLengthDiff + 1;
                            break;
                        default:
                            throw new InternalError("shcmp = " + shcmp);
                    }

                    assert log2Low <= log2High;
                    log2Computed = true;
                }
            }
        }

        a[0] = log2Low;
        a[1] = log2High;
    }

    /**
     * More efficient than regular multiplication. Does not explicitly reduce the result.
     */
    public Rational multiplyTwoPower(int i) {
        final Rational result;

        assert denominator.getLowestSetBit() >= 0;

        if (signum() == 0 || i == 0) {
            assert numerator.getLowestSetBit() == -1;
            result = this;
        } else {
            assert numerator.getLowestSetBit() >= 0;

            if (i < 0) {
                int j = Math.min(-i, numerator.getLowestSetBit());
                /*
                for (j = 0; j < -i && !numerator.testBit(j); j++) {
                }
                */

                BigInteger num = numerator.shiftRight(j);
                assert i + j <= 0;
                BigInteger denom = denominator.shiftRight(i + j);
                result = create(num, denom);
            } else {
                if (i > 0) {
                    int j = Math.min(i, denominator.getLowestSetBit());

                    /*
                    for (j = 0; j < i && !denominator.testBit(j); j++) {
                    }
                    */

                    assert i - j >= 0;
                    BigInteger num = numerator.shiftLeft(i - j);
                    BigInteger denom = denominator.shiftRight(j);
                    result = create(num, denom);
                } else {
                    throw new InternalError("cannot get here");
                }
            }
        }

        return result;
    }

    protected RoundNumberBase createRoundNumber() {
        return new RoundRational(this);
    }

}
