package precisefloating;

import precisefloating.directedrounding.ZeroNumberException;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Immutable nonzero finite double precision value representation with odd fraction.
 * <p/>
 * JLS floating point specs:
 * s * m * 2 ^ (e - N + 1)
 * s = +-1
 * 0 <= m < 2 ^ N
 * Normalized: m >= 2 ^ (N - 1)
 * Emin <= e <= Emax
 * <p/>
 * Float: N = 24 K = 8 Emin = -126 Emax = +127
 * Double: N = 53 K = 11 Emin = -1022 Emax = +1023
 * <p/>
 * This class always uses an odd mantissa.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class DoublePrecisionNo implements Serializable, Cloneable, Comparable {

    /** Odd number (it can't be Long.MIN_VALUE). */
    private long m;

    /** The the exponent of this number when the mantissa is odd. */
    private int e;

    /**
     * Double.NEGATIVE_INFINITY means the value has not been computed.
     * Double.POSITIVE_INFINITY means the value is not a finite (nonzero) double.
     * Double.isNaN(value) is forbidden.
     * Formulas.isFinite(value) means the value has been computed to be a finit (nonzero) double.
     */
    private double value;

    private static final Logger log = Logger.getLogger(DoublePrecisionNo.class.getName());

    public DoublePrecisionNo(double d) {
        if (d == 0 || Double.isInfinite(d) || Double.isNaN(d)) {
            throw new IllegalArgumentException("parameter must be finite nonzero double");
        }

        long bits = Double.doubleToLongBits(d);

        boolean signBit = extractSignBit(bits);
        long fractionBits = extractFractionBits(bits);
        short exponentBits = extractExponentBits(bits);

        if (exponentBits == 0) {
            this.m = signBit ? -fractionBits : fractionBits;
            this.e = 1 - Formulas.DOUBLE_BIAS;
        } else {
            assert (fractionBits & 0x0010000000000000L) == 0;
            long fullMantissa = fractionBits | 0x0010000000000000L;

            this.m = signBit ? -fullMantissa : fullMantissa;
            this.e = exponentBits - Formulas.DOUBLE_BIAS;
        }

        makeOddFraction();

        assert d == computeValue();

        value = d;
    }

    /**
     * Only used for assertion.
     */
    private double computeValue() {
        value = Double.NEGATIVE_INFINITY;
        double t = exactFiniteDoubleValue();
        return t;
    }

    public int getExponent() {
        return e;
    }

    public long getMantissa() {
        return m;
    }

    public DoublePrecisionNo(long m, int e) {
        this(m, e, false);
    }

    public DoublePrecisionNo(long m, int e, boolean restrictFinite) {
        if (m == 0) {
            throw new ZeroNumberException("m must not be zero");
        }

        this.m = m;
        this.e = e;

        makeOddFraction();

        if (restrictFinite && !isExactFiniteDouble()) {
            throw new ArithmeticException(
                    "Restriction to finite double precision numbers exceeded");
        }

        value = Double.NEGATIVE_INFINITY;
    }

    /**
     * For finite nonzero double numbers, -9007199254740991 <= m <= 9007199254740991, m != 0,
     * and -1022 <= e <= 1075. Including the sign, m only needs 54 bits, and e needs only 12 bits.
     */
    public boolean isExactFiniteDouble() {
        // todo should be implemented by inspecting the values of m and e
        try {
            exactFiniteDoubleValue();
        } catch (ArithmeticException ex) {
            return false;
        }

        return true;
    }

    /**
     * @return the exact finite double value, if one exists
     * @throws ArithmeticException if this is not an exact (nonzero) finite double value
     */
    public double exactFiniteDoubleValue() {
        if (value == Double.NEGATIVE_INFINITY) {
            // JLS 5.1.2 Widening Primitive Conversion
            // Conversion of an int or a long value to float, or of a long value to double,
            // may result in loss of precision-that is, the result may lose some of
            // the least significant valueBits of the value. In this case, the resulting
            // floating-point value will be a correctly rounded version of the integer value,
            // using IEEE 754 round-to-nearest mode.

            if (m <= -1L << 53 || m >= 1L << 53) {
                value = Double.POSITIVE_INFINITY;
                // the conversion to double would loose precision
                throw new ArithmeticException("value is not exactly representable because "
                        + "abs(m) is not in the open interval (-2^53, 2^53); m is actually " + m
                        + " It could be overflow or loss of precision");
            }

            try {
                // Because -2^53 < m < 2^53, the widening conversion of m to double is exact
                // m is odd, so underflow in pow2 means an exact result could not be obtained.
                value = m * Formulas.pow2(e - Formulas.N_DOUBLE + 1);

                if (value == Double.POSITIVE_INFINITY) {
                    throw new ArithmeticException("Overflow");
                }
            } catch (ArithmeticException ex) {
                value = Double.POSITIVE_INFINITY;
                throw ex;
            }

            long valueBits = Double.doubleToLongBits(value);
            long valueFractionBits = extractFractionBits(valueBits);
            short valueExponentBits = extractExponentBits(valueBits);

            long absValueM;

            if (valueExponentBits == 0) {
                absValueM = valueFractionBits;
            } else {
                absValueM = valueFractionBits | 0x0010000000000000L;
            }

            if (Math.abs(m) > absValueM) {
                value = Double.POSITIVE_INFINITY;
                throw new ArithmeticException("the least significand bits of the mantissa "
                        + "have been removed");
            }
        }

        assert Formulas.isFinite(value);

        return value;
    }

    /**
     * @throws java.lang.ArithmeticException when the double precistion number cannot be normalized
     */
    private void makeOddFraction() {
        assert m != 0;

        int lowZeroBitCount;

        if (m < 0) {
            // if m is Long.MIN_VALUE, then lowZeroBitCount is correctly set to 63
            lowZeroBitCount = Formulas.lowestSetBit(-m);
        } else {
            lowZeroBitCount = Formulas.lowestSetBit(m);
        }

        assert 0 <= lowZeroBitCount && lowZeroBitCount <= 63;

        m >>= lowZeroBitCount;

        assert m % 2 != 0;

        if ((long) Integer.MAX_VALUE - (long) lowZeroBitCount < (long) e) {
            throw new ArithmeticException("Cannot makeOddFraction number");
        } else {
            e += lowZeroBitCount;
        }
    }

    /**
     * Extracts the fraction bits of the double-precision floating-point value.
     * 
     * @return the fraction bits of the double-precision floating-point value.
     */
    public static long extractFractionBits(double a) {
        long bits = Double.doubleToLongBits(a);
        return extractFractionBits(bits);
    }

    private static long extractFractionBits(long bits) {
        return bits & 0x000FFFFFFFFFFFFFL;
    }

    public String toString() {
        return "m = " + m + "\t e = " + e;
    }

    public String toHexString() {
        return "m = " + Long.toHexString(m) + "\t e = " + Integer.toHexString(e);
    }

    public String toBinaryString() {
        return "m = " + Long.toBinaryString(m) + "\t e = " + Integer.toBinaryString(e);
    }

    /**
     * Extracts the exponent bits of the double-precision floating-point value.
     * 
     * @return the exponent bits of the double-precision floating-point value
     */
    public static short extractExponentBits(double a) {
        long bits = Double.doubleToLongBits(a);
        return extractExponentBits(bits);
    }

    private static short extractExponentBits(long bits) {
        // >>> works as well
        long result = (bits >> 52) & 0x7FF;

        short shortResult = (short) result;
        assert shortResult == result;

        return shortResult;
    }

    /**
     * Extracts the sign bit of the double-precision floating-point value.
     * 
     * @return the sign bit of the double-precision floating-point value;
     *         true for negative; false for pozitive
     */
    public static boolean extractSignBit(double a) {
        long bits = Double.doubleToLongBits(a);
        return extractSignBit(bits);
    }

    private static boolean extractSignBit(long bits) {
        return (bits & 0x8000000000000000L) != 0;
    }

    public int compareTo(DoublePrecisionNo that) {
        if (this.m == that.m && this.e == that.e) {
            return 0;
        }

        if (m < 0 && that.m > 0) {
            return -1;
        }

        if (m > 0 && that.m < 0) {
            return +1;
        }

        long absM, absThatM;

        if (m < 0) {
            absM = -m;
            absThatM = -that.m;
        } else {
            absM = m;
            absThatM = that.m;
        }

        assert Formulas.sameSignum(m, that.m);

        long exp, thatExp;

        if (e < that.e) {
            exp = 0;
            thatExp = (long) that.e - (long) e;
        } else {
            exp = (long) e - (long) that.e;
            thatExp = 0;
        }

        assert Math.min(exp, thatExp) == 0;
        assert Math.max(exp, thatExp) <= (long) Integer.MAX_VALUE - (long) Integer.MIN_VALUE;

        // If exp is Integer.MAX_VALUE - Integer.MIN_VALUE, and highestSetBit is 62, it is still ok.
        long hsbM = (long) Formulas.highestSetBit(absM) + exp,
                hsbThatM = (long) Formulas.highestSetBit(absThatM) + thatExp;

        if (hsbM < hsbThatM) {
            if (m < 0) {
                return +1;
            } else {
                return -1;
            }
        } else {
            if (hsbM > hsbThatM) {
                if (m < 0) {
                    return -1;
                } else {
                    return +1;
                }
            } else {
                assert hsbM == hsbThatM && hsbM <= 62;
                assert Math.max(exp, thatExp) <= 62;

                // can freely compute and compare the (scaled) values because both fit nicely into long
                long x = m * (1L << exp), y = that.m * (1L << thatExp);

                return Formulas.compare(x, y);
            }
        }
    }

    public int compareTo(Object o) {
        DoublePrecisionNo that = (DoublePrecisionNo) o;
        return compareTo(that);
    }

}
