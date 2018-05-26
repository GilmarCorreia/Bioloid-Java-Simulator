package precisefloating.directedrounding;

import precisefloating.directedrounding.RoundingMode;
import precisefloating.directedrounding.RoundNumberBase;

import java.math.BigInteger;

/**
 * Thread safe class. Can be used as superclass for immutable thread safe numbers. Supports a
 * rounding direction that is only set in the constructor and cannot be changed. Superclass for
 * the immutable classes Rational and ContinuedFraction.
 *
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public abstract class DirectedNumber extends Number {

    private final RoundingMode defaultRoundingMode;

    protected DirectedNumber(RoundingMode defaultRoundingMode) {
        if (defaultRoundingMode == null) {
            throw new NullPointerException("defaultRoundingMode can't be null");
        }

        this.defaultRoundingMode = defaultRoundingMode;
    }

    private RoundNumberBase getRoundNumber() {
        if (roundNumber == null) {
            synchronized (this) {
                if (roundNumber == null) {
                    // three rounding modes
                    roundNumber = createRoundNumber();
                }
            }
        }

        return roundNumber;
    }

    /**
     * Lazily initialized. Even if the rounding direction is mutable, it is completely hidden
     * from the outer world.
     */
    private RoundNumberBase roundNumber;

    /**
     * This method is called in a synchronized context.
     *
     * @return the freshly created RoundNumberBase instance
     */
    protected abstract RoundNumberBase createRoundNumber();

    public double doubleValue() {
        return doubleValue(defaultRoundingMode);
    }

    public float floatValue() {
        return floatValue(defaultRoundingMode);
    }

    public BigInteger bigIntegerValue() {
        return bigIntegerValue(defaultRoundingMode);
    }

    public long longValue() {
        return longValue(defaultRoundingMode);
    }

    public int intValue() {
        return intValue(defaultRoundingMode);
    }

    public short shortValue() {
        return shortValue(defaultRoundingMode);
    }

    public byte byteValue() {
        return byteValue(defaultRoundingMode);
    }

    public double doubleValue(RoundingMode mode) {
        getRoundNumber();

        synchronized (roundNumber) {
            roundNumber.setRoundingMode(mode);
            return roundNumber.doubleValue();
        }
    }

    public float floatValue(RoundingMode mode) {
        getRoundNumber();

        synchronized (roundNumber) {
            roundNumber.setRoundingMode(mode);
            return roundNumber.floatValue();
        }
    }

    public BigInteger bigIntegerValue(RoundingMode mode) {
        getRoundNumber();

        synchronized (roundNumber) {
            roundNumber.setRoundingMode(mode);
            return roundNumber.bigIntegerValue();
        }
    }

    public long longValue(RoundingMode mode) {
        getRoundNumber();

        synchronized (roundNumber) {
            roundNumber.setRoundingMode(mode);
            return roundNumber.longValue();
        }
    }

    public int intValue(RoundingMode mode) {
        getRoundNumber();

        synchronized (roundNumber) {
            roundNumber.setRoundingMode(mode);
            return roundNumber.intValue();
        }
    }

    public short shortValue(RoundingMode mode) {
        getRoundNumber();

        synchronized (roundNumber) {
            roundNumber.setRoundingMode(mode);
            return roundNumber.shortValue();
        }
    }

    public byte byteValue(RoundingMode mode) {
        getRoundNumber();

        synchronized (roundNumber) {
            roundNumber.setRoundingMode(mode);
            return roundNumber.byteValue();
        }
    }

}
