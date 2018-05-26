package precisefloating.directedrounding;

import precisefloating.Formulas;

import java.math.BigInteger;

/**
 * Abstract base class for finite numbers supporting directed rounding. It caches the values
 * computed in every rounding direction. The internal arrays are constructed on the first use,
 * so there is almost no overhead added to an extending class as long as the Number methods are not
 * called. Note that this class is mutable because of the setter for roundingMode, and it should
 * only be used for with delegation by immutable numbers.
 * Superclass for RoundNumber.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public abstract class RoundNumberBase extends Number {

    private boolean[][] computed;
    protected double[] doubleValues;
    protected float[] floatValues;
    protected long[] longValues;
    protected int[] intValues;
    protected short[] shortValues;
    protected byte[] byteValues;
    protected BigInteger[] bigIntegerValues;

    private RoundingMode roundingMode = RoundingMode.ROUND_HALF_EVEN;

    protected final boolean isDoubleComputed(int idx) {
        return getComputed()[0][idx];
    }

    protected final boolean isDoubleComputed() {
        return isDoubleComputed(directionIndex());
    }

    protected final boolean isFloatComputed(int idx) {
        return getComputed()[1][idx];
    }

    protected final boolean isFloatComputed() {
        return isFloatComputed(directionIndex());
    }

    protected final boolean isLongComputed(int idx) {
        return getComputed()[2][idx];
    }

    protected final boolean isLongComputed() {
        return isLongComputed(directionIndex());
    }

    protected final boolean isIntComputed(int idx) {
        return getComputed()[3][idx];
    }

    protected final boolean isIntComputed() {
        return isIntComputed(directionIndex());
    }

    protected final boolean isShortComputed(int idx) {
        return getComputed()[4][idx];
    }

    protected final boolean isShortComputed() {
        return isShortComputed(directionIndex());
    }

    protected final boolean isByteComputed(int idx) {
        return getComputed()[5][idx];
    }

    protected final boolean isByteComputed() {
        return isByteComputed(directionIndex());
    }

    protected final boolean isBigIntegerComputed(int idx) {
        return getComputed()[6][idx];
    }

    protected final boolean isBigIntegerComputed() {
        return isBigIntegerComputed(directionIndex());
    }

    protected final boolean[][] getComputed() {
        if (computed == null) {
            // double, float, long, int, short, byte
            computed = new boolean[7][3];
        }
        return computed;
    }

    protected final boolean[] getComputedDoubles() {
        return getComputed()[0];
    }

    protected final boolean[] getComputedFloats() {
        return getComputed()[1];
    }

    protected final boolean[] getComputedLongs() {
        return getComputed()[2];
    }

    protected final boolean[] getComputedInts() {
        return getComputed()[3];
    }

    protected final boolean[] getComputedShorts() {
        return getComputed()[4];
    }

    protected final boolean[] getComputedBytes() {
        return getComputed()[5];
    }

    protected final boolean[] getComputedBigIntegers() {
        return getComputed()[6];
    }

    protected final double[] getDoubleValues() {
        if (doubleValues == null) {
            doubleValues = new double[3];
        }
        return doubleValues;
    }

    protected final float[] getFloatValues() {
        if (floatValues == null) {
            floatValues = new float[3];
        }
        return floatValues;
    }

    protected final long[] getLongValues() {
        if (longValues == null) {
            longValues = new long[3];
        }
        return longValues;
    }

    protected final int[] getIntValues() {
        if (intValues == null) {
            intValues = new int[3];
        }
        return intValues;
    }

    protected final short[] getShortValues() {
        if (shortValues == null) {
            shortValues = new short[3];
        }
        return shortValues;
    }

    protected final byte[] getByteValues() {
        if (byteValues == null) {
            byteValues = new byte[3];
        }
        return byteValues;
    }

    protected final BigInteger[] getBigIntegerValues() {
        if (bigIntegerValues == null) {
            bigIntegerValues = new BigInteger[3];
        }
        return bigIntegerValues;
    }

    public final RoundingMode getRoundingMode() {
        assert roundingMode != null;
        return roundingMode;
    }

    public final void setRoundingMode(RoundingMode roundingMode) {
        if (roundingMode == null) {
            throw new NullPointerException("roundingMode must not be null");
        }
        this.roundingMode = roundingMode;
    }

    protected void computed(int idx, double d) {
        assert idx > 0 || d != Double.POSITIVE_INFINITY
                : "round-to-negative infinity cannot result in positive infinity";
        assert idx < 2 || d != Double.NEGATIVE_INFINITY
                : "round-to-positive infinity cannot result in negative infinity";

        if (!getComputedDoubles()[idx]) {
            getDoubleValues()[idx] = d;
            getComputedDoubles()[idx] = true;
        } else {
            assert getDoubleValues()[idx] == d;
        }
    }

    protected void computed(int idx, float f) {
        assert idx > 0 || f != Float.POSITIVE_INFINITY
                : "round-to-negative infinity cannot result in positive infinity";
        assert idx < 2 || f != Float.NEGATIVE_INFINITY
                : "round-to-positive infinity cannot result in negative infinity";

        if (!getComputedFloats()[idx]) {
            getFloatValues()[idx] = f;
            getComputedFloats()[idx] = true;
        } else {
            assert getFloatValues()[idx] == f;
        }
    }

    protected void computed(int idx, long l) {
        if (!getComputedLongs()[idx]) {
            getLongValues()[idx] = l;
            getComputedLongs()[idx] = true;
        } else {
            assert getLongValues()[idx] == l;
        }
    }

    protected void computed(int idx, int i) {
        if (!getComputedInts()[idx]) {
            getIntValues()[idx] = i;
            getComputedInts()[idx] = true;
        } else {
            assert getIntValues()[idx] == i;
        }
    }

    protected void computed(int idx, short s) {
        if (!getComputedShorts()[idx]) {
            getShortValues()[idx] = s;
            getComputedShorts()[idx] = true;
        } else {
            assert getShortValues()[idx] == s;
        }
    }

    protected void computed(int idx, byte b) {
        if (!getComputedBytes()[idx]) {
            getByteValues()[idx] = b;
            getComputedBytes()[idx] = true;
        } else {
            assert getByteValues()[idx] == b;
        }
    }

    protected void computed(int idx, BigInteger bigInt) {
        if (!getComputedBigIntegers()[idx]) {
            getBigIntegerValues()[idx] = bigInt;
            getComputedBigIntegers()[idx] = true;
        } else {
            assert getBigIntegerValues()[idx] == bigInt;
        }
    }

    public final double doubleValue() {
        int idx = directionIndex();
        double d;

        if (!isDoubleComputed()) {
            // compute the double value fot the current rounding mode
            d = computeDoubleValue();
            computed(idx, d);
        } else {
            d = getDoubleValues()[idx];
        }

        return getDoubleValues()[idx];
    }

    protected abstract double computeDoubleValue();

    public final float floatValue() {
        int idx = directionIndex();
        float f;

        if (!isFloatComputed()) {
            // compute the float value fot the current rounding mode
            f = computeFloatValue();
            computed(idx, f);
        } else {
            f = getFloatValues()[idx];
        }

        return getFloatValues()[idx];
    }

    protected abstract float computeFloatValue();

    /**
     * A number that is -0 in round-to-negative infinity mode is considered to be exacty equal
     * to zero by the methods returning integral types, no matter if in the other rounding modes
     * it is -0 or +0.
     */
    public final long longValue() {
        int idx = directionIndex();
        long l;

        if (!isLongComputed()) {
            // compute the long value fot the current rounding mode
            l = computeLongValue();
            computed(idx, l);
        } else {
            l = getLongValues()[idx];
        }

        return getLongValues()[idx];
    }

    protected long computeLongValue() {
        BigInteger v = bigIntegerValue();
        return Formulas.truncateToLong(v);
    }

    public final int intValue() {
        int idx = directionIndex();
        int i;

        if (!isIntComputed()) {
            // compute the int value fot the current rounding mode
            i = computeIntValue();
            computed(idx, i);
        } else {
            i = getIntValues()[idx];
        }

        return getIntValues()[idx];
    }

    protected int computeIntValue() {
        BigInteger v = bigIntegerValue();
        return Formulas.truncateToInt(v);
    }

    public final short shortValue() {
        int idx = directionIndex();
        short s;

        if (!isShortComputed()) {
            // compute the short value fot the current rounding mode
            s = computeShortValue();
            computed(idx, s);
        } else {
            s = getShortValues()[idx];
        }

        return getShortValues()[idx];
    }

    protected short computeShortValue() {
        BigInteger v = bigIntegerValue();
        return Formulas.truncateToShort(v);
    }

    public final byte byteValue() {
        int idx = directionIndex();
        byte s;

        if (!isByteComputed()) {
            // compute the byte value fot the current rounding mode
            s = computeByteValue();
            computed(idx, s);
        } else {
            s = getByteValues()[idx];
        }

        return getByteValues()[idx];
    }

    protected byte computeByteValue() {
        BigInteger v = bigIntegerValue();
        return Formulas.truncateToByte(v);
    }

    public final BigInteger bigIntegerValue() {
        int idx = directionIndex();
        BigInteger s;

        if (!isBigIntegerComputed()) {
            // compute the BigInteger value fot the current rounding mode
            s = computeBigIntegerValue();
            computed(idx, s);
        } else {
            s = getBigIntegerValues()[idx];
        }

        return getBigIntegerValues()[idx];
    }

    protected abstract BigInteger computeBigIntegerValue();

    protected int directionIndex() {
        return directionIndex(roundingMode);
    }

    static int directionIndex(RoundingMode mode) {
        if (mode == RoundingMode.ROUND_FLOOR) {
            return 0;
        } else {
            if (mode == RoundingMode.ROUND_HALF_EVEN) {
                return 1;
            } else {
                assert mode == RoundingMode.ROUND_CEILING;
                return 2;
            }
        }
    }

    protected static RoundingMode directionToRoundingMode(int direction) {
        final RoundingMode mode;

        switch (direction) {
            case 0:
                mode = RoundingMode.ROUND_FLOOR;
                break;
            case 1:
                mode = RoundingMode.ROUND_HALF_EVEN;
                break;
            case 2:
                mode = RoundingMode.ROUND_CEILING;
                break;
            default:
                throw new InternalError("direction = " + direction);
        }

        return mode;
    }

}
