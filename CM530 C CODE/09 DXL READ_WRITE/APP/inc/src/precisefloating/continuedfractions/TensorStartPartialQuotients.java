package precisefloating.continuedfractions;

import precisefloating.DivideByZeroException;
import precisefloating.Rational;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Alternatively calls nextPartialQuotient on the continued fractions to multiply, until either of
 * them finishes. If the two continued fractions both have infinite length, or both have the same
 * length, the result is that all the calls to nextPartialQuotient are alternated. The alternation
 * is in the the sense that the number of calls on one continued fraction is always equal to
 * the number of calls on the other, with an error of at most one call.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
class TensorStartPartialQuotients extends PartialQuotients {

    PartialQuotients xPartialQuotients, yPartialQuotients, mPartialQuotients;

    private static final Logger log = Logger.getLogger(TensorStartPartialQuotients.class.getName());

    public TensorStartPartialQuotients(long a, long b, long c, long d,
            long e, long f, long g, long h,
            PartialQuotients xPartialQuotients, PartialQuotients yPartialQuotients) {
        this(BigInteger.valueOf(a), BigInteger.valueOf(b), BigInteger.valueOf(c), BigInteger.valueOf(d),
                BigInteger.valueOf(e), BigInteger.valueOf(f), BigInteger.valueOf(g), BigInteger.valueOf(h),
                xPartialQuotients, yPartialQuotients);
    }

    public TensorStartPartialQuotients(BigInteger a, BigInteger b, BigInteger c, BigInteger d,
            BigInteger e, BigInteger f, BigInteger g, BigInteger h,
            PartialQuotients xPartialQuotients, PartialQuotients yPartialQuotients) {
        u = a;
        v = b;
        w = c;
        z = d;
        p = e;
        q = f;
        r = g;
        s = h;

        logState("constructor");

        this.xPartialQuotients = xPartialQuotients;
        this.yPartialQuotients = yPartialQuotients;
    }

    /**
     * Very cheap operation.
     */
    public boolean hasNext() {
        if (mPartialQuotients != null) {
            return mPartialQuotients.hasNext();
        } else {
            return true;
        }
    }

    BigInteger y0, u, v, w, z, p, q, r, s;

    public BigInteger computeNext() {
        assert (!isStarted() && xPartialQuotients.hasNext() && yPartialQuotients.hasNext() && mPartialQuotients == null)
                || (isStarted() && (xPartialQuotients != null && yPartialQuotients != null && mPartialQuotients == null)
                || (xPartialQuotients == null && yPartialQuotients == null && mPartialQuotients.isStarted()));

        if (mPartialQuotients != null) {
            assert y0 == null;
            assert xPartialQuotients == null && yPartialQuotients == null;
            assert mPartialQuotients.isStarted();
            assert mPartialQuotients.hasNext();
            return mPartialQuotients.nextPartialQuotient();
        } else {
            if (!isStarted()) {
                y0 = yPartialQuotients.nextPartialQuotient();

                if (yPartialQuotients.hasNext()) {
                    // x0 will be eventually put back
                    BigInteger x0 = xPartialQuotients.nextPartialQuotient();

                    if (!xPartialQuotients.hasNext()) {
                        // switch x and y
                        PartialQuotients aux;

                        yPartialQuotients = new BeforePartialQuotients(y0, yPartialQuotients);

                        aux = xPartialQuotients;
                        xPartialQuotients = yPartialQuotients;
                        yPartialQuotients = aux;

                        y0 = x0;

                        integerInput();
                        return mPartialQuotients.nextPartialQuotient();
                    } else {
                        // neither x nor y is integer

                        handleX(x0);
                        handleY();
                    }
                } else {
                    integerInput();
                    return mPartialQuotients.nextPartialQuotient();
                }
            }

            for (long i = 0; ; i++) {
                logStep(i);

                if (!yPartialQuotients.hasNext()) {
                    assert notOppositeSigns(p, p.add(q), p.add(r), p.add(q).add(r.add(s)));
                    integerInput();
                    return mPartialQuotients.nextPartialQuotient();
                } else {
                    BigInteger paddq = p.add(q), paddr = p.add(r), paddqrs = paddq.add(r.add(s));
                    assert notOppositeSigns(p, paddq, paddr, paddqrs);

                    BigInteger digit;

                    if (p.signum() != 0 && paddq.signum() != 0 && paddr.signum() != 0 && paddqrs.signum() != 0) {
                        BigInteger uaddv = u.add(v), uaddw = u.add(w), uaddvwz = uaddv.add(w.add(z));

                        Rational up = Rational.create(u, p), uvpq = Rational.create(uaddv, paddq),
                                uwpr = Rational.create(uaddw, paddr), uvwzpqrs = Rational.create(uaddvwz, paddqrs);
                        digit = sameFloor(up, uvpq, uwpr, uvwzpqrs);
                    } else {
                        digit = null;
                    }

                    if (digit != null) {
                        log.fine("output digit " + digit);
                        // keeps the same position in y and lets x0 unchanged
                        transformTensorForOutput(digit);
                        return digit;
                    } else {
                        transformTensorForInput(y0);

                        PartialQuotients aux = xPartialQuotients;
                        xPartialQuotients = yPartialQuotients;
                        yPartialQuotients = aux;

                        y0 = yPartialQuotients.nextPartialQuotient();
                    }
                }
            }
        }
    }

