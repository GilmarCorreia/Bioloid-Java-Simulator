package precisefloating.continuedfractions;

import precisefloating.DivideByZeroException;
import precisefloating.Rational;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
class MatrixStartPartialQuotients extends PartialQuotients {

    PartialQuotients xPartialQuotients, rPartialQuotients;

    private static final Logger log = Logger.getLogger(MatrixStartPartialQuotients.class.getName());

    public MatrixStartPartialQuotients(BigInteger a, BigInteger b, BigInteger c, BigInteger d,
            PartialQuotients xPartialQuotients) {
        u = a;
        v = b;
        w = c;
        z = d;

        logState("constructor");

        if (!xPartialQuotients.hasNext()) {
            throw new IllegalArgumentException("xPartialQuotients must have at least one element");
        }

        this.xPartialQuotients = xPartialQuotients;
    }

    /**
     * Cheap operation.
     */
    public boolean hasNext() {
        if (rPartialQuotients != null) {
            return rPartialQuotients.hasNext();
        } else {
            return true;
        }
    }

    private BigInteger x0, u, v, w, z;

    /**
     * @throws DivideByZeroException If and only if the value <code>x</code>
     *                               represented by the given continued fraction expansion is rational and satisfies
     *                               the identity <code>c * x + d = 0</code>.
     * @throws ArithmeticException   If the value to operate upon is negative.
     */
    public BigInteger computeNext() {
        assert (!isStarted() && xPartialQuotients.hasNext() && rPartialQuotients == null)
                || (isStarted() && (xPartialQuotients != null && xPartialQuotients.hasNext() && xPartialQuotients.isStarted() && rPartialQuotients == null)
                || (xPartialQuotients == null && rPartialQuotients.isStarted()));

        if (rPartialQuotients != null) {
            assert x0 == null;
            assert xPartialQuotients == null;
            assert rPartialQuotients.isStarted();
            return rPartialQuotients.nextPartialQuotient();
        } else {
            if (!isStarted()) {
                x0 = xPartialQuotients.nextPartialQuotient();

                if (xPartialQuotients.hasNext()) {
                    /*
                        If x is not an integer, it must also satisfy ad ? bc and
                        additionally, depending on its value:
                        – if x < -1 then c(c-d) ? 0
                        – if -1 < x < 0 then d(d-c) ? 0
                        – if 0 < x < 1 then d(c + d) ? 0
                        – if x > 1 then c(c+d) ? 0
                    */

                    if (u.multiply(z).equals(v.multiply(w))) {
                        throw new ArithmeticException("x is not an integer but ad = bc");
                    }

                    if (x0.signum() == -1) {
                        // x < 0
                        if (x0.compareTo(BigInteger.valueOf(-1)) < 0) {
                            // x < -1
                            if (w.multiply(w.subtract(z)).signum() == -1) {
                                throw new ArithmeticException("x < -1 but c(c - d) < 0");
                            }
                        } else {
                            // -1 < x < 0
                            assert x0.equals(BigInteger.valueOf(-1));

                            if (z.multiply(z.subtract(w)).signum() == -1) {
                                throw new ArithmeticException("-1 < x < 0 but d(d - c) < 0");
                            }
                        }
                    } else {
                        // x > 0
                        if (x0.signum() == 0) {
                            // 0 < x < 1
                            if (z.multiply(w.add(z)).signum() == -1) {
                                throw new ArithmeticException("0 < x < 1 but d(c + d) < 0");
                            }
                        } else {
                            // 1 < x
                            if (w.multiply(w.add(z)).signum() == -1) {
                                throw new ArithmeticException("1 < x but c(c + d) < 0");
                            }
                        }
                    }

                    if (x0.signum() == -1) {
                        // x < 0
                        u = u.negate();
                        w = w.negate();

                        xPartialQuotients = new NegatePartialQuotients(
                                new BeforePartialQuotients(x0, xPartialQuotients));

                        BigInteger old = x0;
                        x0 = xPartialQuotients.nextPartialQuotient();
                        assert x0.equals(old.negate().subtract(BigInteger.ONE));
                    }

                    if (x0.signum() == 0) {
                        // 0 < x < 1
                        mirrorVertically();

                        // take one over x
                        x0 = xPartialQuotients.nextPartialQuotient();
                        ContinuedFraction.checkNotFirstPartialQuotient(x0, xPartialQuotients);
                    }
                } else {
                    // integer
                    integerInput();
                    return rPartialQuotients.nextPartialQuotient();
                }
            }

            // the algorithm is guaranteed to finish at some time
            for (long i = 0; ; i++) {
                if (log.isLoggable(Level.FINE)) {
                    log.fine(Long.toString(i));
                }

                assert x0.compareTo(BigInteger.ONE) >= 0
                        && !u.multiply(z).equals(v.multiply(w)) && w.multiply(w.add(z)).signum() >= 0;

                if (!xPartialQuotients.hasNext()) {
                    integerInput();
                    return rPartialQuotients.nextPartialQuotient();
                } else {
                    BigInteger digit;

                    BigInteger waddz = w.add(z);

                    if (w.signum() != 0 && waddz.signum() != 0) {
                        assert w.signum() == waddz.signum();

                        Rational uw = Rational.create(u, w), uvwz = Rational.create(u.add(v), waddz);

                        if (uw.floor().equals(uvwz.floor())) {
                            digit = uw.floor();
                        } else {
                            digit = null;
                        }
                    } else {
                        assert w.signum() == 0 ^ waddz.signum() == 0;
                        digit = null;
                    }

                    if (digit != null) {
                        if (log.isLoggable(Level.FINE)) {
                            log.fine("output digit " + digit);
                        }
                        // keeps the same position in x and lets x0 unchanged
                        transformMatrixForOutput(digit);
                        return digit;
                    } else {
                        transformMatrixForInput(x0);
                        x0 = xPartialQuotients.nextPartialQuotient();
                    }
                }
            }
        }
    }

    private void integerInput() {
        xPartialQuotients = null;
        Rational r = Rational.create(u.multiply(x0).add(v), w.multiply(x0).add(z));
        x0 = null;
        rPartialQuotients = new RationalExpansion(r).partialQuotients();
    }

    private void mirrorVertically() {
        BigInteger temp;

        temp = u;
        u = v;
        v = temp;

        temp = w;
        w = z;
        z = temp;
    }

    private void transformMatrixForInput(BigInteger x0) {
        // temporary variables
        BigInteger a = u, b = v, c = w, d = z;

        logState("before transform for input");

        u = a.multiply(x0).add(b);
        v = a;
        w = c.multiply(x0).add(d);
        z = c;

        logState("after transform for input");
    }

    private void transformMatrixForOutput(BigInteger digit) {
        // temporary variables
        BigInteger a = u, b = v, c = w, d = z;

        logState("before transform for output");

        u = c;
        v = d;
        w = a.subtract(c.multiply(digit));
        z = b.subtract(d.multiply(digit));

        logState("after transform for output");
    }

    private void logState(String s) {
        if (log.isLoggable(Level.FINE)) {
            log.fine(s + " x0 = " + x0 + " [[u = " + u + " v = " + v + "] [w = " + w + " z = " + z + "]]");
        }
    }

}
