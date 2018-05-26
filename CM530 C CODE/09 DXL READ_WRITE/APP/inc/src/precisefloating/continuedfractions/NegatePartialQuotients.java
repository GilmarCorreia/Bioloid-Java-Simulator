package precisefloating.continuedfractions;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class NegatePartialQuotients extends PartialQuotients {

    private final PartialQuotients xIterator;
    BigInteger x, y;

    protected BigInteger computeNext() {
        if (!isStarted()) {
            assert getIndex() == -1;

            BigInteger a = xIterator.nextPartialQuotient();
            if (!xIterator.hasNext()) {
                // integer a
                // x = y = null
                return a.negate();
            } else {
                BigInteger b = xIterator.nextPartialQuotient();

                if (!xIterator.hasNext()) {
                    // a + 1/b
                    if (b.equals(BigInteger.valueOf(2))) {
                        // x = 2; y = null
                        x = BigInteger.valueOf(2);
                    } else {
                        // x = 1; y = b - 1
                        x = BigInteger.ONE;
                        y = b.subtract(BigInteger.ONE);
                    }
                } else {
                    if (b.compareTo(BigInteger.ONE) > 0) {
                        // x = 1; y = b - 1
                        x = BigInteger.ONE;
                        y = b.subtract(BigInteger.ONE);
                    } else {
                        assert b.equals(BigInteger.ONE);

                        if (!xIterator.hasNext()) {
                            ContinuedFraction.illegalLastPartialQuotient();
                        } else {
                            // x = c + 1
                            BigInteger c = xIterator.nextPartialQuotient();
                            x = c.add(BigInteger.ONE);
                        }
                    }
                }

                return a.negate().subtract(BigInteger.ONE);
            }
        } else {
            if (x == null) {
                if (y == null) {
                    return xIterator.nextPartialQuotient();
                } else {
                    BigInteger temp = y;
                    y = null;
                    return temp;
                }
            } else {
                if (getIndex() == 0) {
                    BigInteger temp = x;
                    x = null;
                    return temp;
                } else {
                    assert x == null;

                    if (getIndex() == 1) {
                        if (y == null) {
                            return xIterator.nextPartialQuotient();
                        } else {
                            BigInteger temp = y;
                            y = null;
                            return temp;
                        }
                    } else {
                        assert x == null && y == null;
                        return xIterator.nextPartialQuotient();
                    }
                }
            }
        }
    }

    public boolean hasNext() {
        return x != null || y != null || xIterator.hasNext();
    }

    /**
     * @param xIterator May already be started or not, but it must have a next element.
     */
    public NegatePartialQuotients(PartialQuotients xIterator) {
        if (!xIterator.hasNext()) {
            throw new IllegalStateException("xIterator must still have at least one element");
        }

        this.xIterator = xIterator;
    }

}
