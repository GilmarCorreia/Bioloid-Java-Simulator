package precisefloating.continuedfractions;

import precisefloating.Rational;
import precisefloating.directedrounding.RoundNumber;

import java.math.BigInteger;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * All the methods are guaranteed to finish in a finite number of steps.
 *
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class RoundContinuedFraction extends RoundNumber {

    private final ContinuedFraction continuedFraction;

    private static final Logger log = Logger.getLogger(RoundContinuedFraction.class.getName());

    public ContinuedFraction getContinuedFraction() {
        return continuedFraction;
    }

    public RoundContinuedFraction(ContinuedFraction continuedFraction) {
        this.continuedFraction = continuedFraction;
    }

    protected double computeDoubleValue() {
        double d = Double.NaN;

        Convergents convergents = continuedFraction.convergents();

        double previousValue = Double.NaN;

        do {
            if (log.isLoggable(Level.FINEST)) {
                log.finest("convergent index " + convergents.getIndex());
            }

            assert Double.isNaN(d) && convergents.hasNext() : "d should have been computed already";
            assert Double.isNaN(previousValue) ^ convergents.isStarted();

            Rational current = convergents.nextConvergent();

            if (log.isLoggable(Level.FINEST)) {
                double currentValue = current.doubleValue(getRoundingMode());
                log.finest(currentValue + " - " + previousValue + " = "
                        + (currentValue - previousValue));
            }

            if (!convergents.hasNext()) {
                d = current.doubleValue(getRoundingMode());
            } else {
                assert convergents.hasNext();
                // note that convergents.getIndex() may overflow, but it always has the correct parity

                final long index = convergents.getIndex();

                switch (directionIndex()) {
                    case 0:
                        if ((index & 1) == 0
                            && current.compareTo(Rational.DOUBLE_MAX_VALUE) >= 0) {
                            d = Double.MAX_VALUE;
                        } else {
                            if ((index & 1) == 1 && current.compareTo(Rational.DOUBLE_MAX_VALUE.negate()) <= 0) {
                                d = Double.NEGATIVE_INFINITY;
                            } else {
                                double currentValue = current.doubleValue(getRoundingMode());
                                // also works for previousValue = NaN
                                if (previousValue == currentValue) {
                                    d = currentValue;
                                } else {
                                    previousValue = currentValue;
                                }
                            }
                        }
                        break;
                    case 1:
                        if ((index & 1) == 0
                            && current.compareTo(Rational.DOUBLE_MAX_NEAREST_THRESHOLD) >= 0) {
                            d = Double.POSITIVE_INFINITY;
                        } else {
                            if ((index & 1) == 0 && current.compareTo(
                                    Rational.DOUBLE_MAX_NEAREST_THRESHOLD.negate()) <= 0) {
                                d = Double.NEGATIVE_INFINITY;
                            } else {
                                double currentValue = current.doubleValue(getRoundingMode());
                                // also works for previousValue = NaN
                                if (previousValue == currentValue) {
                                    d = currentValue;
                                } else {
                                    previousValue = currentValue;
                                }
                            }
                        }
                        break;
                    case 2:
                        if ((index & 1) == 1
                            && current.compareTo(Rational.DOUBLE_MAX_VALUE.negate()) <= 0) {
                            d = -Double.MAX_VALUE;
                        } else {
                            if ((index & 1) == 0 && current.compareTo(Rational.DOUBLE_MAX_VALUE) >= 0) {
                                d = Double.POSITIVE_INFINITY;
                            } else {
                                double currentValue = current.doubleValue(getRoundingMode());
                                // also works for previousValue = NaN
                                if (previousValue == currentValue) {
                                    d = currentValue;
                                } else {
                                    previousValue = currentValue;
                                }
                            }
                        }
                        break;
                    default:
                        throw new InternalError("illegal direction index " + directionIndex());
                }
            }
        } while (Double.isNaN(d));

        return d;
    }

    protected float computeFloatValue() {
        float f = Float.NaN;

        Convergents convergents = continuedFraction.convergents();

        float previousValue = Float.NaN;

        do {
            if (log.isLoggable(Level.FINEST)) {
                log.finest("convergent index " + convergents.getIndex());
            }

            assert Float.isNaN(f) && convergents.hasNext() : "f should have been computed already";
            assert Float.isNaN(previousValue) ^ convergents.isStarted();

            Rational current = convergents.nextConvergent();

            if (log.isLoggable(Level.FINEST)) {
                float currentValue = current.floatValue(getRoundingMode());
                log.finest(currentValue + " - " + previousValue + " = "
                        + (currentValue - previousValue));
            }

            if (!convergents.hasNext()) {
                f = current.floatValue(getRoundingMode());
            } else {
                assert convergents.hasNext();
                // note that convergents.getIndex() may overflow, but it always has the correct parity

                final long index = convergents.getIndex();

                switch (directionIndex()) {
                    case 0:
                        if ((index & 1) == 0
                            && current.compareTo(Rational.FLOAT_MAX_VALUE) >= 0) {
                            f = Float.MAX_VALUE;
                        } else {
                            if ((index & 1) == 1 && current.compareTo(Rational.FLOAT_MAX_VALUE.negate()) >= 0) {
                                f = Float.NEGATIVE_INFINITY;
                            } else {
                                float currentValue = current.floatValue(getRoundingMode());
                                // also works for previousValue = NaN
                                if (previousValue == currentValue) {
                                    f = currentValue;
                                } else {
                                    previousValue = currentValue;
                                }
                            }
                        }
                        break;
                    case 1:
                        if ((index & 1) == 0
                            && current.compareTo(Rational.FLOAT_MAX_NEAREST_THRESHOLD) >= 0) {
                            f = Float.POSITIVE_INFINITY;
                        } else {
                            if ((index & 1) == 0 && current.compareTo(
                                    Rational.FLOAT_MAX_NEAREST_THRESHOLD.negate()) <= 0) {
                                f = Float.NEGATIVE_INFINITY;
                            } else {
                                float currentValue = current.floatValue(getRoundingMode());
                                // also works for previousValue = NaN
                                if (previousValue == currentValue) {
                                    f = currentValue;
                                } else {
                                    previousValue = currentValue;
                                }
                            }
                        }
                        break;
                    case 2:
                        if ((index & 1) == 1
                            && current.compareTo(Rational.FLOAT_MAX_VALUE.negate()) <= 0) {
                            f = -Float.MAX_VALUE;
                        } else {
                            if ((index & 1) == 0 && current.compareTo(Rational.FLOAT_MAX_VALUE) >= 0) {
                                f = Float.POSITIVE_INFINITY;
                            } else {
                                float currentValue = current.floatValue(getRoundingMode());
                                // also works for previousValue = NaN
                                if (previousValue == currentValue) {
                                    f = currentValue;
                                } else {
                                    previousValue = currentValue;
                                }
                            }
                        }
                        break;
                    default:
                        throw new InternalError("illegal direction index " + directionIndex());
                }
            }
        } while (Float.isNaN(f));

        return f;
    }

    protected BigInteger computeBigIntegerValue() {
        final BigInteger v;

        switch (directionIndex()) {
            case 0:
                v = continuedFraction.floor();
                break;
            case 1:
                PartialQuotients partialQuotients = continuedFraction.partialQuotients();
                BigInteger floor = partialQuotients.nextPartialQuotient();
                if (!partialQuotients.hasNext()) {
                    v = floor;
                } else {
                    BigInteger second = partialQuotients.nextPartialQuotient();
                    if (second.compareTo(BigInteger.valueOf(2)) == +1) {
                        v = floor;
                    } else {
                        if (second.equals(BigInteger.valueOf(2))) {
                            if (partialQuotients.hasNext()) {
                                v = floor;
                            } else {
                                if (floor.testBit(0)) {
                                    v = floor.add(BigInteger.ONE);
                                } else {
                                    v = floor;
                                }
                            }
                        } else {
                            assert second.equals(BigInteger.ONE) && partialQuotients.hasNext();
                            v = floor;
                        }
                    }
                }
                break;
            case 2:
                v = continuedFraction.ceil();
                break;
            default:
                throw new InternalError();
        }

        return v;
    }

}
