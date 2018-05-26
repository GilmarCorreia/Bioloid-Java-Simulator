package precisefloating.continuedfractions;

import java.math.BigInteger;
import java.util.logging.Logger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class MatrixStartExpansion extends ContinuedFraction {

    private final BigInteger a, b, c, d;
    private final ContinuedFraction x;

    private static final Logger log = Logger.getLogger(MatrixStartExpansion.class.getName());

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

    public ContinuedFraction getX() {
        return x;
    }

    public MatrixStartExpansion(long a, long b, long c, long d, ContinuedFraction x) {
        this(BigInteger.valueOf(a), BigInteger.valueOf(b),
                BigInteger.valueOf(c), BigInteger.valueOf(d), x);
    }

    /**
     * @param a nonnegative
     * @param b nonnegative
     * @param c nonnegative
     * @param d nonnegative
     * @param x If negative, the first call to next() in either iterator throws ArithmeticException.
     * @throws IllegalArgumentException either a, b, c or d is negative
     */
    public MatrixStartExpansion(BigInteger a, BigInteger b, BigInteger c, BigInteger d,
            ContinuedFraction x) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;

        this.x = x;
    }

    private static void checkNonNegative(BigInteger d, String s) {
        if (d.signum() == -1) {
            throw new IllegalArgumentException(s + " must be noonnegative but its value is " + d);
        }
    }

    public PartialQuotients partialQuotients() {
        return new MatrixStartPartialQuotients(a, b, c, d, x.partialQuotients());
    }

}
