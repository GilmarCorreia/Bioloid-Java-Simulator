package precisefloating;

import precisefloating.directedrounding.RoundNumber;
import precisefloating.directedrounding.RoundNumberBase;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Not safe for use in concurrent threads. Could extend RoundNumberBase, but using RoundNumber as
 * the base class adds a lot of precomputed values with only about a 10% performance hit for
 * the single call scenario.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class RoundRational extends RoundNumber {

    private final Rational r;
    private final int[] intTemp = new int[2];

    public RoundRational(Rational r) {
        this.r = r.reduced();

        if (r.getExactDoubleValue() != null) {
            Arrays.fill(getDoubleValues(), r.getExactDoubleValue().doubleValue());
            Arrays.fill(getComputedDoubles(), true);
        }
    }

    public Rational getRational() {
        return r;
    }

    protected double computeDoubleValue() {
        assert r.getExactDoubleValue() == null : "doubleValue() should have already been computed";

        final double d;

        switch (r.signum()) {
            case -1:
                d = -positiveDoubleValue(2 - directionIndex(), r.negate());
                break;
            case 0:
                d = +0.0;
                break;
            case 1:
                d = positiveDoubleValue(directionIndex(), r);
                break;
            default:
                throw new InternalError("r.signum() = " + r.signum());
        }

        return d;
    }

    private double positiveDoubleValue(int direction, Rational rational) {
        assert rational.signum() == +1;

        rational.log2Interval(intTemp);

        double d;

        switch (direction) {
            case 0:
                if (intTemp[0] < Formulas.DOUBLE_MIN_TWO_EXPONENT) {
                    assert intTemp[1] <= Formulas.DOUBLE_MIN_TWO_EXPONENT;
                    assert rational.compareTo(Rational.DOUBLE_MIN_VALUE) == -1;
                    d = +0.0;
                } else {
                    assert rational.compareTo(Rational.DOUBLE_MIN_VALUE) >= 0;

                    // quick check for the intTemp[0] > Formulas.DOUBLE_MAX_TWO_EXPONENT condition
                    if (intTemp[0] > Formulas.DOUBLE_MAX_TWO_EXPONENT
                            || rational.compareTo(Rational.DOUBLE_MAX_VALUE) >= 0) {
                        // rational >= Double.MAX_VALUE
                        d = Double.MAX_VALUE;
                    } else {
                        // Double.MIN_VALUE <= rational < Double.MAX_VALUE
                        assert Formulas.DOUBLE_MIN_TWO_EXPONENT <= intTemp[0]
                                && intTemp[1] <= Formulas.DOUBLE_MAX_TWO_EXPONENT + 1;
                        assert rational.compareTo(Rational.DOUBLE_MAX_VALUE) == -1;

                        if (intTemp[0] == intTemp[1]) {
                            // exact two power double value
                            d = Formulas.pow2(intTemp[0]);
                        } else {
                            // pow2(intTemp[0]) < rational < pow2(intTemp[1])
                            assert rational.compareTo(Rational.create(Formulas.pow2(intTemp[0]))) == +1
                                    && rational.compareTo(Rational.create(Formulas.pow2(intTemp[1]))) == -1;
                            if (intTemp[1] <= 1 - Formulas.DOUBLE_BIAS) {
                                // the result is a subnormal number
                                assert rational.compareTo(Rational.DOUBLE_MIN_NORMAL) == -1;
                                /*
                                pow2(-1074) < rational < pow2(-1022) implies 1 < rational * pow2(1074) < pow2(52)
                                The fraction is floor(rational * pow2(1074)).
                                */

                                Rational shifted = rational.multiplyTwoPower(1074);
                                BigInteger shiftedFloor = shifted.floor();
                                assert shiftedFloor.compareTo(BigInteger.ONE) >= 0
                                        && shiftedFloor.compareTo(Formulas.bigintPow2(52)) == -1;
                                assert shiftedFloor.bitLength() <= 52;

                                // exact narrowing primitive conversion
                                long bits = shiftedFloor.longValue();
                                d = Double.longBitsToDouble(bits);
                                assert Double.MIN_VALUE <= d && d <= Formulas.DOUBLE_MAX_SUBNORMAL;
                            } else {
                                // Formulas.DOUBLE_MIN_NORMAL < rational < Double.MAX_VALUE
                                // Formulas.DOUBLE_MIN_NORMAL <= result < Double.MAX_VALUE
                                assert rational.compareTo(Rational.DOUBLE_MIN_NORMAL) == +1;
                                // the exponent is intTemp[0]
                                Rational shifted = rational.multiplyTwoPower(Formulas.N_DOUBLE - 1 - intTemp[0]);
                                BigInteger shiftedFloor = shifted.floor();
                                assert shiftedFloor.compareTo(Formulas.bigintPow2(Formulas.N_DOUBLE - 1)) >= 0
                                        && shiftedFloor.compareTo(Formulas.bigintPow2(Formulas.N_DOUBLE)) == -1;

                                // exact narrowing primitive conversion
                                long shiftedFloorBits = shiftedFloor.longValue();

                                // exact conversion
                                double shiftedFloorAsDouble = (double) shiftedFloorBits;

                                // exact. Do not collapse the last to factors into a single one.
                                d = shiftedFloorAsDouble / Formulas.pow2(Formulas.N_DOUBLE - 1)
                                        * Formulas.pow2(intTemp[0]);

                                assert Formulas.DOUBLE_MIN_NORMAL <= d && d < Double.MAX_VALUE;
                            }
                        }
                    }
                    assert Formulas.isFinite(d);
                }

                break;
            case 1:
                if (intTemp[1] <= Formulas.DOUBLE_MIN_TWO_EXPONENT - 1) {
                    assert rational.compareTo(Rational.DOUBLE_HALF_MIN_VALUE) <= 0;
                    d = +0.0;
                } else {
                    assert rational.compareTo(Rational.DOUBLE_HALF_MIN_VALUE) > 0;

                    // quick check for the intTemp[0] > Formulas.DOUBLE_MAX_TWO_EXPONENT condition
                    if (intTemp[0] > Formulas.DOUBLE_MAX_TWO_EXPONENT
                            || rational.compareTo(Rational.DOUBLE_MAX_NEAREST_THRESHOLD) >= 0) {
                        // rational >= Rational.DOUBLE_MAX_NEAREST_THRESHOLD
                        d = Double.POSITIVE_INFINITY;
                    } else {
                        // the result is a finite number
                        // Rational.DOUBLE_HALF_MIN_VALUE < rational < Rational.DOUBLE_MAX_NEAREST_THRESHOLD
                        assert Formulas.DOUBLE_MIN_TWO_EXPONENT - 1 <= intTemp[0]
                                && intTemp[1] <= Formulas.DOUBLE_MAX_TWO_EXPONENT + 1;
                        assert rational.compareTo(Rational.DOUBLE_MAX_NEAREST_THRESHOLD) == -1;

                        if (intTemp[0] == intTemp[1]) {
                            // exact double value
                            d = Formulas.pow2(intTemp[0]);
                        } else {
                            // pow2(intTemp[0]) < rational < pow2(intTemp[1])
                            assert rational.compareTo(Rational.create(Formulas.pow2(intTemp[0]))) == +1
                                    && rational.compareTo(Rational.create(Formulas.pow2(intTemp[1]))) == -1;
                            // quick check
                            if (intTemp[1] <= -Formulas.DOUBLE_BIAS
                                    || rational.compareTo(Rational.DOUBLE_NORMAL_NEAREST_THRESHOLD) < 0) {
                                // the result is a subnormal number

                                if (intTemp[1] <= -Formulas.DOUBLE_BIAS) {
                                    d = Double.MIN_VALUE;
                                } else {
                                    if (rational.compareTo(Rational.DOUBLE_MAX_SUBNORMAL) >= 0) {
                                        d = Formulas.DOUBLE_MAX_SUBNORMAL;
                                    } else {
                                        assert rational.compareTo(Rational.DOUBLE_MIN_VALUE) == +1
                                                && rational.compareTo(Rational.DOUBLE_MAX_SUBNORMAL) == -1;

                                        /*
                                        pow2(-1074) < rational < pow2(-1022)- pow2(-1074) implies 1 < rational * pow2(1074) < pow2(52) - 1
                                        The fraction is floor(rational * pow2(1074)).
                                        */

                                        Rational shifted = rational.multiplyTwoPower(1074);
                                        BigInteger shiftedFloor = shifted.floor();
                                        assert shiftedFloor.compareTo(BigInteger.ONE) >= 0
                                                && shiftedFloor.compareTo(Formulas.bigintPow2(52).subtract(BigInteger.ONE)) == -1;
                                        assert shiftedFloor.bitLength() <= 52;

                                        // exact narrowing primitive conversion
                                        long bits = shiftedFloor.longValue();
                                        d = Double.longBitsToDouble(bits);

                                        // always exact for subnormal numbers
                                        double twiceD = d * 2;

                                        Rational thresholdAsRational;

                                        if (d < Formulas.pow2(-Formulas.DOUBLE_BIAS)) {
                                            assert twiceD < Formulas.DOUBLE_MAX_SUBNORMAL;
                                            // exact
                                            double threshold = twiceD + Double.MIN_VALUE;
                                            assert threshold <= Formulas.DOUBLE_MIN_NORMAL;
                                            thresholdAsRational = Rational.create(threshold);
                                        } else {
                                            assert twiceD >= Formulas.DOUBLE_MIN_NORMAL;
                                            thresholdAsRational =
                                                    Rational.create(twiceD).add(Rational.DOUBLE_MIN_VALUE);
                                        }

                                        int cmp = rational.compareTo(thresholdAsRational);
                                        if (cmp == +1 || (cmp == 0 && (bits & 1) == 1)) {
                                            d = Formulas.next(d);
                                        }
                                    }
                                }

                                assert Double.MIN_VALUE <= d && d <= Formulas.DOUBLE_MAX_SUBNORMAL;
                            } else {
                                // the result is a normal number

                                assert rational.compareTo(Rational.DOUBLE_NORMAL_NEAREST_THRESHOLD) >= 0
                                        && rational.compareTo(Rational.DOUBLE_MAX_NEAREST_THRESHOLD) == -1;

                                if (intTemp[1] <= 1 - Formulas.DOUBLE_BIAS) {
                                    d = Formulas.DOUBLE_MIN_NORMAL;
                                } else {
                                    if (rational.compareTo(Rational.DOUBLE_MAX_VALUE) >= 0) {
                                        d = Double.MAX_VALUE;
                                    } else {
                                        assert rational.compareTo(Rational.DOUBLE_MIN_NORMAL) == +1
                                                && rational.compareTo(Rational.DOUBLE_MAX_VALUE) == -1;

                                        // the exponent is intTemp[0]
                                        Rational shifted = rational.multiplyTwoPower(Formulas.N_DOUBLE - 1 - intTemp[0]);
                                        BigInteger shiftedFloor = shifted.floor();
                                        assert shiftedFloor.compareTo(Formulas.bigintPow2(Formulas.N_DOUBLE - 1)) >= 0
                                                && shiftedFloor.compareTo(Formulas.bigintPow2(Formulas.N_DOUBLE)) == -1;

                                        // exact narrowing primitive conversion
                                        long shiftedFloorBits = shiftedFloor.longValue();

                                        // exact conversion
                                        double shiftedFloorAsDouble = (double) shiftedFloorBits;

                                        // exact. Do not collapse the last to factors into a single one.
                                        d = shiftedFloorAsDouble / Formulas.pow2(Formulas.N_DOUBLE - 1)
                                                * Formulas.pow2(intTemp[0]);

                                        assert Formulas.DOUBLE_MIN_NORMAL <= d && d < Double.MAX_VALUE;

                                        double dNext = Formulas.next(d);

                                        // exact, actually 1.00..01*2^intTemp[0]
                                        double step = dNext - d;
                                        assert step == Formulas.pow2(intTemp[0] - Formulas.N_DOUBLE + 1);

                                        Rational dAsRational = Rational.create(d),
                                                stepAsRational = Rational.create(step);
                                        Rational thresholdAsRational = dAsRational.add(stepAsRational.multiplyTwoPower(-1));

                                        long bits = Double.doubleToLongBits(d);

                                        int cmp = rational.compareTo(thresholdAsRational);
                                        if (cmp == +1 || (cmp == 0 && (bits & 1) == 1)) {
                                            d = dNext;
                                        }
                                    }
                                }
                            }
                        }
                        assert Formulas.isFinite(d);
                    }
                }

                break;
            case 2:
                if (intTemp[1] <= Formulas.DOUBLE_MIN_TWO_EXPONENT) {
                    assert rational.compareTo(Rational.DOUBLE_MIN_VALUE) <= 0;
                    d = Double.MIN_VALUE;
                } else {
                    assert rational.compareTo(Rational.DOUBLE_MIN_VALUE) > 0;

                    // quick check for the intTemp[0] > Formulas.DOUBLE_MAX_TWO_EXPONENT condition
                    if (intTemp[0] > Formulas.DOUBLE_MAX_TWO_EXPONENT
                            || rational.compareTo(Rational.DOUBLE_MAX_VALUE) > 0) {
                        // rational > Double.MAX_VALUE
                        d = Double.POSITIVE_INFINITY;
                    } else {
                        // Double.MIN_VALUE < rational <= Double.MAX_VALUE
                        assert Formulas.DOUBLE_MIN_TWO_EXPONENT < intTemp[1]
                                && intTemp[1] <= Formulas.DOUBLE_MAX_TWO_EXPONENT + 1;
                        assert rational.compareTo(Rational.DOUBLE_MAX_VALUE) <= 0;

                        if (intTemp[0] == intTemp[1]) {
                            // exact double value
                            d = Formulas.pow2(intTemp[0]);
                        } else {
                            // pow2(intTemp[0]) < rational < pow2(intTemp[1])
                            assert rational.compareTo(Rational.create(Formulas.pow2(intTemp[0]))) == +1
                                    && rational.compareTo(Rational.create(Formulas.pow2(intTemp[1]))) == -1;
                            // fast check first
                            if (intTemp[1] < 1 - Formulas.DOUBLE_BIAS
                                    || (intTemp[1] == 1 - Formulas.DOUBLE_BIAS
                                    && rational.compareTo(Rational.DOUBLE_MAX_SUBNORMAL) <= 0)) {
                                // the result is a subnormal number > Double.MIN_VALUE
                                assert rational.compareTo(Rational.DOUBLE_MIN_NORMAL) == -1;
                                /*
                                pow2(-1074) < rational < pow2(-1022) implies 1 < rational * pow2(1074) < pow2(52)
                                The fraction is ceil(rational * pow2(1074)).
                                */

                                Rational shifted = rational.multiplyTwoPower(1074);
                                BigInteger shiftedCeil = shifted.ceil();
                                assert shiftedCeil.compareTo(BigInteger.ONE) >= 0
                                        && shiftedCeil.compareTo(Formulas.bigintPow2(52)) == -1;
                                assert shiftedCeil.bitLength() <= 52;

                                // exact narrowing primitive conversion
                                long bits = shiftedCeil.longValue();
                                d = Double.longBitsToDouble(bits);
                                assert Double.MIN_VALUE < d && d <= Formulas.DOUBLE_MAX_SUBNORMAL;
                            } else {
                                // Formulas.DOUBLE_MAX_SUBNORMAL < rational <= Double.DOUBLE_MAX_VALUE
                                // Formulas.DOUBLE_MIN_NORMAL <= result <= Double.MAX_VALUE
                                assert rational.compareTo(Rational.DOUBLE_MAX_SUBNORMAL) == +1;
                                // the exponent is intTemp[0] or result = pow2(intTemp[1])
                                Rational shifted = rational.multiplyTwoPower(Formulas.N_DOUBLE - 1 - intTemp[0]);
                                BigInteger shiftedCeil = shifted.ceil();
                                assert shiftedCeil.compareTo(Formulas.bigintPow2(Formulas.N_DOUBLE - 1)) >= 0
                                        && shiftedCeil.compareTo(Formulas.bigintPow2(Formulas.N_DOUBLE)) <= 0;

                                /*
                                These conversions are exact even for the case
                                shiftedCeil.equals(Formulas.bigintPow2(Formulas.N_DOUBLE)).
                                */
                                // exact narrowing primitive conversion
                                long shiftedCeilBits = shiftedCeil.longValue();

                                if (shiftedCeilBits < 1L << Formulas.N_DOUBLE
                                        || intTemp[0] < Formulas.DOUBLE_MAX_TWO_EXPONENT) {
                                    // exact conversion
                                    double shiftedCeilAsDouble = (double) shiftedCeilBits;

                                    // exact. Do not collapse the last to factors into a single one.
                                    d = shiftedCeilAsDouble / Formulas.pow2(Formulas.N_DOUBLE - 1)
                                            * Formulas.pow2(intTemp[0]);

                                    assert Formulas.DOUBLE_MIN_NORMAL <= d && d < Double.MAX_VALUE;
                                } else {
                                    assert shiftedCeilBits == 1L << 53 || intTemp[0] == Formulas.DOUBLE_MAX_TWO_EXPONENT;
                                    /*
                                    The only value with shiftedCeilBits == 1L << 53
                                    and intTemp[0] < Formulas.DOUBLE_MAX_TWO_EXPONENT is
                                    Double.MAX_VALUE.
                                    */
                                    d = Double.MAX_VALUE;
                                }
                            }
                        }
                    }
                    assert Formulas.isFinite(d);
                }

                break;
            default:
                throw new InternalError("direction = " + direction);
        }

        assert d >= 0;
        return d;
    }

    protected float computeFloatValue() {
        final float f;

        switch (r.signum()) {
            case -1:
                f = -positiveFloatValue(2 - directionIndex(), r.negate());
                break;
            case 0:
                f = +0.0F;
                break;
            case 1:
                f = positiveFloatValue(directionIndex(), r);
                break;
            default:
                throw new InternalError("r.signum() = " + r.signum());
        }

        return f;
    }

    private float positiveFloatValue(int direction, Rational rational) {
        assert rational.signum() == +1;

        rational.log2Interval(intTemp);

        float f;

        switch (direction) {
            case 0:
                if (intTemp[0] < Formulas.FLOAT_MIN_TWO_EXPONENT) {
                    assert intTemp[1] <= Formulas.FLOAT_MIN_TWO_EXPONENT;
                    assert rational.compareTo(Rational.FLOAT_MIN_VALUE) == -1;
                    f = +0.0F;
                } else {
                    assert rational.compareTo(Rational.FLOAT_MIN_VALUE) >= 0;

                    // quick check for the intTemp[0] > Formulas.FLOAT_MAX_TWO_EXPONENT condition
                    if (intTemp[0] > Formulas.FLOAT_MAX_TWO_EXPONENT
                            || rational.compareTo(Rational.FLOAT_MAX_VALUE) >= 0) {
                        // rational >= Float.MAX_VALUE
                        f = Float.MAX_VALUE;
                    } else {
                        // Float.MIN_VALUE <= rational < Float.MAX_VALUE
                        assert Formulas.FLOAT_MIN_TWO_EXPONENT <= intTemp[0]
                                && intTemp[1] <= Formulas.FLOAT_MAX_TWO_EXPONENT + 1;
                        assert rational.compareTo(Rational.FLOAT_MAX_VALUE) == -1;

                        if (intTemp[0] == intTemp[1]) {
                            // exact float value
                            assert Formulas.isExactFloat(Formulas.pow2(intTemp[0]));
                            f = (float)Formulas.pow2(intTemp[0]);
                        } else {
                            assert Formulas.isExactFloat(Formulas.pow2(intTemp[0]));
                            assert Formulas.isExactFloat(Formulas.pow2(intTemp[1]));

                            // pow2(intTemp[0]) < rational < pow2(intTemp[1])
                            assert rational.compareTo(Rational.create(Formulas.pow2(intTemp[0]))) == +1
                                    && rational.compareTo(Rational.create(Formulas.pow2(intTemp[1]))) == -1;
                            if (intTemp[1] <= 1 - Formulas.FLOAT_BIAS) {
                                // the result is a subnormal number > Float.MIN_VALUE
                                assert rational.compareTo(Rational.FLOAT_MIN_NORMAL) == -1;
                                /*
                                pow2(-149) < rational < pow2(-126) implies 1 < rational * pow2(149) < pow2(23)
                                The fraction is floor(rational * pow2(149)).
                                */

                                Rational shifted = rational.multiplyTwoPower(149);
                                BigInteger shiftedFloor = shifted.floor();
                                assert shiftedFloor.compareTo(BigInteger.ONE) >= 0
                                        && shiftedFloor.compareTo(Formulas.bigintPow2(23)) == -1;
                                assert shiftedFloor.bitLength() <= 23;

                                // exact narrowing primitive conversion
                                int bits = shiftedFloor.intValue();
                                f = Float.intBitsToFloat(bits);
                                assert Float.MIN_VALUE <= f && f <= Formulas.FLOAT_MAX_SUBNORMAL;
                            } else {
                                // Formulas.FLOAT_MIN_NORMAL < rational < Float.MAX_VALUE
                                // Formulas.FLOAT_MIN_NORMAL <= result < Float.MAX_VALUE
                                assert rational.compareTo(Rational.FLOAT_MIN_NORMAL) == +1;
                                // the exponent is intTemp[0]
                                Rational shifted = rational.multiplyTwoPower(Formulas.N_FLOAT - 1 - intTemp[0]);
                                BigInteger shiftedFloor = shifted.floor();
                                assert shiftedFloor.compareTo(Formulas.bigintPow2(Formulas.N_FLOAT - 1)) >= 0
                                        && shiftedFloor.compareTo(Formulas.bigintPow2(Formulas.N_FLOAT)) == -1;

                                // exact narrowing primitive conversion
                                int shiftedFloorBits = shiftedFloor.intValue();

                                // exact conversion
                                float shiftedFloorAsFloat = (float) shiftedFloorBits;

                                // exact. Do not collapse the last to factors into a single one.
                                f = shiftedFloorAsFloat / (float)Formulas.pow2(Formulas.N_FLOAT - 1)
                                        * (float)Formulas.pow2(intTemp[0]);

                                assert Formulas.FLOAT_MIN_NORMAL <= f && f < Float.MAX_VALUE;
                            }
                        }
                    }
                    assert Formulas.isFinite(f);
                }

                break;
            case 1:
                if (intTemp[1] <= Formulas.FLOAT_MIN_TWO_EXPONENT - 1) {
                    assert rational.compareTo(Rational.FLOAT_HALF_MIN_VALUE) <= 0;
                    f = +0.0F;
                } else {
                    assert rational.compareTo(Rational.FLOAT_HALF_MIN_VALUE) > 0;

                    // quick check for the intTemp[0] > Formulas.FLOAT_MAX_TWO_EXPONENT condition
                    if (intTemp[0] > Formulas.FLOAT_MAX_TWO_EXPONENT
                            || rational.compareTo(Rational.FLOAT_MAX_NEAREST_THRESHOLD) >= 0) {
                        // rational >= Rational.FLOAT_MAX_NEAREST_THRESHOLD
                        f = Float.POSITIVE_INFINITY;
                    } else {
                        // the result is a finite number
                        // Rational.FLOAT_HALF_MIN_VALUE < rational < Rational.FLOAT_MAX_NEAREST_THRESHOLD
                        assert Formulas.FLOAT_MIN_TWO_EXPONENT - 1 <= intTemp[0]
                                && intTemp[1] <= Formulas.FLOAT_MAX_TWO_EXPONENT + 1;
                        assert rational.compareTo(Rational.FLOAT_MAX_NEAREST_THRESHOLD) == -1;

                        if (intTemp[0] == intTemp[1]) {
                            // exact float value
                            assert Formulas.isExactFloat(Formulas.pow2(intTemp[0]));
                            f = (float)Formulas.pow2(intTemp[0]);
                        } else {
                            assert Formulas.isExactFloat(Formulas.pow2(intTemp[0]));
                            assert Formulas.isExactFloat(Formulas.pow2(intTemp[1]));

                            // pow2(intTemp[0]) < rational < pow2(intTemp[1])
                            assert rational.compareTo(Rational.create(Formulas.pow2(intTemp[0]))) == +1
                                    && rational.compareTo(Rational.create(Formulas.pow2(intTemp[1]))) == -1;
                            // quick check
                            if (intTemp[1] <= -Formulas.FLOAT_BIAS
                                    || rational.compareTo(Rational.FLOAT_NORMAL_NEAREST_THRESHOLD) < 0) {
                                // the result is a subnormal number

                                if (intTemp[1] <= -Formulas.FLOAT_BIAS) {
                                    f = Float.MIN_VALUE;
                                } else {
                                    if (rational.compareTo(Rational.FLOAT_MAX_SUBNORMAL) >= 0) {
                                        f = Formulas.FLOAT_MAX_SUBNORMAL;
                                    } else {
                                        assert rational.compareTo(Rational.FLOAT_MIN_VALUE) == +1
                                                && rational.compareTo(Rational.FLOAT_MAX_SUBNORMAL) == -1;

                                        /*
                                        pow2(-149) < rational < pow2(-126)- pow2(-149) implies 1 < rational * pow2(149) < pow2(23) - 1
                                        The fraction is floor(rational * pow2(149)).
                                        */

                                        Rational shifted = rational.multiplyTwoPower(149);
                                        BigInteger shiftedFloor = shifted.floor();
                                        assert shiftedFloor.compareTo(BigInteger.ONE) >= 0
                                                && shiftedFloor.compareTo(Formulas.bigintPow2(23).subtract(BigInteger.ONE)) == -1;
                                        assert shiftedFloor.bitLength() <= 23;

                                        // exact narrowing primitive conversion
                                        int bits = shiftedFloor.intValue();
                                        f = Float.intBitsToFloat(bits);

                                        // always exact for subnormal numbers
                                        float twiceD = f * 2;

                                        Rational thresholdAsRational;

                                        if (f < Formulas.pow2(-Formulas.FLOAT_BIAS)) {
                                            assert twiceD < Formulas.FLOAT_MAX_SUBNORMAL;
                                            // exact
                                            float threshold = twiceD + Float.MIN_VALUE;
                                            assert threshold <= Formulas.FLOAT_MIN_NORMAL;
                                            thresholdAsRational = Rational.create(threshold);
                                        } else {
                                            assert twiceD >= Formulas.FLOAT_MIN_NORMAL;
                                            thresholdAsRational =
                                                    Rational.create(twiceD).add(Rational.FLOAT_MIN_VALUE);
                                        }

                                        int cmp = rational.compareTo(thresholdAsRational);
                                        if (cmp == +1 || (cmp == 0 && (bits & 1) == 1)) {
                                            f = Formulas.next(f);
                                        }
                                    }
                                }

                                assert Float.MIN_VALUE <= f && f <= Formulas.FLOAT_MAX_SUBNORMAL;
                            } else {
                                // the result is a normal number

                                assert rational.compareTo(Rational.FLOAT_NORMAL_NEAREST_THRESHOLD) >= 0
                                        && rational.compareTo(Rational.FLOAT_MAX_NEAREST_THRESHOLD) == -1;

                                if (intTemp[1] <= 1 - Formulas.FLOAT_BIAS) {
                                    f = Formulas.FLOAT_MIN_NORMAL;
                                } else {
                                    if (rational.compareTo(Rational.FLOAT_MAX_VALUE) >= 0) {
                                        f = Float.MAX_VALUE;
                                    } else {
                                        assert rational.compareTo(Rational.FLOAT_MIN_NORMAL) == +1
                                                && rational.compareTo(Rational.FLOAT_MAX_VALUE) == -1;

                                        // the exponent is intTemp[0]
                                        Rational shifted = rational.multiplyTwoPower(Formulas.N_FLOAT - 1 - intTemp[0]);
                                        BigInteger shiftedFloor = shifted.floor();
                                        assert shiftedFloor.compareTo(Formulas.bigintPow2(Formulas.N_FLOAT - 1)) >= 0
                                                && shiftedFloor.compareTo(Formulas.bigintPow2(Formulas.N_FLOAT)) == -1;

                                        // exact narrowing primitive conversion
                                        int shiftedFloorBits = shiftedFloor.intValue();

                                        // exact conversion
                                        float shiftedFloorAsFloat = (float) shiftedFloorBits;

                                        assert Formulas.isExactFloat(Formulas.pow2(intTemp[0]));

                                        f = shiftedFloorAsFloat / (float)Formulas.pow2(Formulas.N_FLOAT - 1)
                                                * (float)Formulas.pow2(intTemp[0]);

                                        assert Formulas.FLOAT_MIN_NORMAL <= f && f < Float.MAX_VALUE;

                                        float dNext = Formulas.next(f);

                                        // exact, actually 1.00..01*2^intTemp[0]
                                        float step = dNext - f;
                                        assert step == Formulas.pow2(intTemp[0] - Formulas.N_FLOAT + 1);

                                        Rational dAsRational = Rational.create(f),
                                                stepAsRational = Rational.create(step);
                                        Rational thresholdAsRational = dAsRational.add(stepAsRational.multiplyTwoPower(-1));

                                        int bits = Float.floatToIntBits(f);

                                        int cmp = rational.compareTo(thresholdAsRational);
                                        if (cmp == +1 || (cmp == 0 && (bits & 1) == 1)) {
                                            f = dNext;
                                        }
                                    }
                                }
                            }
                        }
                        assert Formulas.isFinite(f);
                    }
                }

                break;
            case 2:
                if (intTemp[1] <= Formulas.FLOAT_MIN_TWO_EXPONENT) {
                    assert rational.compareTo(Rational.FLOAT_MIN_VALUE) <= 0;
                    f = Float.MIN_VALUE;
                } else {
                    assert rational.compareTo(Rational.FLOAT_MIN_VALUE) > 0;

                    // quick check for the intTemp[0] > Formulas.FLOAT_MAX_TWO_EXPONENT condition
                    if (intTemp[0] > Formulas.FLOAT_MAX_TWO_EXPONENT
                            || rational.compareTo(Rational.FLOAT_MAX_VALUE) > 0) {
                        // rational > Float.MAX_VALUE
                        f = Float.POSITIVE_INFINITY;
                    } else {
                        // Float.MIN_VALUE < rational <= Float.MAX_VALUE
                        assert Formulas.FLOAT_MIN_TWO_EXPONENT < intTemp[1]
                                && intTemp[1] <= Formulas.FLOAT_MAX_TWO_EXPONENT + 1;
                        assert rational.compareTo(Rational.FLOAT_MAX_VALUE) <= 0;

                        if (intTemp[0] == intTemp[1]) {
                            // exact float value
                            assert Formulas.isExactFloat(Formulas.pow2(intTemp[0]));
                            f = (float)Formulas.pow2(intTemp[0]);
                        } else {
                            assert Formulas.isExactFloat(Formulas.pow2(intTemp[0]));
                            assert Formulas.isExactFloat(Formulas.pow2(intTemp[1]));

                            // pow2(intTemp[0]) < rational < pow2(intTemp[1])
                            assert rational.compareTo(Rational.create(Formulas.pow2(intTemp[0]))) == +1
                                    && rational.compareTo(Rational.create(Formulas.pow2(intTemp[1]))) == -1;
                            // fast check first
                            if (intTemp[1] < 1 - Formulas.FLOAT_BIAS
                                    || (intTemp[1] == 1 - Formulas.FLOAT_BIAS
                                    && rational.compareTo(Rational.FLOAT_MAX_SUBNORMAL) <= 0)) {
                                // the result is a subnormal number > Float.MIN_VALUE
                                assert rational.compareTo(Rational.FLOAT_MIN_NORMAL) == -1;
                                /*
                                pow2(-149) < rational < pow2(-126) implies 1 < rational * pow2(149) < pow2(23)
                                The fraction is ceil(rational * pow2(149)).
                                */

                                Rational shifted = rational.multiplyTwoPower(149);
                                BigInteger shiftedCeil = shifted.ceil();
                                assert shiftedCeil.compareTo(BigInteger.ONE) >= 0
                                        && shiftedCeil.compareTo(Formulas.bigintPow2(23)) == -1;
                                assert shiftedCeil.bitLength() <= 23;

                                // exact narrowing primitive conversion
                                int bits = shiftedCeil.intValue();
                                f = Float.intBitsToFloat(bits);
                                assert Float.MIN_VALUE < f && f <= Formulas.FLOAT_MAX_SUBNORMAL;
                            } else {
                                // Formulas.FLOAT_MAX_SUBNORMAL < rational <= Float.FLOAT_MAX_VALUE
                                // Formulas.FLOAT_MIN_NORMAL <= result <= Float.MAX_VALUE
                                assert rational.compareTo(Rational.FLOAT_MAX_SUBNORMAL) == +1;
                                // the exponent is intTemp[0] or result = pow2(intTemp[1])
                                Rational shifted = rational.multiplyTwoPower(Formulas.N_FLOAT - 1 - intTemp[0]);
                                BigInteger shiftedCeil = shifted.ceil();
                                assert shiftedCeil.compareTo(Formulas.bigintPow2(Formulas.N_FLOAT - 1)) >= 0
                                        && shiftedCeil.compareTo(Formulas.bigintPow2(Formulas.N_FLOAT)) <= 0;

                                /*
                                These conversions are exact even for the case
                                shiftedCeil.equals(Formulas.bigintPow2(Formulas.N_FLOAT)).
                                */
                                // exact narrowing primitive conversion
                                int shiftedCeilBits = shiftedCeil.intValue();

                                if (shiftedCeilBits < 1 << Formulas.N_FLOAT
                                        || intTemp[0] < Formulas.FLOAT_MAX_TWO_EXPONENT) {
                                    // exact conversion
                                    float shiftedCeilAsFloat = (float) shiftedCeilBits;

                                    assert Formulas.isExactFloat(Formulas.pow2(intTemp[0]));

                                    // exact. Do not collapse the last to factors into a single one.
                                    f = shiftedCeilAsFloat / (float)Formulas.pow2(Formulas.N_FLOAT - 1)
                                            * (float)Formulas.pow2(intTemp[0]);

                                    assert Formulas.FLOAT_MIN_NORMAL <= f && f < Float.MAX_VALUE;
                                } else {
                                    assert shiftedCeilBits == 1 << 24 || intTemp[0] == Formulas.FLOAT_MAX_TWO_EXPONENT;
                                    /*
                                    The only value with shiftedCeilBits == 1 << 24
                                    and intTemp[0] < Formulas.FLOAT_MAX_TWO_EXPONENT is
                                    Float.MAX_VALUE.
                                    */
                                    f = Float.MAX_VALUE;
                                }
                            }
                        }
                    }
                    assert Formulas.isFinite(f);
                }

                break;
            default:
                throw new InternalError("direction = " + direction);
        }

        assert f >= 0;
        return f;
    }

    /**
     * Cached but not precomputed.
     */
    protected BigInteger computeBigIntegerValue() {
        BigInteger v;

        switch (directionIndex()) {
            case 0:
                v = r.floor();
                break;
            case 1:
                if (r.numeratorModDenominator().signum() == 0) {
                    v = r.floor();
                } else {
                    // floor < v < ceil
                    BigInteger twoMid = r.floor().shiftLeft(1).add(BigInteger.ONE);
                    Rational twoMidRational = Rational.create(twoMid, BigInteger.ONE);
                    Rational twoThis = r.multiplyTwoPower(1);

                    int cmp = twoThis.compareTo(twoMidRational);

                    switch (cmp) {
                        case -1:
                            v = r.floor();
                            break;
                        case 0:
                            if (r.floor().testBit(0)) {
                                // floor is odd
                                v = r.ceil();
                            } else {
                                // floor is even
                                v = r.floor();
                            }
                            break;
                        case 1:
                            v = r.ceil();
                            break;
                        default:
                            throw new InternalError("cmp = " + cmp);
                    }
                }
                break;
            case 2:
                v = r.ceil();
                break;
            default:
                throw new InternalError();
        }

        return v;
    }

}
