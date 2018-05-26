package precisefloating;

import java.math.BigInteger;
import java.util.logging.Logger;
import java.util.Arrays;

/**
 * Contains general utility floating point operations.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class Formulas {

    private static final Logger log = Logger.getLogger(Formulas.class.getName());

    public static final int N_DOUBLE = 53, DOUBLE_BIAS = 1023;
    public static final int N_FLOAT = 24, FLOAT_BIAS = 127;

    public static final long DOUBLE_MAX_SUBNORMAL_BITS = 0x000FFFFFFFFFFFFFL;
    /**
     * DOUBLE_MIN_NORMAL - Double.MIN_VALUE
     */
    public static final double DOUBLE_MAX_SUBNORMAL = Double.longBitsToDouble(DOUBLE_MAX_SUBNORMAL_BITS);

    public static final long DOUBLE_MIN_NORMAL_BITS = 0x0010000000000000L;

    /**
     * pow2(-1022)
     * DOUBLE_MAX_SUBNORMAL + Double.MIN_VALUE
     */
    public static final double DOUBLE_MIN_NORMAL = Double.longBitsToDouble(DOUBLE_MIN_NORMAL_BITS);

    public static final int FLOAT_MAX_SUBNORMAL_BITS = 0x007FFFFF;
    public static final float FLOAT_MAX_SUBNORMAL = Float.intBitsToFloat(FLOAT_MAX_SUBNORMAL_BITS);

    public static final int FLOAT_MIN_NORMAL_BITS = 0x00800000;
    public static final float FLOAT_MIN_NORMAL = Float.intBitsToFloat(FLOAT_MIN_NORMAL_BITS);

    public static final BigInteger BIG_MIN_BYTE = BigInteger.valueOf(Byte.MIN_VALUE);
    public static final BigInteger BIG_MAX_BYTE = BigInteger.valueOf(Byte.MAX_VALUE);
    public static final BigInteger BIG_MIN_SHORT = BigInteger.valueOf(Short.MIN_VALUE);
    public static final BigInteger BIG_MAX_SHORT = BigInteger.valueOf(Short.MAX_VALUE);
    public static final BigInteger BIG_MIN_INT = BigInteger.valueOf(Integer.MIN_VALUE);
    public static final BigInteger BIG_MAX_INTEGER = BigInteger.valueOf(Integer.MAX_VALUE);
    public static final BigInteger BIG_MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
    public static final BigInteger BIG_MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

    public static final int DOUBLE_MAX_TWO_EXPONENT = 1023;
    public static final int DOUBLE_MIN_TWO_EXPONENT = -1074;

    public static final int FLOAT_MAX_TWO_EXPONENT = 127;
    public static final int FLOAT_MIN_TWO_EXPONENT = -149;

    private static final double[] positiveTwoPowers = new double[DOUBLE_MAX_TWO_EXPONENT + 1];
    private static final double[] negativeTwoPowers = new double[-DOUBLE_MIN_TWO_EXPONENT + 1];

    /**
     * All the long values satisfying -DOUBLE_MAX_EXACT_LONG <= l <= DOUBLE_MAX_EXACT_LONG
     * are exactly representable as a double precision floting point number, but
     * DOUBLE_MAX_EXACT_LONG + 1 and -DOUBLE_MAX_EXACT_LONG - 1 are not.
     */
    public static final long DOUBLE_MAX_EXACT_LONG = 1L << N_DOUBLE;

    /**
     * All the int values satisfying -FLOAT_MAX_EXACT_INT <= i <= FLOAT_MAX_EXACT_INT
     * are exactly representable as a float precision floting point number, but
     * FLOAT_MAX_EXACT_INT + 1 and -FLOAT_MAX_EXACT_INT - 1 are not.
     */
    public static final int FLOAT_MAX_EXACT_INT = 1 << N_FLOAT;

    static {
        positiveTwoPowers[0] = 1;

        for (int i = 1; i < positiveTwoPowers.length; i++) {
            positiveTwoPowers[i] = positiveTwoPowers[i - 1] * 2;
        }

        assert !Double.isInfinite(positiveTwoPowers[positiveTwoPowers.length - 1]);
        assert positiveTwoPowers[positiveTwoPowers.length - 1] * 2 == Double.POSITIVE_INFINITY;

        negativeTwoPowers[0] = 1;

        for (int i = 1; i < negativeTwoPowers.length; i++) {
            negativeTwoPowers[i] = negativeTwoPowers[i - 1] / 2;
        }

        assert negativeTwoPowers[negativeTwoPowers.length - 1] > 0;
        assert negativeTwoPowers[negativeTwoPowers.length - 1] / 2 == 0;
    }

    /**
     * The value is exactly representable as a double precision number.
     * 3.4028235677973366E38
     */
    public static final double SINGLE_SMALLEST_NEAREST_POSITIVE_INFINITY =
            (Float.MAX_VALUE + Formulas.pow2(128)) / 2;

    /**
     * The value is exactly representable as a double precision number.
     * -3.4028235677973366E38
     */
    public static final double SINGLE_LARGEST_NEAREST_NEGATIVE_INFINITY =
            -SINGLE_SMALLEST_NEAREST_POSITIVE_INFINITY;

    private static final BigInteger[] leftShifted = new BigInteger[1075];

    static {
        leftShifted[0] = BigInteger.ONE;

        for (int i = 1; i < leftShifted.length; i++) {
            leftShifted[i] = leftShifted[i - 1].shiftLeft(1);
        }
    }

    public static BigInteger bigintPow2(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be positive");
        }

        if (n < leftShifted.length) {
            return leftShifted[n];
        } else {
            return BigInteger.ONE.shiftLeft(n);
        }
    }

    /**
     * Quote from http://192.18.99.138/800-7895/800-7895.pdf:
     * <p/>
     * The computed sum S is equal to <i>S = sum(xj * (1 + dj)) + O(Ne2) * sum|xj|</i>,
     * where <i>|dj| <= 2e</i>.
     * Each time a summand is added, there is a correction factor C which will be
     * applied on the next loop. So first subtract the correction C computed in the
     * previous loop from Xj, giving the corrected summand Y. Then add this
     * summand to the running sum S. The low order bits of Y (namely Yl) are lost in
     * the sum. Next compute the high order bits of Y by computing T - S. When Y is
     * subtracted from this, the low order bits of Y will be recovered. These are the
     * bits that were lost in the first sum in the diagram. They become the correction
     * factor for the next loop.
     */
    public static double kahanSummation(double[] x) {
        return kahanSummation(x, 0, x.length);
    }

    public static double kahanSummation(double[] x, int start, int length) {
        if (length == 0) {
            return 0;
        }

        double s = x[start];
        double c = 0;

        for (int j = start + 1; j < start + length; j++) {
            double y = x[j] - c;
            double t = s + y;
            c = (t - s) - y;
            s = t;
        }

        return s;
    }

    public static double kahanSummation(float[] x) {
        return kahanSummation(x, 0, x.length);
    }

    public static double kahanSummation(float[] x, int start, int length) {
        if (length == 0) {
            return 0;
        }

        double s = x[start];
        double c = 0;

        for (int j = start + 1; j < start + length; j++) {
            double y = x[j] - c;
            double t = s + y;
            c = (t - s) - y;
            s = t;
        }

        return s;
    }

    double[] threeElements = new double[3];

    /**
     * The area of a triangle.
     * The rounding error incurred when using (7) to compute the area of a triangle is at
     * most 11e, provided that subtraction is performed with a guard digit, e <= .005, and
     * that square roots are computed to within 1/2 ulp.
     */
    public double triagleArea(double x, double y, double z) {
        fillThreeElementsArray(threeElements, x, y, z);
        sortDescending(threeElements);
        double a = threeElements[0], b = threeElements[1], c = threeElements[2];

        double f0 = a + (b + c), f1 = c - (a - b), f2 = c + (a - b), f3 = a + (b - c);
        double square = Math.sqrt(f0 * f1 * f2 * f3);
        return square / 4;
    }

    private static void fillThreeElementsArray(double[] threeElements, double x, double y, double z) {
        threeElements[0] = x;
        threeElements[1] = y;
        threeElements[2] = z;
    }

    // todo test
    private static void sortDescending(double[] threeElements) {
        double x = threeElements[0], y = threeElements[1], z = threeElements[2];

        if (x <= y) {
            if (y <= z) {
                // y <= z <= x
                fillThreeElementsArray(threeElements, x, z, y);
            } else {
                // z < y
                if (x <= z) {
                    // x <= z < y
                    fillThreeElementsArray(threeElements, y, z, x);
                } else {
                    // z < x <= y
                    fillThreeElementsArray(threeElements, y, x, z);
                }
            }
        } else {
            // y < x
            if (x <= z) {
                // y < x <= z
                fillThreeElementsArray(threeElements, z, x, y);
            } else {
                // z < x
                if (y <= z) {
                    // y <= z < x
                    fillThreeElementsArray(threeElements, x, z, y);
                } else {
                    // z < y < x
                    fillThreeElementsArray(threeElements, x, y, z);
                }
            }
        }
    }

    public static void main(String[] args) {
        double d = (double) Long.MAX_VALUE;
        System.out.println("Long.MAX_VALUE = " + Long.MAX_VALUE);
        System.out.println("d = " + d);
        System.out.println("(d == pow2(63)) = " + (d == pow2(63)));
        System.out.println("previous(d) = " + previous(d));
    }

    /**
     * Computes ln(1 + x).
     * The relative error is at most 5e when 0 <= x < 3/4, provided subtraction is performed
     * with a guard digit, e < 0.1, and ln is computed to within 1/2 ulp.
     * This formula will work for any value of x.
     * todo Unfortunately, the Math.log function documentation states that a result must be within 1 ulp
     * of the correctly rounded result, so it does not have enough precision, so the relative error
     * must be recomputed.
     */
    public static double ln1p(double x) {
        if (1 + x == 1) {
            return x;
        } else {
            double m = Math.log(1 + x) / ((1 + x) - 1);
            return x * m;
        }
    }

    /**
     * p = 53
     * b = 2
     * k = ceil(53.0 / 2.0) = 27
     * x is split as x = xh + xl where each xi is representable using
     * floor(53.0 / 2.0 ) = 26 bits of precision.
     * 
     * @param x the floating point number to be split
     * @return x = xh + xl
     */
    public static double[] split(double x) {
        double[] highLow = new double[2];
        split(x, highLow, 0);
        return highLow;
    }

    private static final double m = 2 ^ 27 + 1;

    public static void split(double x, double[] highLow, int start) {
        highLow[start] = (m * x) - ((m * x) - x);
        highLow[start + 1] = x - highLow[start];
    }

    /**
     * Computes exactly the 2 power or infinity if overflow occurs.
     * 
     * @param n -1074 <= n
     */
    public static double pow2(int n) {
        if (n >= 0) {
            if (n >= positiveTwoPowers.length) {
                return Double.POSITIVE_INFINITY;
            } else {
                return positiveTwoPowers[n];
            }
        } else {
            if (n <= -negativeTwoPowers.length) {
                throw new ArithmeticException("Cannot compute exactly");
            } else {
                return negativeTwoPowers[-n];
            }
        }
    }

    /**
     * @param x positive long or zero
     * @return the highest set bit; -1 if x is zero
     * @throws IllegalArgumentException x is negative
     */
    public static byte highestSetBit(long x) {
        if (x < 0) {
            throw new IllegalArgumentException("x must be positive or zero");
        }

        byte highest;

        if (x < (1L << 32)) {
            int i = (int) x;

            if (i < 0) {
                highest = 31;
            } else {
                highest = highestSetBit(i);
            }
        } else {
            int i = (int) (x >> 32);
            assert i > 0;
            highest = (byte) (32 + highestSetBitNoCheck(i));
        }

        return highest;
    }

    /**
     * @param x >= 0
     * @return 
     */
    public static byte highestSetBit(int x) {
        if (x < 0) {
            throw new IllegalArgumentException("x must be positive or zero");
        }

        return highestSetBitNoCheck(x);
    }

    /**
     * @param x >= 0
     * @return 
     */
    private static byte highestSetBitNoCheck(int x) {
        assert x >= 0;

        byte highest;

        if (x < (1 << 16)) {
            short s = (short) x;

            if (s < 0) {
                highest = 15;
            } else {
                highest = highestSetBit(s);
            }
        } else {
            short s = (short) (x >> 16);
            assert s > 0;
            highest = (byte) (16 + highestSetBit[s]);
        }

        return highest;
    }

    /**
     * @param x >= 0
     * @return 
     */
    public static byte highestSetBit(short x) {
        if (x < 0) {
            throw new IllegalArgumentException("x must be positive or zero");
        }

        return highestSetBit[x];
    }

    private static final byte[] highestSetBit = new byte[Short.MAX_VALUE + 1];

    static {
        log.finest("Begin highestSetBit computation");

        int k = 1;
        byte val = 0;

        highestSetBit[0] = -1;

        for (int i = 1; i < highestSetBit.length; i <<= 1) {
            for (int j = i; j < i << 1; j++) {
                highestSetBit[j] = val;
            }

            val++;
        }

        assert val == 15;

        log.finest("End highestSetBit computation");
    }

    public static byte highestSetBit(byte x) {
        if (x < 0) {
            throw new IllegalArgumentException("x must be positive or zero");
        }

        return highestSetBit[x];
    }

    private static final byte[] lowestSetBit = new byte[1 << 14 + 1];

    static {
        // not necessary, but useful for debug
        Arrays.fill(lowestSetBit, Byte.MIN_VALUE);

        lowestSetBit[0] = -1;
        for (byte i = 0; i < 15; i++) {
            lowestSetBit[1 << i] = i;
        }
    }

    /**
     * @param x any int value
     */
    public static byte lowestSetBit(long x) {
        byte lowest;

        int i = (int) x;

        if (i == 0) {
            if (x == 0) {
                lowest = -1;
            } else {
                i = (int) (x >> 32);
                lowest = (byte) (32 + lowestSetBit(i));
            }
        } else {
            lowest = lowestSetBit(i);
        }

        return lowest;
    }

    /**
     * @param x any int value
     */
    public static byte lowestSetBit(int x) {
        byte lowest;

        short s = (short) x;

        if (s == 0) {
            if (x == 0) {
                lowest = -1;
            } else {
                s = (short) (x >> 16);
                lowest = (byte) (16 + lowestSetBit(s));
            }
        } else {
            lowest = lowestSetBit(s);
        }

        return lowest;
    }

    /**
     * @param x any short value
     */
    public static byte lowestSetBit(short x) {
        short y = (short)(x & -x);
        return twoPowerOrZeroLowest(y);
    }

    private static byte twoPowerOrZeroLowest(short x) {
        assert (short)(x & -x) == x;

        final byte lowest;

        if (x == Short.MIN_VALUE) {
            lowest = 15;
        } else {
            assert x >= 0;
            lowest = lowestSetBit[x];
            assert -1 <= lowest && lowest <= 14;
        }

        return lowest;
    }

    public static boolean sameSignum(long x, long y) {
        if (x > 0) {
            return y > 0;
        } else {
            if (x == 0) {
                return y == 0;
            } else {
                return y < 0;
            }
        }
    }

    public static int compare(long x, long y) {
        if (x < y) {
            return -1;
        } else {
            if (x > y) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static final long DOUBLE_NEGATIVE_ZERO_BITS = Double.doubleToLongBits(-0.0);
    public static final long DOUBLE_POSITIVE_ZERO_BITS = Double.doubleToLongBits(+0.0);

    public static final int FLOAT_NEGATIVE_ZERO_BITS = Float.floatToIntBits(-0.0F);
    public static final int FLOAT_POSITIVE_ZERO_BITS = Float.floatToIntBits(+0.0F);

    public static float previous(float x) {
        checkFiniteParameter(x);

        int bits = Float.floatToIntBits(x);
        float f;

        if (x > 0) {
            f = Float.intBitsToFloat(bits - 1);
        } else {
            if (bits == FLOAT_POSITIVE_ZERO_BITS) {
                return -0.0F;
            } else {
                f = -next(-x);
            }
        }

        return f;
    }

    public static double previous(double x) {
        checkFiniteParameter(x);

        long bits = Double.doubleToLongBits(x);
        double d;

        if (x > 0) {
            d = Double.longBitsToDouble(bits - 1);
        } else {
            if (bits == DOUBLE_POSITIVE_ZERO_BITS) {
                return -0.0;
            } else {
                d = -next(-x);
            }
        }

        return d;
    }

    /**
     * @return Never Double.POSITIVE_INFINITY.
     */
    public static double previousIfExists(double z) {
        if (isFinite(z)) {
            return previous(z);
        } else {
            if (z == Double.NEGATIVE_INFINITY || Double.isNaN(z)) {
                return z;
            } else {
                assert z == Double.POSITIVE_INFINITY;
                return Double.MAX_VALUE;
            }
        }
    }

    public static float next(float x) {
        checkFiniteParameter(x);

        int bits = Float.floatToIntBits(x);
        float d;

        if (x > 0 || bits == FLOAT_POSITIVE_ZERO_BITS) {
            d = Float.intBitsToFloat(bits + 1);
        } else {
            if (bits == FLOAT_NEGATIVE_ZERO_BITS) {
                return +0.0F;
            } else {
                d = -previous(-x);
            }
        }

        return d;
    }

    public static double next(double x) {
        checkFiniteParameter(x);

        long bits = Double.doubleToLongBits(x);
        double d;

        if (x > 0 || bits == DOUBLE_POSITIVE_ZERO_BITS) {
            d = Double.longBitsToDouble(bits + 1);
        } else {
            if (bits == DOUBLE_NEGATIVE_ZERO_BITS) {
                return +0.0;
            } else {
                d = -previous(-x);
            }
        }

        return d;
    }

    /**
     * @return Never Double.NEGATIVE_INFINITY.
     */
    public static double nextIfExists(double z) {
        if (isFinite(z)) {
            return next(z);
        } else {
            if (z == Double.POSITIVE_INFINITY || Double.isNaN(z)) {
                return z;
            } else {
                assert z == Double.NEGATIVE_INFINITY;
                return Double.MIN_VALUE;
            }
        }
    }

    private static void checkFiniteParameter(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            throw new IllegalArgumentException(x + " is not a finite number");
        }
    }

    private static void checkFiniteParameter(float x) {
        if (Float.isNaN(x) || Float.isInfinite(x)) {
            throw new IllegalArgumentException(x + " is not a finite number");
        }
    }

    /**
     * @return big-endian two's-complement binary representation of value
     */
    public static byte[] getBytes(long value) {
        byte[] bytes = new byte[8];
        getBytes(value, bytes, 0);
        return bytes;
    }

    public static void getBytes(long value, byte[] bytes, int start) {
        for (int i = 7; i >= 0; i--) {
            bytes[start + i] = (byte) value;
            value >>>= 8;
        }
    }

    public static boolean isFinite(double x) {
        return !Double.isNaN(x) && !Double.isInfinite(x);
    }

    public static boolean isFinite(float x) {
        return !Float.isNaN(x) && !Float.isInfinite(x);
    }

    public static boolean inClosedInterval(long x, long left, long right) {
        return left <= x && x <= right;
    }

    public static boolean inClosedInterval(int x, int left, int right) {
        return left <= x && x <= right;
    }

    public static boolean inOpenInterval(double x, double left, double right) {
        return left < x && x < right;
    }

    /**
     * Whenever left >= right, returns false.
     */
    public static boolean inOpenInterval(int x, int left, int right) {
        return left < x && x < right;
    }

    /**
     * floatTemp[0] = floatTemp[1] = Float.NEGATIVE_INFINITY iff d == Double.NEGATIVE_INFINITY.
     * floatTemp[0] = floatTemp[1] = Float.POSITIVE_INFINITY iff d == Double.POSITIVE_INFINITY.
     * If d is +0.0, then floatTemp[0] = floatTemp[1] = d = +0.0F.
     * If d is -0.0, then floatTemp[0] = floatTemp[1] = d = -0.0F.
     * The interval returned is never (-0.0F, +0.0F).
     */
    public static void floatInterval(double d, float[] floatTemp) {
        if (Double.isNaN(d)) {
            throw new IllegalArgumentException("d parameter must not be NaN");
        }

        // conversion rounds to nearest
        float f = (float) d;

        // f suffers widening conversion to double
        if (f < d) {
            if (f == Float.NEGATIVE_INFINITY) {
                assert d < -Float.MAX_VALUE && d >= -Double.MAX_VALUE;
                floatTemp[0] = Float.NEGATIVE_INFINITY;
                floatTemp[1] = -Float.MAX_VALUE;
            } else {
                floatTemp[0] = f;
                floatTemp[1] = next(f);
            }
        } else {
            if (f > d) {
                if (f == Float.POSITIVE_INFINITY) {
                    assert d > Float.MAX_VALUE && d <= Double.MAX_VALUE;
                    floatTemp[0] = Float.MAX_VALUE;
                    floatTemp[1] = Float.POSITIVE_INFINITY;
                } else {
                    floatTemp[0] = previous(f);
                    floatTemp[1] = f;
                }
            } else {
                assert f == d;
                floatTemp[0] = floatTemp[1] = f;
            }
        }
    }

    private static final double doublePow2_63 = pow2(63);
    private static final double doublePreviuosPow2_63 = previous(doublePow2_63);

    /**
     * -pow2(63) <= doubleTemp[0] <= doubleTemp[1] < pow2(63)
     * If l == 0 then doubleTemp[0] = doubleTemp[1] = +0.0.
     */
    public static void doubleInterval(long l, double[] doubleTemp) {
        // round-to-nearest
        double d = (double) l;
        assert -doublePow2_63 <= d && d <= doublePow2_63;

        // truncating conversion; exact if d < doublePow2_63
        long dAsLong = (long) d;

        if (dAsLong < l) {
            doubleTemp[0] = d;
            doubleTemp[1] = next(d);
        } else {
            if (dAsLong > l) {
                // d can be doublePow2_63, but the result is still ok
                doubleTemp[0] = previous(d);
                doubleTemp[1] = d;
            } else {
                assert dAsLong == l;

                if (l == Long.MAX_VALUE) {
                    assert d == doublePow2_63;
                    doubleTemp[0] = doublePreviuosPow2_63;
                    doubleTemp[1] = doublePow2_63;
                } else {
                    assert d <= doublePreviuosPow2_63;
                    // l is actually an exact double value
                    doubleTemp[0] = doubleTemp[1] = d;
                }
            }
        }
    }

    private static final float floatPow2_63 = (float) pow2(63);
    private static final float floatPreviuosPow2_63 = previous(floatPow2_63);

    /**
     * -pow2(63) <= floatTemp[0] <= floatTemp[1] < pow2(63)
     * If l == 0 then floatTemp[0] = floatTemp[1] = +0.0.
     */
    public static void floatInterval(long l, float[] floatTemp) {
        // round-to-nearest
        float f = (float) l;
        assert -floatPow2_63 <= f && f <= floatPow2_63;

        // truncating conversion; exact if f < floatPow2_63
        long dAsLong = (long) f;

        if (dAsLong < l) {
            floatTemp[0] = f;
            floatTemp[1] = next(f);
        } else {
            if (dAsLong > l) {
                // f can be floatPow2_63, but the result is still ok
                floatTemp[0] = previous(f);
                floatTemp[1] = f;
            } else {
                assert dAsLong == l;

                if (l == Long.MAX_VALUE) {
                    assert f == floatPow2_63;
                    floatTemp[0] = floatPreviuosPow2_63;
                    floatTemp[1] = floatPow2_63;
                } else {
                    assert f <= floatPreviuosPow2_63;
                    // l is actually an exact float value
                    floatTemp[0] = floatTemp[1] = f;
                }
            }
        }
    }


    public static boolean isEven(float f) {
        if (Float.isNaN(f)) {
            throw new IllegalArgumentException("f parameter must not be NaN");
        }

        int bits = Float.floatToIntBits(f);
        boolean evenBits = (bits & 1) == 0;
        return evenBits;
    }

    public static boolean isEven(double d) {
        if (Double.isNaN(d)) {
            throw new IllegalArgumentException("d parameter must not be NaN");
        }

        long bits = Double.doubleToLongBits(d);
        boolean evenBits = (bits & 1) == 0;
        return evenBits;
    }

    public static boolean signedZerosLess(double a, double b) {
        checkNaNParameters("a", a, "b", b);
        return a < b || (Double.doubleToLongBits(a) == DOUBLE_NEGATIVE_ZERO_BITS
                && Double.doubleToLongBits(b) == DOUBLE_POSITIVE_ZERO_BITS);
    }

    public static boolean signedZerosLess(float a, float b) {
        checkNaNParameters("a", a, "b", b);
        return a < b || (Float.floatToIntBits(a) == FLOAT_NEGATIVE_ZERO_BITS
                && Float.floatToIntBits(b) == FLOAT_POSITIVE_ZERO_BITS);
    }

    private static void checkNaNParameters(String aName, double a, String bName, double b) {
        if (Double.isNaN(a) || Double.isNaN(b)) {
            throw new IllegalArgumentException(aName + " = " + a + "; " + bName + " = " + b);
        }
    }

    private static void checkNaNParameters(String aName, float a, String bName, float b) {
        if (Float.isNaN(a) || Float.isNaN(b)) {
            throw new IllegalArgumentException(aName + " = " + a + "; " + bName + " = " + b);
        }
    }

    private static void checkNaNParameter(String aName, double a) {
        if (Double.isNaN(a)) {
            throw new IllegalArgumentException(aName + " = " + a);
        }
    }

    private static void checkNaNParameter(String aName, float a) {
        if (Float.isNaN(a)) {
            throw new IllegalArgumentException(aName + " = " + a);
        }
    }

    /**
     * The java.lang.Math class misses the overload Math.floor(float).
     * Returns the largest (closest to positive infinity)
     * <code>float</code> value that is not greater than the argument and
     * is equal to a mathematical integer. Special cases:
     * <ul><li>If the argument value is already equal to a mathematical
     * integer, then the result is the same as the argument.
     * <li>If the argument is NaN or an infinity or positive zero or
     * negative zero, then the result is the same as the argument.</ul>
     * 
     * @param a a value.
     * @return the largest (closest to positive infinity)
     *         floating-point value that is not greater than the argument
     *         and is equal to a mathematical integer.
     */
    public static float floor(float a) {
        // preserve raw int bits for NaN
        if (Float.isNaN(a)) {
            return a;
        }

        double d = Math.floor(a);
        float dAsFloat = (float) d;
        assert dAsFloat == d;
        return dAsFloat;
    }

    /**
     * The java.lang.Math class misses the overload Math.ceil(float).
     * <p/>
     * Returns the smallest (closest to negative infinity)
     * <code>float</code> value that is not less than the argument and is
     * equal to a mathematical integer. Special cases:
     * <ul><li>If the argument value is already equal to a mathematical
     * integer, then the result is the same as the argument.
     * <li>If the argument is NaN or an infinity or positive zero or negative
     * zero, then the result is the same as the argument.
     * <li>If the argument value is less than zero but greater than -1.0,
     * then the result is negative zero.</ul>
     * Note that the value of <code>Math.ceil(x)</code> is exactly the
     * value of <code>-Math.floor(-x)</code>.
     * 
     * @param a a value.
     * @return the smallest (closest to negative infinity)
     *         floating-point value that is not less than the argument
     *         and is equal to a mathematical integer.
     */
    public static float ceil(float a) {
        // preserve raw int bits for NaN
        if (Float.isNaN(a)) {
            return a;
        }

        double d = Math.ceil(a);
        float dAsFloat = (float) d;
        assert dAsFloat == d;
        return dAsFloat;
    }

    /**
     * Zero is considered less than or equal to -0.0 and +0.0.
     * 
     * @return l <= d
     */
    public static boolean lessThanOrEqual(long l, double d) {
        if (d >= 0) {
            // truncating conversion, towards minus infinity
            long dAsLong = (long) d;

            // dAsLong is mathematically less than or equal to d
            return l <= dAsLong;
        } else {
            // parameter check deferred
            checkNaNParameter("d", d);

            assert d < 0;

            // truncating conversion, towards positive infinity
            long dAsLong = (long) d;

            if (l < dAsLong) {
                return true;
            } else {
                if (l > dAsLong) {
                    return false;
                } else {
                    assert l == dAsLong;

                    // l <= d iff d is exactly a long value
                    // todo optimization. We already know that d < 0 and dAsLong.
                    return isExactLong(d);
                }
            }
        }
    }

    public static boolean isExactLong(double d) {
        if (d < -doublePow2_63 || d >= doublePow2_63 || Double.isNaN(d)) {
            return false;
        } else {
            // d is in the proper range

            if (d <= -DOUBLE_MAX_EXACT_LONG || d >= DOUBLE_MAX_EXACT_LONG) {
                return true;
            } else {
                long dAsLong = (long) d;
                assert -DOUBLE_MAX_EXACT_LONG < dAsLong && dAsLong < DOUBLE_MAX_EXACT_LONG;

                // exact conversion
                double dResurrected = (double) dAsLong;
                return dResurrected == d;
            }
        }
    }

    public static boolean isExactLong(float f) {
        if (f < -floatPow2_63 || f >= floatPow2_63 || Double.isNaN(f)) {
            return false;
        } else {
            // f is in the proper range

            if (f <= -FLOAT_MAX_EXACT_INT || f >= FLOAT_MAX_EXACT_INT) {
                return true;
            } else {
                long fAsLong = (long) f;
                assert -FLOAT_MAX_EXACT_INT < fAsLong && fAsLong < FLOAT_MAX_EXACT_INT;

                // exact conversion
                float fResurrected = (float) fAsLong;
                return fResurrected == f;
            }
        }
    }

    public static boolean isExactDouble(long l) {
        // round-to-nearest
        double d = (double) l;
        // truncation
        long lResurrected = (long) d;

        return (lResurrected == l && isExactLong(d));
    }

    public static boolean isExactFloat(long l) {
        // round-to-nearest
        float f = (float) l;
        // truncation
        long lResurrected = (long) f;

        return lResurrected == l && isExactLong(f);
    }

    public static boolean isExactFloat(double d) {
        // round-to-nearest
        float f = (float) d;
        // exact
        double dResurrected = (double)f;

        return d == dResurrected;
    }

    public static boolean greaterThanOrEqual(long l, double d) {
        if (d <= 0) {
            // truncating conversion, towards positive infinity
            long dAsLong = (long) d;

            // dAsLong is mathematically less than or equal to d
            return l >= dAsLong;
        } else {
            // parameter check deferred
            checkNaNParameter("d", d);

            assert d > 0;

            // truncating conversion, towards negative infinity
            long dAsLong = (long) d;

            if (l > dAsLong) {
                return true;
            } else {
                if (l < dAsLong) {
                    return false;
                } else {
                    assert l == dAsLong;

                    // l >= d iff d is exactly a long value
                    // todo optimization. We already know that d > 0 and dAsLong.
                    return isExactLong(d);
                }
            }
        }
    }

    public static long truncateToLong(BigInteger v) {
        final long l;

        switch (v.signum()) {
            case -1:
                if (v.compareTo(BIG_MIN_LONG) <= 0) {
                    l = Long.MIN_VALUE;
                } else {
                    // exact narrowing primitive conversion
                    l = v.longValue();
                }
                break;
            case 0:
                l = 0;
                break;
            case 1:
                if (v.compareTo(BIG_MAX_LONG) >= 0) {
                    l = Long.MAX_VALUE;
                } else {
                    // exact narrowing primitive conversion
                    l = v.longValue();
                }
                break;
            default:
                throw new InternalError("v.signum() = " + v.signum());
        }

        return l;
    }

    public static int truncateToInt(BigInteger v) {
        final int i;

        switch (v.signum()) {
            case -1:
                if (v.compareTo(BIG_MIN_INT) <= 0) {
                    i = Integer.MIN_VALUE;
                } else {
                    // exact narrowing primitive conversion
                    i = v.intValue();
                }
                break;
            case 0:
                i = 0;
                break;
            case 1:
                if (v.compareTo(BIG_MIN_INT) >= 0) {
                    i = Integer.MAX_VALUE;
                } else {
                    // exact narrowing primitive conversion
                    i = v.intValue();
                }
                break;
            default:
                throw new InternalError("v.signum() = " + v.signum());
        }

        return i;
    }

    public static short truncateToShort(BigInteger v) {
        final short s;

        switch (v.signum()) {
            case -1:
                if (v.compareTo(BIG_MIN_SHORT) <= 0) {
                    s = Short.MIN_VALUE;
                } else {
                    // exact narrowing primitive conversion
                    s = v.shortValue();
                }
                break;
            case 0:
                s = 0;
                break;
            case 1:
                if (v.compareTo(BIG_MAX_SHORT) >= 0) {
                    s = Short.MAX_VALUE;
                } else {
                    // exact narrowing primitive conversion
                    s = v.shortValue();
                }
                break;
            default:
                throw new InternalError("v.signum() = " + v.signum());
        }

        return s;
    }

    public static byte truncateToByte(BigInteger v) {
        final byte b;

        switch (v.signum()) {
            case -1:
                if (v.compareTo(BIG_MIN_BYTE) <= 0) {
                    b = Byte.MIN_VALUE;
                } else {
                    // exact narrowing primitive conversion
                    b = v.byteValue();
                }
                break;
            case 0:
                b = 0;
                break;
            case 1:
                if (v.compareTo(BIG_MAX_BYTE) >= 0) {
                    b = Byte.MAX_VALUE;
                } else {
                    // exact narrowing primitive conversion
                    b = v.byteValue();
                }
                break;
            default:
                throw new InternalError("v.signum() = " + v.signum());
        }

        return b;
    }

}
