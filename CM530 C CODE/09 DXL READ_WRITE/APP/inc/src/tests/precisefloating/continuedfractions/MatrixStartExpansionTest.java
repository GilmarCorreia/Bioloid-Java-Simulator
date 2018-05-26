package tests.precisefloating.continuedfractions;

import junit.framework.TestCase;
import precisefloating.DivideByZeroException;
import precisefloating.Rational;
import precisefloating.continuedfractions.Convergents;
import precisefloating.continuedfractions.MatrixStartExpansion;
import precisefloating.continuedfractions.PartialQuotients;
import precisefloating.continuedfractions.RationalExpansion;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class MatrixStartExpansionTest extends TestCase {

    Rational r;
    RationalExpansion re;
    MatrixStartExpansion mse;
    PartialQuotients pqi;
    BigInteger a, b, c, d;
    Convergents convergents;
    Rational convergent;

    public void testExpansion() {
        r = Rational.ZERO;
        re = new RationalExpansion(r);
        a = BigInteger.valueOf(70);
        b = BigInteger.valueOf(29);
        c = BigInteger.valueOf(12);
        d = BigInteger.valueOf(5);
        mse = new MatrixStartExpansion(a, b, c, d, re);

        pqi = mse.partialQuotients();
        assertEquals(BigInteger.valueOf(5), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(4), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());

        convergents = mse.convergents();

        do {
            convergent = convergents.nextConvergent();
        } while (convergents.hasNext());

        assertEquals(Rational.create(29, 5), convergent);

        r = Rational.valueOf(2);
        re = new RationalExpansion(r);
        a = BigInteger.valueOf(70);
        b = BigInteger.valueOf(29);
        c = BigInteger.valueOf(12);
        d = BigInteger.valueOf(5);
        mse = new MatrixStartExpansion(a, b, c, d, re);

        pqi = mse.partialQuotients();
        assertEquals(BigInteger.valueOf(5), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(4), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(4), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());

        convergents = mse.convergents();

        do {
            convergent = convergents.nextConvergent();
        } while (convergents.hasNext());

        assertEquals(Rational.create(169, 29), convergent);

        r = Rational.create(995, 1111);
        re = new RationalExpansion(r);
        a = BigInteger.valueOf(1);
        b = BigInteger.valueOf(0);
        c = BigInteger.valueOf(0);
        d = BigInteger.valueOf(1);
        mse = new MatrixStartExpansion(a, b, c, d, re);

        // [0, 1, 8, 1, 1, 2, 1, 2, 1, 1, 2]
        pqi = mse.partialQuotients();
        assertEquals(BigInteger.valueOf(0), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(8), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(2), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(2), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(2), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());

        convergents = mse.convergents();

        do {
            convergent = convergents.nextConvergent();
        } while (convergents.hasNext());

        assertEquals(Rational.create(995, 1111), convergent);
    }

    public void testSmallValue() {
        r = Rational.create(1, 10);
        re = new RationalExpansion(r);
        a = BigInteger.ONE;
        b = BigInteger.valueOf(10);
        c = BigInteger.valueOf(3);
        d = BigInteger.valueOf(9);
        mse = new MatrixStartExpansion(a, b, c, d, re);

        // [1, 11, 1, 1, 1, 2]
        pqi = mse.partialQuotients();
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(11), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(2), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());

        convergents = mse.convergents();

        do {
            convergent = convergents.nextConvergent();
        } while (convergents.hasNext());

        assertEquals(Rational.create(101, 93), convergent);
    }

    public void testNegativeCoeffs() {
        a = BigInteger.valueOf(-11);
        b = BigInteger.valueOf(-5);
        c = BigInteger.valueOf(4);
        d = BigInteger.valueOf(-7);

        r = Rational.create(1, 10);
        re = new RationalExpansion(r);
        mse = new MatrixStartExpansion(a, b, c, d, re);

        pqi = mse.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{0, 1, 12, 5}, false, mse.partialQuotients());

        convergents = mse.convergents();

        do {
            convergent = convergents.nextConvergent();
        } while (convergents.hasNext());

        assertEquals(Rational.create(61, 66), convergent);

        r = Rational.create(-1, 10);
        re = new RationalExpansion(r);
        mse = new MatrixStartExpansion(a, b, c, d, re);

        pqi = mse.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{0, 1, 1, 8, 1, 3}, false, mse.partialQuotients());

        convergents = mse.convergents();

        do {
            convergent = convergents.nextConvergent();
        } while (convergents.hasNext());

        assertEquals(Rational.create(39, 74), convergent);
    }

    public void testDivisionByZero() {
        r = Rational.valueOf(1);
        re = new RationalExpansion(r);
        a = b = BigInteger.ONE;
        c = d = BigInteger.ZERO;
        mse = new MatrixStartExpansion(a, b, c, d, re);

        pqi = mse.partialQuotients();
        assertTrue(pqi.hasNext());

        try {
            pqi.nextPartialQuotient();
            fail("divide by zero exception should have been thrown");
        } catch (DivideByZeroException divideZero) {
        }

        convergents = mse.convergents();
        assertTrue(convergents.hasNext());

        try {
            convergents.nextConvergent();
            fail("divide by zero exception should have been thrown");
        } catch (DivideByZeroException divideZero) {
        }
    }

    public void testNegative() {
        r = Rational.create(-Double.MIN_VALUE);
        re = new RationalExpansion(r);
        a = b = c = d = BigInteger.ONE;

        try {
            mse = new MatrixStartExpansion(a, b, c, d, re);

            pqi = mse.partialQuotients();
            assertTrue(pqi.hasNext());

            pqi.nextPartialQuotient();
            fail("ArithmeticException should have been thrown");
        } catch (ArithmeticException arithmeticEx) {
        }

        try {
            mse = new MatrixStartExpansion(a, b, c, d, re);
            convergents = mse.convergents();
            assertTrue(convergents.hasNext());

            convergents.nextConvergent();
            fail("divide by zero exception should have been thrown");
        } catch (ArithmeticException arithmeticEx) {
        }
    }

    public void tearDown() {
        r = null;
        re = null;
        mse = null;
        pqi = null;
        a = b = c = d = null;
        convergents = null;
        convergent = null;
    }

}
