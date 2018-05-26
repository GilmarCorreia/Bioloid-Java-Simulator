package precisefloating.directedrounding;

import precisefloating.Formulas;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * When a rounded value is computed, this class makes its best effort to deduce the value of this
 * number as rounded in other directions or as other types.
 * BigIntegers or DoublePrecisionNo objects are not used in this process because this class must be
 * very lightweight. The only objects that are created by this class or the transient closure of all
 * the called methods are two temporary two-size arrays, one of type float[] and the other double[].
 * Superclass for RoundNumber and RoundContinuedFraction.
 *
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public abstract class RoundNumber extends RoundNumberBase {

    public final float[] getFloatTemp() {
        if (floatTemp == null) {
            floatTemp = new float[2];
        }

        return floatTemp;
    }

    private float[] floatTemp;

    public final double[] getDoubleTemp() {
        if (doubleTemp == null) {
            doubleTemp = new double[2];
        }

        return doubleTemp;
    }

    private double[] doubleTemp;

    // Long.MAX_VALUE rounded to positive infinity
    private static final double doublePow2_63 = Formulas.pow2(63);
    private static final double doubleNextPow2_63 = doublePow2_63 + 2048;
    private static final double doublePreviousMinusPow2_63 = -doublePow2_63 - 2048;

    private static final Logger log = Logger.getLogger(RoundNumber.class.getName());

    static {
        /*
        Actually equal to Long.MAX_VALUE rounded to nearest because Long.MAX_VALUE is closer to
        pow2(63) than to previous(pow2(63)) == (2 - pow2(-52)) * pow2(62).
        */
        assert doublePow2_63 == Long.MAX_VALUE && doublePow2_63 == -(double) Long.MIN_VALUE;
        assert Formulas.next(doublePow2_63) == doubleNextPow2_63;
        assert Formulas.previous(-doublePow2_63) == doublePreviousMinusPow2_63;
    }

    protected final void computed(int idx, double d) {
        boolean isAlreadyComputed = isDoubleComputed(idx);

        super.computed(idx, d);

        // avoids infinite recursion and duplicate computation
        if (!isAlreadyComputed) {
            if (log.isLoggable(Level.FINEST)) {
                log.finest("idx = " + idx + ", d = " + d);
            }

            // update other values if enough finestrmation is available
            precomputeDoubles(idx, d);
            precomputeFloats(idx, d);
            precomputeLongs(idx, d);
            precomputeInts(idx, d);
            precomputeShorts(idx, d);
            precomputeBytes(idx, d);

            assert !isDoubleComputed(0) || !isDoubleComputed(1) || !isDoubleComputed(2) ||
                    (isFloatComputed(0) && isFloatComputed(1) && isFloatComputed(2))
                    : "when all the double values are computed, all the float values "
                    + "should also have been computed";
        }
    }

    private void precomputeDoubles(int idx, double d) {
        switch (idx) {
            case 0:
                assert !getComputedDoubles()[1] || !getComputedDoubles()[2]
                        || Double.doubleToLongBits(doubleValues[1]) == Double.doubleToLongBits(doubleValues[2])
                        : "should have been computed " + doubleValues[1];
                assert !getComputedDoubles()[1] || doubleValues[1] > Double.NEGATIVE_INFINITY
                        : "should have been computed " + Double.NEGATIVE_INFINITY;

                if (getComputedDoubles()[1] && Formulas.signedZerosLess(d, doubleValues[1])) {
                    computed(2, doubleValues[1]);
                } else {
                    if (d == Double.NEGATIVE_INFINITY) {
                        computed(2, -Double.MAX_VALUE);
                    } else {
                        if (getComputedDoubles()[2] && Double.doubleToLongBits(d) ==
                                Double.doubleToLongBits(doubleValues[2])) {
                            computed(1, d);
                        }
                    }
                }

                break;
            case 1:
                assert !getComputedDoubles()[0] || !getComputedDoubles()[2]
                        || Double.doubleToLongBits(d) != Double.doubleToLongBits(doubleValues[2])
                        : "should have been computed " + d;

                if (getComputedDoubles()[0] && Formulas.signedZerosLess(doubleValues[0], d)) {
                    computed(2, d);
                } else {
                    if (d == Double.NEGATIVE_INFINITY) {
                        computed(0, Double.NEGATIVE_INFINITY);
                    } else {
                        if (d == Double.POSITIVE_INFINITY) {
                            computed(2, Double.POSITIVE_INFINITY);
                        }
                    }
                }

                break;
            case 2:
                assert !getComputedDoubles()[0] || !getComputedDoubles()[1]
                        || Double.doubleToLongBits(doubleValues[0]) ==
                        Double.doubleToLongBits(doubleValues[1])
                        : "should have been computed " + doubleValues[1];
                assert !getComputedDoubles()[1] || doubleValues[1] < Double.POSITIVE_INFINITY
                        : "should have been computed " + Double.POSITIVE_INFINITY;

                if (getComputedDoubles()[1] && Formulas.signedZerosLess(doubleValues[1], d)) {
                    computed(0, doubleValues[1]);
                } else {
                    if (d == Double.POSITIVE_INFINITY) {
                        computed(0, Double.MAX_VALUE);
                    } else {
                        if (getComputedDoubles()[0] && Double.doubleToLongBits(doubleValues[0]) ==
                                Double.doubleToLongBits(d)) {
                            computed(1, d);
                        }
                    }
                }

                break;
            default:
                throw new InternalError();
        }
    }

    private void precomputeFloats(int idx, double d) {
        Formulas.floatInterval(d, getFloatTemp());

        float low = floatTemp[0], high = floatTemp[1];

        if (!Formulas.isFinite(low) || !Formulas.isFinite(high)) {
            if (high == Float.POSITIVE_INFINITY) {
                computed(0, Float.MAX_VALUE);
                computed(2, Float.POSITIVE_INFINITY);

                if (low == Float.POSITIVE_INFINITY) {
                    assert d == Double.POSITIVE_INFINITY;
                    /*
                    As (float)Double.MAX_VALUE = Float.POSITIVE_INFINITY,
                    value > Double.MAX_VALUE implies the value is Float.POSITIVE_INFINITY
                    in round-to-nearest mode.
                    */
                    computed(1, Float.POSITIVE_INFINITY);
                } else {
                    assert low == Float.MAX_VALUE;
                    assert Float.MAX_VALUE < d && d < Double.POSITIVE_INFINITY;

                    if (d < Formulas.SINGLE_SMALLEST_NEAREST_POSITIVE_INFINITY) {
                        computed(1, Float.MAX_VALUE);
                    } else {
                        if (d > Formulas.SINGLE_SMALLEST_NEAREST_POSITIVE_INFINITY) {
                            computed(1, Float.POSITIVE_INFINITY);
                        } else {
                            assert d == Formulas.SINGLE_SMALLEST_NEAREST_POSITIVE_INFINITY;

                            if (idx == 0) {
                                // Formulas.SINGLE_SMALLEST_NEAREST_POSITIVE_INFINITY <= value < Float.POSITIVE_INFINITY
                                computed(1, Float.POSITIVE_INFINITY);
                            }
                        }
                    }
                }
            } else {
                if (low == Float.NEGATIVE_INFINITY) {
                    computed(0, Float.NEGATIVE_INFINITY);
                    computed(2, -Float.MAX_VALUE);

                    if (high == Float.NEGATIVE_INFINITY) {
                        assert d == Double.NEGATIVE_INFINITY;
                        /*
                        As (float)-Double.MAX_VALUE = Float.NEGATIVE_INFINITY,
                        value < -Double.MAX_VALUE implies the value is Float.NEGATIVE_INFINITY
                        in round-to-nearest mode.
                        */
                        computed(1, Float.NEGATIVE_INFINITY);
                    } else {
                        assert high == -Float.MAX_VALUE;
                        assert Double.NEGATIVE_INFINITY < d && d < -Float.MAX_VALUE;

                        if (d < Formulas.SINGLE_LARGEST_NEAREST_NEGATIVE_INFINITY) {
                            computed(1, Float.NEGATIVE_INFINITY);
                        } else {
                            if (d > Formulas.SINGLE_LARGEST_NEAREST_NEGATIVE_INFINITY) {
                                computed(1, -Float.MAX_VALUE);
                            } else {
                                assert d == Formulas.SINGLE_LARGEST_NEAREST_NEGATIVE_INFINITY;

                                if (idx == 2) {
                                    // Float.NEGATIVE_INFINITY < value <= Formulas.SINGLE_LARGEST_NEAREST_NEGATIVE_INFINITY
                                    computed(1, Float.NEGATIVE_INFINITY);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (low == high) {
                // can't be (-0.0F, +0.0F)
                assert Float.floatToIntBits(low) == Float.floatToIntBits(high);

                // d is exactly representable as a floating point number
                float f = (float) d;
                assert f == low && f == high && f == d;

                computed(idx, f);
            } else {
                assert low < high;

                // always exact in double precision
                double mid = ((double) low + (double) high) / 2;
                assert Formulas.isFinite(mid);

                computed(0, low);
                computed(2, high);

                if (d < mid) {
                    computed(1, low);
                } else {
                    if (d > mid) {
                        computed(1, high);
                    } else {
                        assert d == mid;

                        switch (idx) {
                            case 0:
                                // mid <= value < high
                                if (Formulas.isEven(high)) {
                                    computed(1, high);
                                }

                                break;
                            case 1:
                                // no information supplied
                                break;
                            case 2:
                                // low < value <= mid
                                if (Formulas.isEven(low)) {
                                    computed(1, low);
                                }

                                break;
                            default:
                                throw new InternalError("invalid directionIndex " + idx);
                        }
                    }
                }
            }
        }
    }

    private void precomputeLongs(int idx, double d) {
        if (d > doublePow2_63) {
            assert d >= doubleNextPow2_63;
            computed(0, Long.MAX_VALUE);
            computed(1, Long.MAX_VALUE);
            computed(2, Long.MAX_VALUE);
        } else {
            if (d < -doublePow2_63) {
                assert d <= doublePreviousMinusPow2_63;
                computed(0, Long.MIN_VALUE);
                computed(1, Long.MIN_VALUE);
                computed(2, Long.MIN_VALUE);
            } else {
                assert -doublePow2_63 <= d && d <= doublePow2_63;

                double dFloor = Math.floor(d);
                long dFloorLong = (long) dFloor;

                switch (idx) {
                    case 0:
                        if (-Formulas.DOUBLE_MAX_EXACT_LONG <= d && d < Formulas.DOUBLE_MAX_EXACT_LONG) {
                            assert dFloor == dFloorLong;
                            computed(0, dFloorLong);
                        } else {
                            if (d == doublePow2_63) {
                                // value >= pow2(63) > Long.MAX_VALUE
                                assert dFloor > dFloorLong && dFloorLong == Long.MAX_VALUE;
                                computed(0, Long.MAX_VALUE);
                                computed(1, Long.MAX_VALUE);
                                computed(2, Long.MAX_VALUE);
                            }
                        }

                        break;
                    case 1:
                        if (-Formulas.DOUBLE_MAX_EXACT_LONG < d && d < Formulas.DOUBLE_MAX_EXACT_LONG) {
                            assert dFloor == dFloorLong;

                            if (d == dFloor) {
                                computed(1, dFloorLong);
                            } else {
                                assert -1L << 52 < d && d < 1L << 52;
                                assert Math.ceil(d) == dFloor + 1;

                                computed(0, dFloorLong);
                                computed(2, dFloorLong + 1);

                                if (d < dFloor + 0.5) {
                                    computed(1, dFloorLong);
                                } else {
                                    if (d > dFloor + 0.5) {
                                        computed(1, dFloorLong + 1);
                                    } else {
                                        // in the middle. Nothing to do.
                                        assert d == dFloor + 0.5;
                                    }
                                }
                            }
                        }

                        break;
                    case 2:
                        if (-Formulas.DOUBLE_MAX_EXACT_LONG < d && d <= Formulas.DOUBLE_MAX_EXACT_LONG) {
                            double dCeil = Math.ceil(d);
                            long dCeilLong = (long) dCeil;
                            assert dCeil == dCeilLong;
                            computed(2, dCeilLong);
                        } else {
                            if (d == -doublePow2_63) {
                                // value <= Long.MIN_VALUE
                                assert dFloor == dFloorLong;
                                computed(0, Long.MIN_VALUE);
                                computed(1, Long.MIN_VALUE);
                                computed(2, Long.MIN_VALUE);
                            }
                        }

                        break;
                    default:
                        throw new InternalError("invalid directionIndex " + idx);
                }
            }
        }
    }

    private void precomputeInts(int idx, double d) {
        if (d > Integer.MAX_VALUE) {
            computed(0, Integer.MAX_VALUE);
            computed(1, Integer.MAX_VALUE);
            computed(2, Integer.MAX_VALUE);
        } else {
            if (d < Integer.MIN_VALUE) {
                computed(0, Integer.MIN_VALUE);
                computed(1, Integer.MIN_VALUE);
                computed(2, Integer.MIN_VALUE);
            } else {
                assert Integer.MIN_VALUE <= d && d <= Integer.MAX_VALUE;

                double dFloor = Math.floor(d);
                int dFloorInteger = (int) dFloor;
                assert dFloor == dFloorInteger;

                switch (idx) {
                    case 0:
                        if (d == Integer.MAX_VALUE) {
                            // value >= d = Integer.MAX_VALUE
                            computed(0, Integer.MAX_VALUE);
                            computed(1, Integer.MAX_VALUE);
                            computed(2, Integer.MAX_VALUE);
                        } else {
                            assert dFloorInteger < Integer.MAX_VALUE;
                            computed(0, dFloorInteger);

                            if (d == dFloor) {
                                computed(1, dFloorInteger);
                            } else {
                                assert d > dFloor;
                                computed(2, dFloorInteger + 1);

                                // dFloorInteger + 0.5 is exact
                                double mid = dFloorInteger + 0.5;
                                if (d < mid) {
                                    computed(1, dFloorInteger);
                                } else {
                                    if (d > mid || (dFloorInteger & 1) == 1) {
                                        computed(1, dFloorInteger + 1);
                                    }
                                }
                            }
                        }

                        break;
                    case 1:
                        if (d == dFloor) {
                            computed(1, dFloorInteger);
                        } else {
                            assert Integer.MIN_VALUE < d && d < Integer.MAX_VALUE;
                            assert dFloorInteger < Integer.MAX_VALUE;

                            computed(0, dFloorInteger);
                            computed(2, dFloorInteger + 1);

                            if (d < dFloor + 0.5) {
                                computed(1, dFloorInteger);
                            } else {
                                if (d > dFloor + 0.5) {
                                    computed(1, dFloorInteger + 1);
                                } else {
                                    // in the middle. Nothing to do.
                                    assert d == dFloor + 0.5;
                                }
                            }
                        }

                        break;
                    case 2:
                        if (d == Integer.MIN_VALUE) {
                            // value <= d = Integer.MIN_VALUE
                            computed(0, Integer.MIN_VALUE);
                            computed(1, Integer.MIN_VALUE);
                            computed(2, Integer.MIN_VALUE);
                        } else {
                            assert Integer.MIN_VALUE < d && d <= Integer.MAX_VALUE;
                            assert dFloor == dFloorInteger;

                            if (d == Integer.MAX_VALUE) {
                                computed(1, Integer.MAX_VALUE);
                                computed(2, Integer.MAX_VALUE);
                            } else {
                                assert Integer.MIN_VALUE < d && d < Integer.MAX_VALUE;
                                assert dFloorInteger < Integer.MAX_VALUE;

                                if (d == dFloor) {
                                    computed(1, dFloorInteger);
                                    computed(2, dFloorInteger);
                                } else {
                                    assert d > dFloor;
                                    computed(0, dFloorInteger);
                                    computed(2, dFloorInteger + 1);

                                    // dFloorInteger + 0.5 is exact
                                    double mid = dFloorInteger + 0.5;
                                    if (d < mid || (d == mid && (dFloorInteger & 1) == 0)) {
                                        computed(1, dFloorInteger);
                                    } else {
                                        if (d > mid) {
                                            computed(1, dFloorInteger + 1);
                                        }
                                    }
                                }
                            }
                        }

                        break;
                    default:
                        throw new InternalError("invalid directionIndex " + idx);
                }
            }
        }
    }

    private void precomputeShorts(int idx, double d) {
        if (d > Short.MAX_VALUE) {
            computed(0, Short.MAX_VALUE);
            computed(1, Short.MAX_VALUE);
            computed(2, Short.MAX_VALUE);
        } else {
            if (d < Short.MIN_VALUE) {
                computed(0, Short.MIN_VALUE);
                computed(1, Short.MIN_VALUE);
                computed(2, Short.MIN_VALUE);
            } else {
                assert Short.MIN_VALUE <= d && d <= Short.MAX_VALUE;

                double dFloor = Math.floor(d);
                short dFloorShort = (short) dFloor;
                assert dFloor == dFloorShort;

                switch (idx) {
                    case 0:
                        if (d == Short.MAX_VALUE) {
                            // value >= d = Short.MAX_VALUE
                            computed(0, Short.MAX_VALUE);
                            computed(1, Short.MAX_VALUE);
                            computed(2, Short.MAX_VALUE);
                        } else {
                            assert dFloorShort < Short.MAX_VALUE;
                            computed(0, dFloorShort);

                            if (d == dFloor) {
                                computed(1, dFloorShort);
                            } else {
                                assert d > dFloor;
                                computed(2, (short) (dFloorShort + 1));

                                // dFloorShort + 0.5 is exact
                                double mid = dFloorShort + 0.5;
                                if (d < mid) {
                                    computed(1, dFloorShort);
                                } else {
                                    if (d > mid || (dFloorShort & 1) == 1) {
                                        computed(1, (short) (dFloorShort + 1));
                                    }
                                }
                            }
                        }

                        break;
                    case 1:
                        if (d == dFloor) {
                            computed(1, dFloorShort);
                        } else {
                            assert Short.MIN_VALUE < d && d < Short.MAX_VALUE;
                            assert dFloorShort < Short.MAX_VALUE;

                            computed(0, dFloorShort);
                            computed(2, (short) (dFloorShort + 1));

                            if (d < dFloor + 0.5) {
                                computed(1, dFloorShort);
                            } else {
                                if (d > dFloor + 0.5) {
                                    computed(1, (short) (dFloorShort + 1));
                                } else {
                                    // in the middle. Nothing to do.
                                    assert d == dFloor + 0.5;
                                }
                            }
                        }

                        break;
                    case 2:
                        if (d == Short.MIN_VALUE) {
                            // value <= d = Short.MIN_VALUE
                            computed(0, Short.MIN_VALUE);
                            computed(1, Short.MIN_VALUE);
                            computed(2, Short.MIN_VALUE);
                        } else {
                            assert Short.MIN_VALUE < d && d <= Short.MAX_VALUE;
                            assert dFloor == dFloorShort;

                            if (d == Short.MAX_VALUE) {
                                computed(1, Short.MAX_VALUE);
                                computed(2, Short.MAX_VALUE);
                            } else {
                                assert Short.MIN_VALUE < d && d < Short.MAX_VALUE;
                                assert dFloorShort < Short.MAX_VALUE;

                                if (d == dFloor) {
                                    computed(1, dFloorShort);
                                    computed(2, dFloorShort);
                                } else {
                                    assert d > dFloor;
                                    computed(0, dFloorShort);
                                    computed(2, (short) (dFloorShort + 1));

                                    // dFloorShort + 0.5 is exact
                                    double mid = dFloorShort + 0.5;
                                    if (d < mid || (d == mid && (dFloorShort & 1) == 0)) {
                                        computed(1, dFloorShort);
                                    } else {
                                        if (d > mid) {
                                            computed(1, (short) (dFloorShort + 1));
                                        }
                                    }
                                }
                            }
                        }

                        break;
                    default:
                        throw new InternalError("invalid directionIndex " + idx);
                }
            }
        }
    }

    private void precomputeBytes(int idx, double d) {
        if (d > Byte.MAX_VALUE) {
            computed(0, Byte.MAX_VALUE);
            computed(1, Byte.MAX_VALUE);
            computed(2, Byte.MAX_VALUE);
        } else {
            if (d < Byte.MIN_VALUE) {
                computed(0, Byte.MIN_VALUE);
                computed(1, Byte.MIN_VALUE);
                computed(2, Byte.MIN_VALUE);
            } else {
                assert Byte.MIN_VALUE <= d && d <= Byte.MAX_VALUE;

                double dFloor = Math.floor(d);
                byte dFloorByte = (byte) dFloor;
                assert dFloor == dFloorByte;

                switch (idx) {
                    case 0:
                        if (d == Byte.MAX_VALUE) {
                            // value >= d = Byte.MAX_VALUE
                            computed(0, Byte.MAX_VALUE);
                            computed(1, Byte.MAX_VALUE);
                            computed(2, Byte.MAX_VALUE);
                        } else {
                            assert dFloorByte < Byte.MAX_VALUE;
                            computed(0, dFloorByte);

                            if (d == dFloor) {
                                computed(1, dFloorByte);
                            } else {
                                assert d > dFloor;
                                computed(2, (byte) (dFloorByte + 1));

                                // dFloorByte + 0.5 is exact
                                double mid = dFloorByte + 0.5;
                                if (d < mid) {
                                    computed(1, dFloorByte);
                                } else {
                                    if (d > mid || (dFloorByte & 1) == 1) {
                                        computed(1, (byte) (dFloorByte + 1));
                                    }
                                }
                            }
                        }

                        break;
                    case 1:
                        if (d == dFloor) {
                            computed(1, dFloorByte);
                        } else {
                            assert Byte.MIN_VALUE < d && d < Byte.MAX_VALUE;
                            assert dFloorByte < Byte.MAX_VALUE;

                            computed(0, dFloorByte);
                            computed(2, (byte) (dFloorByte + 1));

                            if (d < dFloor + 0.5) {
                                computed(1, dFloorByte);
                            } else {
                                if (d > dFloor + 0.5) {
                                    computed(1, (byte) (dFloorByte + 1));
                                } else {
                                    // in the middle. Nothing to do.
                                    assert d == dFloor + 0.5;
                                }
                            }
                        }

                        break;
                    case 2:
                        if (d == Byte.MIN_VALUE) {
                            // value <= d = Byte.MIN_VALUE
                            computed(0, Byte.MIN_VALUE);
                            computed(1, Byte.MIN_VALUE);
                            computed(2, Byte.MIN_VALUE);
                        } else {
                            assert Byte.MIN_VALUE < d && d <= Byte.MAX_VALUE;
                            assert dFloor == dFloorByte;

                            if (d == Byte.MAX_VALUE) {
                                computed(1, Byte.MAX_VALUE);
                                computed(2, Byte.MAX_VALUE);
                            } else {
                                assert Byte.MIN_VALUE < d && d < Byte.MAX_VALUE;
                                assert dFloorByte < Byte.MAX_VALUE;

                                if (d == dFloor) {
                                    computed(1, dFloorByte);
                                    computed(2, dFloorByte);
                                } else {
                                    assert d > dFloor;
                                    computed(0, dFloorByte);
                                    computed(2, (byte) (dFloorByte + 1));

                                    // dFloorByte + 0.5 is exact
                                    double mid = dFloorByte + 0.5;
                                    if (d < mid || (d == mid && (dFloorByte & 1) == 0)) {
                                        computed(1, dFloorByte);
                                    } else {
                                        if (d > mid) {
                                            computed(1, (byte) (dFloorByte + 1));
                                        }
                                    }
                                }
                            }
                        }

                        break;
                    default:
                        throw new InternalError("invalid directionIndex " + idx);
                }
            }
        }
    }

    protected final void computed(int idx, float f) {
        boolean isAlreadyComputed = isFloatComputed(idx);

        super.computed(idx, f);

        // avoids infinite recursion and duplicate computation
        if (!isAlreadyComputed) {
            if (log.isLoggable(Level.FINEST)) {
                log.finest("idx = " + idx + ", f = " + f);
            }

            // update other values if enough finestrmation is available
            precomputeDoubles(f);
            precomputeFloats(idx, f);
            precomputeLongs(idx, f);
            precomputeInts(idx, f);
            precomputeShorts(idx, f);
            precomputeBytes(idx, f);
        }
    }

    private void precomputeDoubles(float f) {
        if (isFloatComputed(0) && isFloatComputed(2)
                && Float.floatToIntBits(floatValues[0]) == Float.floatToIntBits(floatValues[2])) {
            assert isFloatComputed(1) && Float.floatToIntBits(floatValues[0]) ==
                    Float.floatToIntBits(floatValues[1])
                    : "exact float value should have been set in all rounding modes";
            // exact widening conversion
            double fAsDouble = (double) f;
            computed(0, fAsDouble);
            computed(1, fAsDouble);
            computed(2, fAsDouble);
        }
    }

    /**
     * sed -e "s/double/float/g" precomputeFloats(int idx, double d)
     */
    private void precomputeFloats(int idx, float f) {
        switch (idx) {
            case 0:
                assert !getComputedFloats()[1] || !getComputedFloats()[2]
                        || Float.floatToIntBits(floatValues[1]) == Float.floatToIntBits(floatValues[2])
                        : "should have been computed " + floatValues[1];
                assert !getComputedFloats()[1] || floatValues[1] > Float.NEGATIVE_INFINITY
                        : "should have been computed " + Float.NEGATIVE_INFINITY;

                if (getComputedFloats()[1] && Formulas.signedZerosLess(f, floatValues[1])) {
                    computed(2, floatValues[1]);
                } else {
                    if (f == Float.NEGATIVE_INFINITY) {
                        computed(2, -Float.MAX_VALUE);
                    } else {
                        if (getComputedFloats()[2] && Float.floatToIntBits(f) ==
                                Float.floatToIntBits(floatValues[2])) {
                            computed(1, f);
                        }
                    }
                }

                break;
            case 1:
                assert !getComputedFloats()[0] || !getComputedFloats()[2]
                        || Float.floatToIntBits(floatValues[0]) != Float.floatToIntBits(floatValues[2])
                        : "should have been computed " + floatValues[0];

                if (getComputedFloats()[0] && Formulas.signedZerosLess(floatValues[0], f)) {
                    computed(2, f);
                } else {
                    if (f == Float.NEGATIVE_INFINITY) {
                        computed(0, Float.NEGATIVE_INFINITY);
                    } else {
                        if (f == Float.POSITIVE_INFINITY) {
                            computed(2, Float.POSITIVE_INFINITY);
                        }
                    }
                }

                break;
            case 2:
                assert !getComputedFloats()[0] || !getComputedFloats()[1]
                        || Float.floatToIntBits(floatValues[0]) ==
                        Float.floatToIntBits(floatValues[1])
                        : "should have been computed " + floatValues[1];
                assert !getComputedFloats()[1] || floatValues[1] < Float.POSITIVE_INFINITY
                        : "should have been computed " + Float.POSITIVE_INFINITY;

                if (getComputedFloats()[1] && Formulas.signedZerosLess(floatValues[1], f)) {
                    computed(0, floatValues[1]);
                } else {
                    if (f == Float.POSITIVE_INFINITY) {
                        computed(0, Float.MAX_VALUE);
                    } else {
                        if (getComputedFloats()[0] && Float.floatToIntBits(floatValues[0]) ==
                                Float.floatToIntBits(f)) {
                            computed(1, f);
                        }
                    }
                }

                break;
            default:
                throw new InternalError();
        }
    }

    // Long.MAX_VALUE rounded to positive infinity
    private static final float floatPow2_63 = (float) Formulas.pow2(63);
    private static final float floatNextPow2_63 = floatPow2_63 + (1L << 40);
    private static final float floatPreviousMinusPow2_63 = -floatPow2_63 - (1L << 40);

    static {
        /*
        Actually equal to Long.MAX_VALUE rounded to nearest because Long.MAX_VALUE is closer to
        pow2(63) than to previous(pow2(63)) == (2 - pow2(-23)) * pow2(62).
        */
        assert floatPow2_63 == Long.MAX_VALUE && floatPow2_63 == -(float) Long.MIN_VALUE;
        assert Formulas.next(floatPow2_63) == floatNextPow2_63;
        assert Formulas.previous(-floatPow2_63) == floatPreviousMinusPow2_63;
    }

    /**
     * sed -e "s/double/float/g" precomputeLongs(int idx, double d)
     * Replacement permitted because both double and float do not have enough precision for all
     * long values, and both of them can have magnitude much higher than long.
     */
    private void precomputeLongs(int idx, float f) {
        if (f > floatPow2_63) {
            assert f >= floatNextPow2_63;
            computed(0, Long.MAX_VALUE);
            computed(1, Long.MAX_VALUE);
            computed(2, Long.MAX_VALUE);
        } else {
            if (f < -floatPow2_63) {
                assert f <= floatPreviousMinusPow2_63;
                computed(0, Long.MIN_VALUE);
                computed(1, Long.MIN_VALUE);
                computed(2, Long.MIN_VALUE);
            } else {
                assert -floatPow2_63 <= f && f <= floatPow2_63;

                float fFloor = Formulas.floor(f);
                long fFloorLong = (long) fFloor;

                switch (idx) {
                    case 0:
                        if (-Formulas.FLOAT_MAX_EXACT_INT <= f && f < Formulas.FLOAT_MAX_EXACT_INT) {
                            assert fFloor == fFloorLong;
                            computed(0, fFloorLong);
                        } else {
                            if (f == floatPow2_63) {
                                // value >= pow2(63) > Long.MAX_VALUE
                                assert fFloor > fFloorLong && fFloorLong == Long.MAX_VALUE;
                                computed(0, Long.MAX_VALUE);
                                computed(1, Long.MAX_VALUE);
                                computed(2, Long.MAX_VALUE);
                            }
                        }

                        break;
                    case 1:
                        if (-Formulas.FLOAT_MAX_EXACT_INT < f && f < Formulas.FLOAT_MAX_EXACT_INT) {
                            assert fFloor == fFloorLong;

                            if (f == fFloor) {
                                computed(1, fFloorLong);
                            } else {
                                assert -1 << 23 < f && f < 1 << 23;
                                assert Formulas.ceil(f) == fFloor + 1;

                                computed(0, fFloorLong);
                                computed(2, fFloorLong + 1);

                                if (f < fFloor + 0.5F) {
                                    computed(1, fFloorLong);
                                } else {
                                    if (f > fFloor + 0.5F) {
                                        computed(1, fFloorLong + 1);
                                    } else {
                                        // in the middle. Nothing to do.
                                        assert f == fFloor + 0.5F;
                                    }
                                }
                            }
                        }

                        break;
                    case 2:
                        if (-Formulas.FLOAT_MAX_EXACT_INT < f && f <= Formulas.FLOAT_MAX_EXACT_INT) {
                            float fCeil = Formulas.ceil(f);
                            long fCeilLong = (long) fCeil;
                            assert fCeil == fCeilLong;
                            computed(2, fCeilLong);
                        } else {
                            if (f == -floatPow2_63) {
                                // value <= Long.MIN_VALUE
                                assert fFloor == fFloorLong;
                                computed(0, Long.MIN_VALUE);
                                computed(1, Long.MIN_VALUE);
                                computed(2, Long.MIN_VALUE);
                            }
                        }

                        break;
                    default:
                        throw new InternalError("invalid directionIndex " + idx);
                }
            }
        }
    }

    // Integer.MAX_VALUE rounded to positive infinity
    private static final float floatPow2_31 = (float) Formulas.pow2(31);
    private static final float floatNextPow2_31 = floatPow2_31 + (1 << 8);
    private static final float floatPreviousMinusPow2_31 = -floatPow2_31 - (1 << 8);

    static {
        /*
        Actually equal to Integer.MAX_VALUE rounded to nearest because Integer.MAX_VALUE is closer to
        pow2(31) than to previous(pow2(31)) == (2 - pow2(-23)) * pow2(30).
        */
        assert floatPow2_31 == Integer.MAX_VALUE && floatPow2_31 == -(float) Integer.MIN_VALUE;
        assert Formulas.next(floatPow2_31) == floatNextPow2_31;
        assert Formulas.previous(-floatPow2_31) == floatPreviousMinusPow2_31;
    }

    /**
     * sed -e "s/long/int/g" precomputeLongs(int idx, float f)
     * Replacement permitted because float does not have enough precision for either long or int,
     * but it has values with magnitude much greater than Long.MAX_VALUE and Integer.MAX_VALUE.
     */
    private void precomputeInts(int idx, float f) {
        if (f > floatPow2_31) {
            assert f >= floatNextPow2_31;
            computed(0, Integer.MAX_VALUE);
            computed(1, Integer.MAX_VALUE);
            computed(2, Integer.MAX_VALUE);
        } else {
            if (f < -floatPow2_31) {
                assert f <= floatPreviousMinusPow2_31;
                computed(0, Integer.MIN_VALUE);
                computed(1, Integer.MIN_VALUE);
                computed(2, Integer.MIN_VALUE);
            } else {
                assert -floatPow2_31 <= f && f <= floatPow2_31;

                float fFloor = Formulas.floor(f);
                int fFloorInteger = (int) fFloor;

                switch (idx) {
                    case 0:
                        if (-Formulas.FLOAT_MAX_EXACT_INT <= f && f < Formulas.FLOAT_MAX_EXACT_INT) {
                            assert fFloor == fFloorInteger;
                            computed(0, fFloorInteger);
                        } else {
                            if (f == floatPow2_31) {
                                // value >= pow2(31) > Integer.MAX_VALUE
                                assert fFloor > fFloorInteger && fFloorInteger == Integer.MAX_VALUE;
                                computed(0, Integer.MAX_VALUE);
                                computed(1, Integer.MAX_VALUE);
                                computed(2, Integer.MAX_VALUE);
                            }
                        }

                        break;
                    case 1:
                        if (-Formulas.FLOAT_MAX_EXACT_INT < f && f < Formulas.FLOAT_MAX_EXACT_INT) {
                            assert fFloor == fFloorInteger;

                            if (f == fFloor) {
                                computed(1, fFloorInteger);
                            } else {
                                assert -1 << 23 < f && f < 1 << 23;
                                assert Formulas.ceil(f) == fFloor + 1;

                                computed(0, fFloorInteger);
                                computed(2, fFloorInteger + 1);

                                if (f < fFloor + 0.5F) {
                                    computed(1, fFloorInteger);
                                } else {
                                    if (f > fFloor + 0.5F) {
                                        computed(1, fFloorInteger + 1);
                                    } else {
                                        // in the middle. Nothing to do.
                                        assert f == fFloor + 0.5F;
                                    }
                                }
                            }
                        }

                        break;
                    case 2:
                        if (-Formulas.FLOAT_MAX_EXACT_INT < f && f <= Formulas.FLOAT_MAX_EXACT_INT) {
                            float fCeil = Formulas.ceil(f);
                            int fCeilInteger = (int) fCeil;
                            assert fCeil == fCeilInteger;
                            computed(2, fCeilInteger);
                        } else {
                            if (f == -floatPow2_31) {
                                // value <= Integer.MIN_VALUE
                                assert fFloor == fFloorInteger;
                                computed(0, Integer.MIN_VALUE);
                                computed(1, Integer.MIN_VALUE);
                                computed(2, Integer.MIN_VALUE);
                            }
                        }

                        break;
                    default:
                        throw new InternalError("invalid directionIndex " + idx);
                }
            }
        }
    }

    /**
     * sed -e "s/double/float/g" precomputeShorts(int idx, double d)
     * Replacement permitted because both double and float have enough precision for all
     * short values, and both of them support magnitudes much higher than short.
     */
    private void precomputeShorts(int idx, float f) {
        if (f > Short.MAX_VALUE) {
            computed(0, Short.MAX_VALUE);
            computed(1, Short.MAX_VALUE);
            computed(2, Short.MAX_VALUE);
        } else {
            if (f < Short.MIN_VALUE) {
                computed(0, Short.MIN_VALUE);
                computed(1, Short.MIN_VALUE);
                computed(2, Short.MIN_VALUE);
            } else {
                assert Short.MIN_VALUE <= f && f <= Short.MAX_VALUE;

                float fFloor = Formulas.floor(f);
                short fFloorShort = (short) fFloor;
                assert fFloor == fFloorShort;

                switch (idx) {
                    case 0:
                        if (f == Short.MAX_VALUE) {
                            // value >= f = Short.MAX_VALUE
                            computed(0, Short.MAX_VALUE);
                            computed(1, Short.MAX_VALUE);
                            computed(2, Short.MAX_VALUE);
                        } else {
                            assert fFloorShort < Short.MAX_VALUE;
                            computed(0, fFloorShort);

                            if (f == fFloor) {
                                computed(1, fFloorShort);
                            } else {
                                assert f > fFloor;
                                computed(2, (short) (fFloorShort + 1));

                                // fFloorShort + 0.5F is exact
                                float mid = fFloorShort + 0.5F;
                                if (f < mid) {
                                    computed(1, fFloorShort);
                                } else {
                                    if (f > mid || (fFloorShort & 1) == 1) {
                                        computed(1, (short) (fFloorShort + 1));
                                    }
                                }
                            }
                        }

                        break;
                    case 1:
                        if (f == fFloor) {
                            computed(1, fFloorShort);
                        } else {
                            assert Short.MIN_VALUE < f && f < Short.MAX_VALUE;
                            assert fFloorShort < Short.MAX_VALUE;

                            computed(0, fFloorShort);
                            computed(2, (short) (fFloorShort + 1));

                            if (f < fFloor + 0.5F) {
                                computed(1, fFloorShort);
                            } else {
                                if (f > fFloor + 0.5F) {
                                    computed(1, (short) (fFloorShort + 1));
                                } else {
                                    // in the middle. Nothing to do.
                                    assert f == fFloor + 0.5F;
                                }
                            }
                        }

                        break;
                    case 2:
                        if (f == Short.MIN_VALUE) {
                            // value <= f = Short.MIN_VALUE
                            computed(0, Short.MIN_VALUE);
                            computed(1, Short.MIN_VALUE);
                            computed(2, Short.MIN_VALUE);
                        } else {
                            assert Short.MIN_VALUE < f && f <= Short.MAX_VALUE;
                            assert fFloor == fFloorShort;

                            if (f == Short.MAX_VALUE) {
                                computed(1, Short.MAX_VALUE);
                                computed(2, Short.MAX_VALUE);
                            } else {
                                assert Short.MIN_VALUE < f && f < Short.MAX_VALUE;
                                assert fFloorShort < Short.MAX_VALUE;

                                if (f == fFloor) {
                                    computed(1, fFloorShort);
                                    computed(2, fFloorShort);
                                } else {
                                    assert f > fFloor;
                                    computed(0, fFloorShort);
                                    computed(2, (short) (fFloorShort + 1));

                                    // fFloorShort + 0.5F is exact
                                    float mid = fFloorShort + 0.5F;
                                    if (f < mid || (f == mid && (fFloorShort & 1) == 0)) {
                                        computed(1, fFloorShort);
                                    } else {
                                        if (f > mid) {
                                            computed(1, (short) (fFloorShort + 1));
                                        }
                                    }
                                }
                            }
                        }

                        break;
                    default:
                        throw new InternalError("invalid directionIndex " + idx);
                }
            }
        }
    }

    /**
     * sed -e "s/double/float/g" precomputeBytes(int idx, double f)
     * Replacement permitted because both double and float have enough precision for all
     * short values, and both of them support magnitudes much higher than byte.
     */
    private void precomputeBytes(int idx, float f) {
        if (f > Byte.MAX_VALUE) {
            computed(0, Byte.MAX_VALUE);
            computed(1, Byte.MAX_VALUE);
            computed(2, Byte.MAX_VALUE);
        } else {
            if (f < Byte.MIN_VALUE) {
                computed(0, Byte.MIN_VALUE);
                computed(1, Byte.MIN_VALUE);
                computed(2, Byte.MIN_VALUE);
            } else {
                assert Byte.MIN_VALUE <= f && f <= Byte.MAX_VALUE;

                float fFloor = Formulas.floor(f);
                byte fFloorByte = (byte) fFloor;
                assert fFloor == fFloorByte;

                switch (idx) {
                    case 0:
                        if (f == Byte.MAX_VALUE) {
                            // value >= f = Byte.MAX_VALUE
                            computed(0, Byte.MAX_VALUE);
                            computed(1, Byte.MAX_VALUE);
                            computed(2, Byte.MAX_VALUE);
                        } else {
                            assert fFloorByte < Byte.MAX_VALUE;
                            computed(0, fFloorByte);

                            if (f == fFloor) {
                                computed(1, fFloorByte);
                            } else {
                                assert f > fFloor;
                                computed(2, (byte) (fFloorByte + 1));

                                // fFloorByte + 0.5F is exact
                                float mid = fFloorByte + 0.5F;
                                if (f < mid) {
                                    computed(1, fFloorByte);
                                } else {
                                    if (f > mid || (fFloorByte & 1) == 1) {
                                        computed(1, (byte) (fFloorByte + 1));
                                    }
                                }
                            }
                        }

                        break;
                    case 1:
                        if (f == fFloor) {
                            computed(1, fFloorByte);
                        } else {
                            assert Byte.MIN_VALUE < f && f < Byte.MAX_VALUE;
                            assert fFloorByte < Byte.MAX_VALUE;

                            computed(0, fFloorByte);
                            computed(2, (byte) (fFloorByte + 1));

                            if (f < fFloor + 0.5F) {
                                computed(1, fFloorByte);
                            } else {
                                if (f > fFloor + 0.5F) {
                                    computed(1, (byte) (fFloorByte + 1));
                                } else {
                                    // in the middle. Nothing to do.
                                    assert f == fFloor + 0.5F;
                                }
                            }
                        }

                        break;
                    case 2:
                        if (f == Byte.MIN_VALUE) {
                            // value <= f = Byte.MIN_VALUE
                            computed(0, Byte.MIN_VALUE);
                            computed(1, Byte.MIN_VALUE);
                            computed(2, Byte.MIN_VALUE);
                        } else {
                            assert Byte.MIN_VALUE < f && f <= Byte.MAX_VALUE;
                            assert fFloor == fFloorByte;

                            if (f == Byte.MAX_VALUE) {
                                computed(1, Byte.MAX_VALUE);
                                computed(2, Byte.MAX_VALUE);
                            } else {
                                assert Byte.MIN_VALUE < f && f < Byte.MAX_VALUE;
                                assert fFloorByte < Byte.MAX_VALUE;

                                if (f == fFloor) {
                                    computed(1, fFloorByte);
                                    computed(2, fFloorByte);
                                } else {
                                    assert f > fFloor;
                                    computed(0, fFloorByte);
                                    computed(2, (byte) (fFloorByte + 1));

                                    // fFloorByte + 0.5F is exact
                                    float mid = fFloorByte + 0.5F;
                                    if (f < mid || (f == mid && (fFloorByte & 1) == 0)) {
                                        computed(1, fFloorByte);
                                    } else {
                                        if (f > mid) {
                                            computed(1, (byte) (fFloorByte + 1));
                                        }
                                    }
                                }
                            }
                        }

                        break;
                    default:
                        throw new InternalError("invalid directionIndex " + idx);
                }
            }
        }
    }

    protected final void computed(int idx, long l) {
        boolean isAlreadyComputed = isLongComputed(idx);

        super.computed(idx, l);

        // avoids infinite recursion and duplicate computation
        if (!isAlreadyComputed) {
            if (log.isLoggable(Level.FINEST)) {
                log.finest("idx = " + idx + ", l = " + l);
            }

            // update other values if enough finestrmation is available
            precomputeDoubles(idx, l);
            precomputeFloats(idx, l);
            precomputeLongs(idx, l);
            precomputeInts(idx, l);
            precomputeShorts(idx, l);
            precomputeBytes(idx, l);
        }
    }

    private void precomputeDoubles(int idx, long l) {
        // avoid signed zeroes problem
        if (Long.MIN_VALUE < l && l != 0 && l < Long.MAX_VALUE) {
            Formulas.doubleInterval(l, getDoubleTemp());

            double low = doubleTemp[0], high = doubleTemp[1];

            double lAsDouble = (double) l;

            if (getComputedLongs()[0] && getComputedLongs()[2] && longValues[0] == longValues[2]) {
                assert longValues[0] == l && longValues[1] == l;
                computed(0, low);
                computed(2, high);
                computed(1, (double) l);
            } else {
                switch (idx) {
                    case 0:
                        // low <= l0 <= x < l0 + 1

                        if (Formulas.lessThanOrEqual(l + 1, high)) {
                            double lAddOneAsDouble = (double) (l + 1);

                            assert lAddOneAsDouble == low
                                    || lAddOneAsDouble == high;
                            computed(0, low);
                            computed(2, high);

                            if (lAddOneAsDouble == low) {
                                computed(1, low);
                            } else {
                                if (lAsDouble == high) {
                                    computed(1, high);
                                }
                            }
                        }

                        break;
                    case 1:
                        // if l even, l - 0.5 <= x <= l + 0.5; else l - 0.5 < x < l + 0.5

                        Formulas.doubleInterval(l - 1, doubleTemp);
                        double lMinusOneLow = doubleTemp[0], lMinusOneHigh = doubleTemp[1];

                        if (lMinusOneHigh >= low) {
                            assert (lMinusOneLow == low && lMinusOneHigh == high)
                                    || (lMinusOneHigh == low);
                            assert !Formulas.isExactDouble(l);
                            computed(0, low);
                        }

                        Formulas.doubleInterval(l + 1, doubleTemp);
                        double lPlusOneLow = doubleTemp[0], lPlusOneHigh = doubleTemp[1];

                        if (lPlusOneLow <= high) {
                            assert (lPlusOneLow == low && lPlusOneHigh == high)
                                    || (lPlusOneLow == high);
                            assert !Formulas.isExactDouble(l);
                            computed(2, high);
                        }

                        // todo try to deduce the round-to-nearest value
                        break;
                    case 2:
                        // l2 - 1 < x <= l2 <= high

                        if (Formulas.greaterThanOrEqual(l - 1, low)) {
                            double lMinusOneAsDouble = (double) (l - 1);

                            assert lMinusOneAsDouble == low
                                    || lMinusOneAsDouble == high;
                            computed(0, low);
                            computed(2, high);

                            if (lMinusOneAsDouble == high) {
                                computed(1, high);
                            } else {
                                if (lAsDouble == low) {
                                    computed(1, low);
                                }
                            }
                        }

                        break;
                    default:
                        throw new InternalError("invalid directionIndex " + idx);
                }
            }
        }
    }

    /**
     * sed precomputeDoubles(int idx, long l) -e "s/float/double/"
     */
    private void precomputeFloats(int idx, long l) {
        // avoid signed zeroes problem
        if (Long.MIN_VALUE < l && l != 0 && l < Long.MAX_VALUE) {
            Formulas.floatInterval(l, getFloatTemp());

            float low = floatTemp[0], high = floatTemp[1];

            float lAsFloat = (float) l;

            if (getComputedLongs()[0] && getComputedLongs()[2] && longValues[0] == longValues[2]) {
                assert longValues[0] == l && longValues[1] == l;
                computed(0, low);
                computed(2, high);
                computed(1, (float) l);
            } else {
                switch (idx) {
                    case 0:
                        // low <= l0 <= x < l0 + 1

                        if (Formulas.lessThanOrEqual(l + 1, high)) {
                            float lAddOneAsFloat = (float) (l + 1);

                            assert lAddOneAsFloat == low
                                    || lAddOneAsFloat == high;
                            computed(0, low);
                            computed(2, high);

                            if (lAddOneAsFloat == low) {
                                computed(1, low);
                            } else {
                                if (lAsFloat == high) {
                                    computed(1, high);
                                }
                            }
                        }

                        break;
                    case 1:
                        // if l even, l - 0.5 <= x <= l + 0.5; else l - 0.5 < x < l + 0.5

                        Formulas.floatInterval(l - 1, floatTemp);
                        float lMinusOneLow = floatTemp[0], lMinusOneHigh = floatTemp[1];

                        if (lMinusOneHigh >= low) {
                            assert (lMinusOneLow == low && lMinusOneHigh == high)
                                    || (lMinusOneHigh == low);
                            assert !Formulas.isExactFloat(l);
                            computed(0, low);
                        }

                        Formulas.floatInterval(l + 1, floatTemp);
                        float lPlusOneLow = floatTemp[0], lPlusOneHigh = floatTemp[1];

                        if (lPlusOneLow <= high) {
                            assert (lPlusOneLow == low && lPlusOneHigh == high)
                                    || (lPlusOneLow == high);
                            assert !Formulas.isExactFloat(l);
                            computed(2, high);
                        }

                        // todo try to deduce the round-to-nearest value
                        break;
                    case 2:
                        // l2 - 1 < x <= l2 <= high

                        if (Formulas.greaterThanOrEqual(l - 1, low)) {
                            float lMinusOneAsFloat = (float) (l - 1);

                            assert lMinusOneAsFloat == low
                                    || lMinusOneAsFloat == high;
                            computed(0, low);
                            computed(2, high);

                            if (lMinusOneAsFloat == high) {
                                computed(1, high);
                            } else {
                                if (lAsFloat == low) {
                                    computed(1, low);
                                }
                            }
                        }

                        break;
                    default:
                        throw new InternalError("invalid directionIndex " + idx);
                }
            }
        }
    }

    private void precomputeLongs(int idx, long l) {
        switch (idx) {
            case 0:
                assert !getComputedLongs()[1] || !getComputedLongs()[2]
                        || longValues[1] == longValues[2]
                        : "should have been computed " + longValues[1];
                assert !getComputedLongs()[1] || longValues[1] > Long.MIN_VALUE
                        : "should have been computed " + Long.MIN_VALUE;

                if (getComputedLongs()[1] && l < longValues[1]) {
                    computed(2, longValues[1]);
                } else {
                    if (getComputedLongs()[2] && l == longValues[2]) {
                        computed(1, l);
                    }
                }

                break;
            case 1:
                assert !getComputedLongs()[0] || !getComputedLongs()[2]
                        || longValues[0] != longValues[2]
                        : "should have been computed " + longValues[0];

                if (getComputedLongs()[0] && longValues[0] < l) {
                    computed(2, l);
                } else {
                    if (l == Long.MAX_VALUE) {
                        computed(2, Long.MAX_VALUE);
                    }
                }

                break;
            case 2:
                assert !getComputedLongs()[0] || !getComputedLongs()[1]
                        || longValues[0] == longValues[1]
                        : "should have been computed " + longValues[1];
                assert !getComputedLongs()[1] || longValues[1] < Long.MAX_VALUE
                        : "should have been computed " + Long.MAX_VALUE;

                if (getComputedLongs()[1] && longValues[1] < l) {
                    computed(0, longValues[1]);
                } else {
                    if (getComputedLongs()[0] && longValues[0] == l) {
                        computed(1, l);
                    }
                }

                break;
            default:
                throw new InternalError();
        }
    }

    private void precomputeInts(int idx, long l) {
        if (l > Integer.MAX_VALUE) {
            computed(0, Integer.MAX_VALUE);
            computed(1, Integer.MAX_VALUE);
            computed(2, Integer.MAX_VALUE);
        } else {
            if (l < Integer.MIN_VALUE) {
                computed(0, Integer.MIN_VALUE);
                computed(1, Integer.MIN_VALUE);
                computed(2, Integer.MIN_VALUE);
            } else {
                assert Integer.MIN_VALUE <= l && l <= Integer.MAX_VALUE;
                assert (int) l == l;

                computed(idx, (int) l);
            }
        }
    }

    private void precomputeShorts(int idx, long l) {
        if (l > Short.MAX_VALUE) {
            computed(0, Short.MAX_VALUE);
            computed(1, Short.MAX_VALUE);
            computed(2, Short.MAX_VALUE);
        } else {
            if (l < Short.MIN_VALUE) {
                computed(0, Short.MIN_VALUE);
                computed(1, Short.MIN_VALUE);
                computed(2, Short.MIN_VALUE);
            } else {
                assert Short.MIN_VALUE <= l && l <= Short.MAX_VALUE;
                assert (short) l == l;

                computed(idx, (short) l);
            }
        }
    }

    private void precomputeBytes(int idx, long l) {
        if (l > Byte.MAX_VALUE) {
            computed(0, Byte.MAX_VALUE);
            computed(1, Byte.MAX_VALUE);
            computed(2, Byte.MAX_VALUE);
        } else {
            if (l < Byte.MIN_VALUE) {
                computed(0, Byte.MIN_VALUE);
                computed(1, Byte.MIN_VALUE);
                computed(2, Byte.MIN_VALUE);
            } else {
                assert Byte.MIN_VALUE <= l && l <= Byte.MAX_VALUE;
                assert (byte) l == l;

                computed(idx, (byte) l);
            }
        }
    }

    protected final void computed(int idx, int i) {
        boolean isAlreadyComputed = isIntComputed(idx);

        super.computed(idx, i);

        // avoids infinite recursion and duplicate computation
        if (!isAlreadyComputed) {
            if (log.isLoggable(Level.FINEST)) {
                log.finest("idx = " + idx + ", i = " + i);
            }

            // update other values if enough finestrmation is available
            precomputeDoubles(i);
            precomputeFloats(idx, i);
            precomputeLongs(idx, i);
            precomputeInts(idx, i);
            precomputeShorts(idx, i);
            precomputeBytes(idx, i);
        }
    }

    /**
     * Double has enough precision for integer values.
     */
    private void precomputeDoubles(int i) {
        // avoid signed zeroes problem
        if (Integer.MIN_VALUE < i && i != 0 && i < Integer.MAX_VALUE) {
            assert Formulas.isExactDouble((long) i);

            if (getComputedInts()[0] && getComputedInts()[2] && intValues[0] == intValues[2]) {
                assert intValues[0] == i && intValues[1] == i;
                double iAsDouble = (double) i;
                computed(0, iAsDouble);
                computed(1, iAsDouble);
                computed(2, iAsDouble);
            }
        }
    }

    private void precomputeFloats(int idx, int i) {
        // avoid signed zeroes problem
        if (Integer.MIN_VALUE < i && i != 0 && i < Integer.MAX_VALUE) {
            Formulas.floatInterval(i, getFloatTemp());

            float low = floatTemp[0], high = floatTemp[1];

            float lAsFloat = (float) i;

            if (getComputedInts()[0] && getComputedInts()[2] && intValues[0] == intValues[2]) {
                assert intValues[0] == i && intValues[1] == i;
                computed(0, low);
                computed(2, high);
                computed(1, (float) i);
            } else {
                switch (idx) {
                    case 0:
                        // low <= l0 <= x < l0 + 1

                        if (Formulas.lessThanOrEqual(i + 1, high)) {
                            float lAddOneAsFloat = (float) (i + 1);

                            assert lAddOneAsFloat == low
                                    || lAddOneAsFloat == high;
                            computed(0, low);
                            computed(2, high);

                            if (lAddOneAsFloat == low) {
                                computed(1, low);
                            } else {
                                if (lAsFloat == high) {
                                    computed(1, high);
                                }
                            }
                        }

                        break;
                    case 1:
                        // if i even, i - 0.5 <= x <= i + 0.5; else i - 0.5 < x < i + 0.5

                        Formulas.floatInterval(i - 1, floatTemp);
                        float lMinusOneLow = floatTemp[0], lMinusOneHigh = floatTemp[1];

                        if (lMinusOneHigh >= low) {
                            assert (lMinusOneLow == low && lMinusOneHigh == high)
                                    || (lMinusOneHigh == low);
                            assert !Formulas.isExactFloat(i);
                            computed(0, low);
                        }

                        Formulas.floatInterval(i + 1, floatTemp);
                        float lPlusOneLow = floatTemp[0], lPlusOneHigh = floatTemp[1];

                        if (lPlusOneLow <= high) {
                            assert (lPlusOneLow == low && lPlusOneHigh == high)
                                    || (lPlusOneLow == high);
                            assert !Formulas.isExactFloat(i);
                            computed(2, high);
                        }

                        // todo try to deduce the round-to-nearest value
                        break;
                    case 2:
                        // l2 - 1 < x <= l2 <= high

                        if (Formulas.greaterThanOrEqual(i - 1, low)) {
                            float lMinusOneAsFloat = (float) (i - 1);

                            assert lMinusOneAsFloat == low
                                    || lMinusOneAsFloat == high;
                            computed(0, low);
                            computed(2, high);

                            if (lMinusOneAsFloat == high) {
                                computed(1, high);
                            } else {
                                if (lAsFloat == low) {
                                    computed(1, low);
                                }
                            }
                        }

                        break;
                    default:
                        throw new InternalError("invalid directionIndex " + idx);
                }
            }
        }
    }

    private void precomputeLongs(int idx, int i) {
        if (Integer.MIN_VALUE < i && i < Integer.MAX_VALUE) {
            computed(idx, (long) i);
        }
        ;
    }

    /**
     * sed precomputeLongs(int idx, long l) -e "s/long/int/g"
     */
    private void precomputeInts(int idx, int i) {
        switch (idx) {
            case 0:
                assert !getComputedInts()[1] || !getComputedInts()[2]
                        || intValues[1] == intValues[2]
                        : "should have been computed " + intValues[1];
                assert !getComputedInts()[1] || intValues[1] > Integer.MIN_VALUE
                        : "should have been computed " + Integer.MIN_VALUE;

                if (getComputedInts()[1] && i < intValues[1]) {
                    computed(2, intValues[1]);
                } else {
                    if (getComputedInts()[2] && i == intValues[2]) {
                        computed(1, i);
                    }
                }

                break;
            case 1:
                assert !getComputedInts()[0] || !getComputedInts()[2]
                        || intValues[0] != intValues[2]
                        : "should have been computed " + intValues[0];

                if (getComputedInts()[0] && intValues[0] < i) {
                    computed(2, i);
                } else {
                    if (i == Integer.MAX_VALUE) {
                        computed(2, Integer.MAX_VALUE);
                    }
                }

                break;
            case 2:
                assert !getComputedInts()[0] || !getComputedInts()[1]
                        || intValues[0] == intValues[1]
                        : "should have been computed " + intValues[1];
                assert !getComputedInts()[1] || intValues[1] < Integer.MAX_VALUE
                        : "should have been computed " + Integer.MAX_VALUE;

                if (getComputedInts()[1] && intValues[1] < i) {
                    computed(0, intValues[1]);
                } else {
                    if (getComputedInts()[0] && intValues[0] == i) {
                        computed(1, i);
                    }
                }

                break;
            default:
                throw new InternalError();
        }
    }

    /**
     * sed precomputeShorts(int idx, long l) -e "s/long/int/g"
     */
    private void precomputeShorts(int idx, int i) {
        if (i > Short.MAX_VALUE) {
            computed(0, Short.MAX_VALUE);
            computed(1, Short.MAX_VALUE);
            computed(2, Short.MAX_VALUE);
        } else {
            if (i < Short.MIN_VALUE) {
                computed(0, Short.MIN_VALUE);
                computed(1, Short.MIN_VALUE);
                computed(2, Short.MIN_VALUE);
            } else {
                assert Short.MIN_VALUE <= i && i <= Short.MAX_VALUE;
                assert (short) i == i;

                computed(idx, (short) i);
            }
        }
    }

    /**
     * sed precomputeBytes(int idx, long l) -e "s/long/int/g"
     */
    private void precomputeBytes(int idx, int i) {
        if (i > Byte.MAX_VALUE) {
            computed(0, Byte.MAX_VALUE);
            computed(1, Byte.MAX_VALUE);
            computed(2, Byte.MAX_VALUE);
        } else {
            if (i < Byte.MIN_VALUE) {
                computed(0, Byte.MIN_VALUE);
                computed(1, Byte.MIN_VALUE);
                computed(2, Byte.MIN_VALUE);
            } else {
                assert Byte.MIN_VALUE <= i && i <= Byte.MAX_VALUE;
                assert (byte) i == i;

                computed(idx, (byte) i);
            }
        }
    }

    protected final void computed(int idx, short s) {
        boolean isAlreadyComputed = isShortComputed(idx);

        super.computed(idx, s);

        // avoids infinite recursion and duplicate computation
        if (!isAlreadyComputed) {
            if (log.isLoggable(Level.FINEST)) {
                log.finest("idx = " + idx + ", s = " + s);
            }

            // update other values if enough finestrmation is available
            precomputeDoubles(s);
            precomputeFloats(s);
            precomputeLongs(idx, s);
            precomputeInts(idx, s);
            precomputeShorts(idx, s);
            precomputeBytes(idx, s);
        }
    }

    /**
     * Double has enough precision for all short values.
     * sed precomputeDoubles(int i) -e "s/int/short/g"
     */
    private void precomputeDoubles(short s) {
        // avoid signed zeroes problem
        if (Short.MIN_VALUE < s && s != 0 && s < Short.MAX_VALUE) {
            if (getComputedInts()[0] && getComputedInts()[2] && shortValues[0] == shortValues[2]) {
                assert shortValues[0] == s && shortValues[1] == s;
                double sAsDouble = (double) s;
                computed(0, sAsDouble);
                computed(1, sAsDouble);
                computed(2, sAsDouble);
            }
        }
    }

    /**
     * Float has enough precision for all short values.
     * sed precomputeDoubles(short s) -e "s/double/float/g"
     */
    private void precomputeFloats(short s) {
        // avoid signed zeroes problem
        if (Short.MIN_VALUE < s && s != 0 && s < Short.MAX_VALUE) {
            if (getComputedInts()[0] && getComputedInts()[2] && shortValues[0] == shortValues[2]) {
                assert shortValues[0] == s && shortValues[1] == s;
                float sAsFloat = (float) s;
                computed(0, sAsFloat);
                computed(1, sAsFloat);
                computed(2, sAsFloat);
            }
        }
    }

    /**
     * sed precomputeLongs(int idx, int i) -e "s/int/short/g"
     */
    private void precomputeLongs(int idx, short s) {
        if (Short.MIN_VALUE < s && s < Short.MAX_VALUE) {
            computed(idx, (long) s);
        }
    }

    /**
     * sed precomputeLongs(int idx, short s) -e "s/long/int/g"
     */
    private void precomputeInts(int idx, short s) {
        if (Short.MIN_VALUE < s && s < Short.MAX_VALUE) {
            computed(idx, (int) s);
        }
    }

    /**
     * sed precomputeInts(int idx, int s) -e "s/int/short/g"
     */
    private void precomputeShorts(int idx, short s) {
        switch (idx) {
            case 0:
                assert !getComputedShorts()[1] || !getComputedShorts()[2]
                        || shortValues[1] == shortValues[2]
                        : "should have been computed " + shortValues[1];
                assert !getComputedShorts()[1] || shortValues[1] > Short.MIN_VALUE
                        : "should have been computed " + Short.MIN_VALUE;

                if (getComputedShorts()[1] && s < shortValues[1]) {
                    computed(2, shortValues[1]);
                } else {
                    if (getComputedShorts()[2] && s == shortValues[2]) {
                        computed(1, s);
                    }
                }

                break;
            case 1:
                assert !getComputedShorts()[0] || !getComputedShorts()[2]
                        || shortValues[0] != shortValues[2]
                        : "should have been computed " + shortValues[0];

                if (getComputedShorts()[0] && shortValues[0] < s) {
                    computed(2, s);
                } else {
                    if (s == Short.MAX_VALUE) {
                        computed(2, Short.MAX_VALUE);
                    }
                }

                break;
            case 2:
                assert !getComputedShorts()[0] || !getComputedShorts()[1]
                        || shortValues[0] == shortValues[1]
                        : "should have been computed " + shortValues[1];
                assert !getComputedShorts()[1] || shortValues[1] < Short.MAX_VALUE
                        : "should have been computed " + Short.MAX_VALUE;

                if (getComputedShorts()[1] && shortValues[1] < s) {
                    computed(0, shortValues[1]);
                } else {
                    if (getComputedShorts()[0] && shortValues[0] == s) {
                        computed(1, s);
                    }
                }

                break;
            default:
                throw new InternalError();
        }
    }

    /**
     * sed precomputeShorts(int idx, short s) -e "s/short/byte/g"
     */
    private void precomputeBytes(int idx, short s) {
        if (s > Byte.MAX_VALUE) {
            computed(0, Byte.MAX_VALUE);
            computed(1, Byte.MAX_VALUE);
            computed(2, Byte.MAX_VALUE);
        } else {
            if (s < Byte.MIN_VALUE) {
                computed(0, Byte.MIN_VALUE);
                computed(1, Byte.MIN_VALUE);
                computed(2, Byte.MIN_VALUE);
            } else {
                assert Byte.MIN_VALUE <= s && s <= Byte.MAX_VALUE;
                assert (byte) s == s;

                computed(idx, (byte) s);
            }
        }
    }

    protected final void computed(int idx, byte b) {
        boolean isAlreadyComputed = isByteComputed(idx);

        super.computed(idx, b);

        // avoids infinite recursion and duplicate computation
        if (!isAlreadyComputed) {
            if (log.isLoggable(Level.FINEST)) {
                log.finest("idx = " + idx + ", b = " + b);
            }

            // update other values if enough finestrmation is available
            precomputeDoubles(b);
            precomputeFloats(b);
            precomputeLongs(idx, b);
            precomputeInts(idx, b);
            precomputeShorts(idx, b);
            precomputeBytes(idx, b);
        }
    }

    /**
     * Double has enough precision for all byte values.
     * sed precomputeDoubles(int i) -e "b/int/byte/g"
     */
    private void precomputeDoubles(byte b) {
        // avoid signed zeroes problem
        if (Byte.MIN_VALUE < b && b != 0 && b < Byte.MAX_VALUE) {
            if (getComputedInts()[0] && getComputedInts()[2] && byteValues[0] == byteValues[2]) {
                assert byteValues[0] == b && byteValues[1] == b;
                double sAsDouble = (double) b;
                computed(0, sAsDouble);
                computed(1, sAsDouble);
                computed(2, sAsDouble);
            }
        }
    }

    /**
     * Float has enough precision for all byte values.
     * sed precomputeFloats(int i) -e "b/int/byte/g"
     */
    private void precomputeFloats(byte b) {
        // avoid signed zeroes problem
        if (Byte.MIN_VALUE < b && b != 0 && b < Byte.MAX_VALUE) {
            if (getComputedInts()[0] && getComputedInts()[2] && byteValues[0] == byteValues[2]) {
                assert byteValues[0] == b && byteValues[1] == b;
                float sAsFloat = (float) b;
                computed(0, sAsFloat);
                computed(1, sAsFloat);
                computed(2, sAsFloat);
            }
        }
    }

    /**
     * sed precomputeLongs(int idx, int i) -e "b/int/short/g"
     */
    private void precomputeLongs(int idx, byte b) {
        if (Byte.MIN_VALUE < b && b < Byte.MAX_VALUE) {
            computed(idx, (long) b);
        }
    }

    /**
     * sed precomputeLongs(int idx, short b) -e "b/long/int/g"
     */
    private void precomputeInts(int idx, byte b) {
        if (Byte.MIN_VALUE < b && b < Byte.MAX_VALUE) {
            computed(idx, (int) b);
        }
    }

    private void precomputeShorts(int idx, byte b) {
        if (Byte.MIN_VALUE < b && b < Byte.MAX_VALUE) {
            computed(idx, (short) b);
        }
    }

    /**
     * sed precomputeInts(int idx, int b) -e "b/int/short/g"
     */
    private void precomputeBytes(int idx, byte b) {
        switch (idx) {
            case 0:
                assert !getComputedBytes()[1] || !getComputedBytes()[2]
                        || byteValues[1] == byteValues[2]
                        : "should have been computed " + byteValues[1];
                assert !getComputedBytes()[1] || byteValues[1] > Byte.MIN_VALUE
                        : "should have been computed " + Byte.MIN_VALUE;

                if (getComputedBytes()[1] && b < byteValues[1]) {
                    computed(2, byteValues[1]);
                } else {
                    if (getComputedBytes()[2] && b == byteValues[2]) {
                        computed(1, b);
                    }
                }

                break;
            case 1:
                assert !getComputedBytes()[0] || !getComputedBytes()[2]
                        || byteValues[0] != byteValues[2]
                        : "should have been computed " + byteValues[0];

                if (getComputedBytes()[0] && byteValues[0] < b) {
                    computed(2, b);
                } else {
                    if (b == Byte.MAX_VALUE) {
                        computed(2, Byte.MAX_VALUE);
                    }
                }

                break;
            case 2:
                assert !getComputedBytes()[0] || !getComputedBytes()[1]
                        || byteValues[0] == byteValues[1]
                        : "should have been computed " + byteValues[1];
                assert !getComputedBytes()[1] || byteValues[1] < Byte.MAX_VALUE
                        : "should have been computed " + Byte.MAX_VALUE;

                if (getComputedBytes()[1] && byteValues[1] < b) {
                    computed(0, byteValues[1]);
                } else {
                    if (getComputedBytes()[0] && byteValues[0] == b) {
                        computed(1, b);
                    }
                }

                break;
            default:
                throw new InternalError();
        }
    }

}
