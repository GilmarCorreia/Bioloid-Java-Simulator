package precisefloating.continuedfractions;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class ERadicalPartialQuotients extends InfinitePartialQuotients
        implements ScrollableIterator, Cloneable, Serializable {

    private final BigInteger p;

    /** p * 2. */
    private final BigInteger p2;

    private final BigInteger minusOneMinusP;

    private static final Logger log = Logger.getLogger(ERadicalPartialQuotients.class.getName());

    public BigInteger getP() {
        return p;
    }

    public ERadicalPartialQuotients(long p) {
        this(BigInteger.valueOf(p));
    }

    public ERadicalPartialQuotients(BigInteger p) {
        if (p.compareTo(BigInteger.valueOf(2)) < 0) {
            throw new IllegalArgumentException("The radical directionIndex must be at least 2");
        }

        this.p = p;
        minusOneMinusP = BigInteger.valueOf(-1).subtract(p);
        p2 = p.shiftLeft(1);
    }

    private byte b;

    /** The initial value is -p - 1. */
    private BigInteger x;

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new InternalError("impossible as we do implement Cloneable");
        }
    }

    public String toString() {
        return "radical(e, " + p + ")";
    }

    protected BigInteger computeNext() {
        BigInteger next;

        if (!isStarted()) {
            next = BigInteger.ONE;
            x = minusOneMinusP;
        } else {
            if (b == 1) {
                next = BigInteger.ONE;
                b = 2;
            } else {
                if (b == 2) {
                    next = BigInteger.ONE;
                    b = 0;
                } else {
                    assert b == 0;

                    x = x.add(p2);
                    next = x;
                    b = 1;
                }
            }

        }

        if (log.isLoggable(Level.FINEST)) {
            log.finest(this + " " + next);
        }

        return next;
    }

    public void setIndex(long index) {
        if (index != getIndex()) {

            if (index < -1) {
                throw new IllegalArgumentException("directionIndex must be at least -1 but it is " + index);
            }

            super.index = index;

            if (index == -1) {
                b = 0;
                x = null;
                super.started = false;
            } else {
                assert index >= 0;

                b = (byte) (index % 3);

                if (b == 0) {
                    x = minusOneMinusP.add(p2.multiply(BigInteger.valueOf(index / 3)));
                } else {
                    x = minusOneMinusP.add(p2.multiply(BigInteger.valueOf(index / 3 + 1)));
                }

                super.started = true;
            }
        }
    }

}
