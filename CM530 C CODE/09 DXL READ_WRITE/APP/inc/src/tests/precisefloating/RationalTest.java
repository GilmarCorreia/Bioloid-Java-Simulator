package tests.precisefloating;

import junit.framework.TestCase;
import precisefloating.DivideByZeroException;
import precisefloating.Formulas;
import precisefloating.Rational;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class RationalTest extends TestCase {

    Rational p, q, r;
    BigInteger f;

    public void testDouble() {
        Rational r, q;

        r = Rational.create(Double.MIN_VALUE);
        q = r.inverse().inverse();

        assertEquals(r, q);
        assertEquals(BigInteger.ONE, q.getNumerator());
        assertEquals(BigInteger.ONE.shiftLeft(1074), q.getDenominator());

        r = Rational.DOUBLE_MIN_NORMAL.subtract(Rational.DOUBLE_MIN_VALUE);
        assertEquals(Rational.DOUBLE_MAX_SUBNORMAL, r);
    }

    public void testCompareTo() {
        Rational p, q;

        p = Rational.create(Double.MIN_VALUE);
        q = p.inverse().inverse();

        assertEquals(0, p.compareTo(q));

        p = Rational.create(Double.MIN_VALUE);
        q = Rational.valueOf(0);
        assertEquals(1, p.compareTo(q));

        p = Rational.create(-Double.MIN_VALUE);
        q = Rational.create(-0.0);
        assertEquals(-1, p.compareTo(q));

        p = Rational.create(-0.0);
        q = Rational.create(0.0);
        assertEquals(0, p.compareTo(q));
    }

    public void testPower() {
        Rational p;

        p = Rational.create(Double.MIN_VALUE).power(0);
        assertEquals(Rational.ONE, p);

        p = Rational.create(-Double.MIN_VALUE).power(-1);
        assertEquals(Rational.valueOf(BigInteger.ONE.shiftLeft(1074).negate()), p);

        p = Rational.create(-Double.MIN_VALUE).power(0);
        assertEquals(Rational.ONE, p);

        try {
            Rational.valueOf(0).power(-1);
            fail("DivideByZeroException should have been thrown");
        } catch (DivideByZeroException zeroEx) {
        }
    }

    public void testFloor() {
        p = Rational.valueOf(-1);
        f = p.floor();
        assertEquals(BigInteger.valueOf(-1), f);

        p = Rational.create(-1, 2);
        f = p.floor();
        assertEquals(BigInteger.valueOf(-1), f);

        p = Rational.ZERO;
        f = p.floor();
        assertEquals(BigInteger.ZERO, f);

        p = Rational.create(1, 2);
        f = p.floor();
        assertEquals(BigInteger.ZERO, f);

        p = Rational.ONE;
        f = p.floor();
        assertEquals(BigInteger.ONE, f);
    }

    public void testFloorSpeed() {
        r = Rational.create(Double.MAX_VALUE);

        final int n = Integer.MAX_VALUE / 10;

        for (int i = 0; i < n; i++) {
            r.floor();
        }

        r = Rational.create(Double.MIN_VALUE);

        for (int i = 0; i < n; i++) {
            r.floor();
        }
    }

    public void testLog2Inteval() {
        int[] a = new int[2];

        r = Rational.create(Double.MIN_VALUE);
        r.log2Interval(a);
        assertEquals(Formulas.DOUBLE_MIN_TWO_EXPONENT, a[0]);
        assertEquals(Formulas.DOUBLE_MIN_TWO_EXPONENT, a[1]);

        r = Rational.create(Formulas.previous(0.5));
        r.log2Interval(a);
        assertEquals(-2, a[0]);
        assertEquals(-1, a[1]);

        r = Rational.create(0.5);
        r.log2Interval(a);
        assertEquals(-1, a[0]);
        assertEquals(-1, a[1]);

        r = Rational.create(Formulas.next(0.5));
        r.log2Interval(a);
        assertEquals(-1, a[0]);
        assertEquals(0, a[1]);

        r = Rational.create(Double.MAX_VALUE);
        r.log2Interval(a);
        assertEquals(Formulas.DOUBLE_MAX_TWO_EXPONENT, a[0]);
        assertEquals(Formulas.DOUBLE_MAX_TWO_EXPONENT + 1, a[1]);
    }

    public void testMultiplyTwoPower() {
        r = Rational.create(1, 2);
        r = r.multiplyTwoPower(1);
        assertEquals(BigInteger.ONE, r.getNumerator());
        assertEquals(BigInteger.ONE, r.getDenominator());

        r = Rational.create(1, 4);
        r = r.multiplyTwoPower(3);
        assertEquals(BigInteger.valueOf(2), r.getNumerator());
        assertEquals(BigInteger.ONE, r.getDenominator());

        r = Rational.create(-8, 5);
        r = r.multiplyTwoPower(-2);
        assertEquals(BigInteger.valueOf(-2), r.getNumerator());
        assertEquals(BigInteger.valueOf(5), r.getDenominator());

        r = Rational.create(-3, 20);
        r = r.multiplyTwoPower(3);
        assertEquals(BigInteger.valueOf(-6), r.getNumerator());
        assertEquals(BigInteger.valueOf(5), r.getDenominator());
    }

    public void testReduced() {
        assertTrue(Rational.ZERO.reduced() == Rational.ZERO);
    }

}
