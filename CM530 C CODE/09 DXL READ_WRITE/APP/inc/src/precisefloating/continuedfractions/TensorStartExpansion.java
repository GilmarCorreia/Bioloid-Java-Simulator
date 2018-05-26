package precisefloating.continuedfractions;

import java.math.BigInteger;
import java.util.logging.Logger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class TensorStartExpansion extends ContinuedFraction {

    private final BigInteger a;
    private final BigInteger b;
    private final BigInteger c;
    private final BigInteger d;
    private final BigInteger e;
    private final BigInteger f;
    private final BigInteger g;
    private final BigInteger h;
    private final ContinuedFraction x, y;

    private static final Logger log = Logger.getLogger(TensorStartExpansion.class.getName());

    public BigInteger getA() {
        return a;
    }

    public BigInteger getB() {
        return b;
    }

    public BigInteger getC() {
        return c;
    }

    public BigInteger getD() {
        return d;
    }

    public BigInteger getE() {
        return e;
    }

    public BigInteger getF() {
        return f;
    }

    public BigInteger getG() {
        return g;
    }

    public BigInteger getH() {
        return h;
    }

    public TensorStartExpansion(long a, long b, long c, long d,
            long e, long f, long g, long h,
            ContinuedFraction x, ContinuedFraction y) {
        this(BigInteger.valueOf(a), BigInteger.valueOf(b), BigInteger.valueOf(c), BigInteger.valueOf(d),
                BigInteger.valueOf(e), BigInteger.valueOf(f), BigInteger.valueOf(g), BigInteger.valueOf(h),
                x, y);
    }

    /**
     * @param a nonnegative
     * @param b nonnegative
     * @param c nonnegative
     * @param d nonnegative
     * @param e nonnegative
     * @param f nonnegative
     * @param g nonnegative
     * @param h nonnegative
     * @param x If negative, this constructor or some call to next() in either iterator
     *          throws ArithmeticException.
     * @param y If negative, this constructor or some call to next() in either iterator
     *          throws ArithmeticException.
     * @throws IllegalArgumentException either a, b, c or d is negative
     */
    public TensorStartExpansion(BigInteger a, BigInteger b, BigInteger c, BigInteger d,
            BigInteger e, BigInteger f, BigInteger g, BigInteger h,
            ContinuedFraction x, ContinuedFraction y) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
        this.g = g;
        this.h = h;

        this.x = x;
        this.y = y;
    }

    public PartialQuotients partialQuotients() {
        return new TensorStartPartialQuotients(a, b, c, d, e, f, g, h,
                x.partialQuotients(), y.partialQuotients());
    }

}
