package tests.precisefloating.continuedfractions;

import junit.framework.TestCase;
import precisefloating.DivideByZeroException;
import precisefloating.Rational;
import precisefloating.continuedfractions.*;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class InverseSimpleContinuedFractionTest extends TestCase {

    Rational r;
    RationalExpansion re;
    PartialQuotients pqi;
    Rational convergent;
    ContinuedFraction inverse;

    public void testIntegers() {
        r = Rational.ZERO;
        re = new RationalExpansion(r);
        inverse = new InverseContinuedFraction(re);

        pqi = inverse.partialQuotients();
        assertTrue(pqi.hasNext());
        try {
            pqi.nextPartialQuotient();
            fail("should have thrown DivideByZeroException");
        } catch (DivideByZeroException divideZero) {
        }

        Convergents convergents = inverse.convergents();

        assertTrue(convergents.hasNext());
        try {
            convergents.nextConvergent();
            fail("should have thrown DivideByZeroException");
        } catch (DivideByZeroException divideZero) {
        }
    }

    public void testNegativeIntegers() {
        r = Rational.valueOf(-3);
        re = new RationalExpansion(r);
        inverse = new InverseContinuedFraction(re);
        pqi = inverse.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{-1, 1, 2}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.inverse(), inverse.convergents());

        r = Rational.valueOf(-2);
        re = new RationalExpansion(r);
        inverse = new InverseContinuedFraction(re);

        pqi = inverse.partialQuotients();
        pqi = inverse.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{-1, 2}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.inverse(), inverse.convergents());

        r = Rational.valueOf(-1);
        re = new RationalExpansion(r);
        inverse = new InverseContinuedFraction(re);

        pqi = inverse.partialQuotients();
        assertEquals(BigInteger.valueOf(-1), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.inverse(), inverse.convergents());
    }

    public void testPositiveIntegers() {
        r = Rational.valueOf(1);
        re = new RationalExpansion(r);
        inverse = new InverseContinuedFraction(re);

        pqi = inverse.partialQuotients();
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.inverse(), inverse.convergents());

        r = Rational.valueOf(2);
        re = new RationalExpansion(r);
        inverse = new InverseContinuedFraction(re);

        pqi = inverse.partialQuotients();
        pqi = inverse.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{0, 2}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.inverse(), inverse.convergents());

        r = Rational.valueOf(3);
        re = new RationalExpansion(r);
        inverse = new InverseContinuedFraction(re);
        pqi = inverse.partialQuotients();
        pqi = inverse.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{0, 3}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.inverse(), inverse.convergents());
    }

    public void testPositiveRationals() {
        r = Rational.create(789, 456);
        re = new RationalExpansion(r);
        inverse = new InverseContinuedFraction(re);
        pqi = inverse.partialQuotients();
        pqi = inverse.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{0, 1, 1, 2, 1, 2, 2, 2, 2}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.inverse(), inverse.convergents());

        r = Rational.create(-789, 456);
        re = new RationalExpansion(r);
        inverse = new InverseContinuedFraction(re);
        pqi = inverse.partialQuotients();
        pqi = inverse.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{-1, 2, 2, 1, 2, 2, 2, 2}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.inverse(), inverse.convergents());

        r = Rational.create(5, 23);
        re = new RationalExpansion(r);
        inverse = new InverseContinuedFraction(re);

        pqi = inverse.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{4, 1, 1, 2}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.inverse(), inverse.convergents());
    }

    protected void tearDown() throws Exception {
        r = null;
        re = null;
        pqi = null;
        convergent = null;
        inverse = null;
    }

}