    private void integerInput() {
        assert xPartialQuotients.hasNext();
        yPartialQuotients = null;
        mPartialQuotients = new MatrixStartPartialQuotients(
                u.multiply(y0).add(v), w.multiply(y0).add(z),
                p.multiply(y0).add(q), r.multiply(y0).add(s), xPartialQuotients);
        xPartialQuotients = null;
        y0 = null;
    }

    private static boolean notOppositeSigns(BigInteger a0, BigInteger a1, BigInteger a2, BigInteger a3) {
        int sgn = a0.signum();

        if (a1.signum() != 0) {
            if (sgn * a1.signum() < 0) {
                return false;
            } else {
                sgn = a1.signum();
            }
        }

        if (a2.signum() != 0) {
            if (sgn * a2.signum() < 0) {
                return false;
            } else {
                sgn = a2.signum();
            }
        }

        if (a3.signum() != 0) {
            if (sgn * a3.signum() < 0) {
                return false;
            } else {
                sgn = a3.signum();
            }
        }

        if (sgn == 0) {
            return false;
        }

        return true;
    }

    private static void chechNotOpositeSigns(BigInteger a0, BigInteger a1, BigInteger a2, BigInteger a3) {
        int sgn = a0.signum();

        sgn = checkSign(a1, sgn);
        sgn = checkSign(a2, sgn);
        sgn = checkSign(a3, sgn);

        if (sgn == 0) {
            throw new DivideByZeroException();
        }
    }

    private static int checkSign(BigInteger signed, int sgn) {
        if (signed.signum() != 0) {
            if (sgn * signed.signum() < 0) {
                throw new ArithmeticException("in step 3 e, e + f, e + g, e + f + g + h "
                        + "must not have opposite signs");
            } else {
                // sgn may be zero
                return signed.signum();
            }
        } else {
            return sgn;
        }
    }

    private void handleY() {
        if (y0.signum() == -1) {
            // y < 0
            u = u.negate();
            w = w.negate();
            p = p.negate();
            r = r.negate();

            yPartialQuotients = new NegatePartialQuotients(
                    new BeforePartialQuotients(y0, yPartialQuotients));

            BigInteger old = y0;
            y0 = yPartialQuotients.nextPartialQuotient();
            assert y0.equals(old.negate().subtract(BigInteger.ONE));
        }

        if (y0.signum() == 0) {
            // 0 < x < 1

            BigInteger a = u, b = v, c = w, d = z, e = p, f = q, g = r, h = s;

            u = b;
            v = a;
            w = d;
            z = c;
            p = f;
            q = e;
            r = h;
            s = g;

            // take one over y
            y0 = yPartialQuotients.nextPartialQuotient();
        }
    }

    private void handleX(BigInteger x0) {
        boolean xStarted = true;

        if (x0.signum() == -1) {
            // x < 0
            u = u.negate();
            v = v.negate();
            p = p.negate();
            q = q.negate();

            xPartialQuotients = new NegatePartialQuotients(
                    new BeforePartialQuotients(x0, xPartialQuotients));
            xStarted = false;

            // do not start the first partial quotient from the iterator
            x0 = x0.negate().subtract(BigInteger.ONE);
        }

        if (x0.signum() == 0) {
            // 0 < x < 1
            BigInteger a = u, b = v, c = w, d = z, e = p, f = q, g = r, h = s;

            u = c;
            v = d;
            w = a;
            z = b;
            p = g;
            q = h;
            r = e;
            s = f;

            // 1/x
            if (!xStarted) {
                x0 = xPartialQuotients.nextPartialQuotient();
            } else {
                xStarted = false;
            }
        }

        if (xStarted) {
            xPartialQuotients = new BeforePartialQuotients(x0, xPartialQuotients);
            xStarted = false;
        }

        assert !xStarted;
    }

    private static BigInteger sameFloor(Rational up, Rational vq, Rational wr, Rational zs) {
        BigInteger fl = up.floor();
        if (vq.isFloorEqualTo(fl) && wr.isFloorEqualTo(fl) && zs.isFloorEqualTo(fl)) {
            return fl;
        } else {
            return null;
        }
    }

    private static void logStep(long i) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("step " + i);
        }
    }

    private void transformTensorForInput(BigInteger y0) {
        // temporary variables
        BigInteger a = u, b = v, c = w, d = z, e = p, f = q, g = r, h = s;

        logState("before transform for input");

        // seems ok
        u = a.multiply(y0).add(b);
        v = c.multiply(y0).add(d);
        w = a;
        z = c;
        p = e.multiply(y0).add(f);
        q = g.multiply(y0).add(h);
        r = e;
        s = g;

        logState("after transform for input");
    }

    private void transformTensorForOutput(BigInteger digit) {
        logState("before transform for output");

        // temporary variables
        BigInteger a = u, b = v, c = w, d = z, e = p, f = q, g = r, h = s;

        u = e;
        v = f;
        w = g;
        z = h;
        p = a.subtract(e.multiply(digit));
        q = b.subtract(f.multiply(digit));
        r = c.subtract(g.multiply(digit));
        s = d.subtract(h.multiply(digit));

        logState("after transform for output");
    }

    private void logState(String method) {
        if (log.isLoggable(Level.FINEST)) {
            log.finest(method + " x0 = " + y0 + " [[u = " + u + " v = " + v + "] [w = " + w + " z = " + z
                    + "]] [[p = " + p + " q = " + q + "] [r = " + r + " s = " + s + "]]");
        }
    }

}
