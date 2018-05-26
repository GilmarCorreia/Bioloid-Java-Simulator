package tests.precisefloating.continuedfractions;

import junit.framework.TestCase;
import precisefloating.Rational;
import precisefloating.continuedfractions.*;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class NegateSimpleContinuedFractionTest extends TestCase {

    Rational r;
    RationalExpansion re;
    PartialQuotients pqi;
    Convergents convergents;
    Rational convergent;
    ContinuedFraction negate;

    protected void setUp() throws Exception {
    }

    public void testIntegers() {
        r = Rational.ZERO;
        re = new RationalExpansion(r);
        negate = new NegateContinuedFraction(re);

        pqi = negate.partialQuotients();
        assertEquals(BigInteger.valueOf(0), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());

        convergents = negate.convergents();

        do {
            convergent = convergents.nextConvergent();
        } while (convergents.hasNext());

        assertEquals(r.negate(), convergent);

        r = Rational.valueOf(-1);
        re = new RationalExpansion(r);
        negate = new NegateContinuedFraction(re);

        pqi = negate.partialQuotients();
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());

        convergents = negate.convergents();

        do {
            convergent = convergents.nextConvergent();
        } while (convergents.hasNext());

        assertEquals(r.negate(), convergent);
    }

    public void testTwoLength() {
        r = Rational.create(0.5);
        re = new RationalExpansion(r);
        negate = new NegateContinuedFraction(re);

        pqi = negate.partialQuotients();
        assertEquals(BigInteger.valueOf(-1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(2), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());

        convergents = negate.convergents();

        do {
            convergent = convergents.nextConvergent();
        } while (convergents.hasNext());

        assertEquals(r.negate(), convergent);

        r = Rational.create(1, 10);
        re = new RationalExpansion(r);
        negate = new NegateContinuedFraction(re);

        pqi = negate.partialQuotients();
        assertEquals(BigInteger.valueOf(-1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(9), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());

        convergents = negate.convergents();

        do {
            convergent = convergents.nextConvergent();
        } while (convergents.hasNext());

        assertEquals(r.negate(), convergent);
    }


    public void testLongerThanTwo() {
        r = Rational.create(456, 789);
        re = new RationalExpansion(r);
        negate = new NegateContinuedFraction(re);

        // [-1, 2, 2, 1, 2, 2, 2, 2]
        pqi = negate.partialQuotients();
        assertEquals(BigInteger.valueOf(-1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(2), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(2), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(2), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(2), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(2), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(2), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());

        convergents = negate.convergents();

        do {
            convergent = convergents.nextConvergent();
        } while (convergents.hasNext());

        assertEquals(r.negate(), convergent);

        r = Rational.create(2, 3);
        re = new RationalExpansion(r);
        negate = new NegateContinuedFraction(re);

        pqi = negate.partialQuotients();
        assertEquals(BigInteger.valueOf(-1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(3), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());

        convergents = negate.convergents();

        do {
            convergent = convergents.nextConvergent();
        } while (convergents.hasNext());

        assertEquals(r.negate(), convergent);

        r = Rational.create(6, 5);
        re = new RationalExpansion(r);
        negate = new NegateContinuedFraction(re);

        pqi = negate.partialQuotients();
        assertEquals(BigInteger.valueOf(-2), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(4), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());

        convergents = negate.convergents();

        do {
            convergent = convergents.nextConvergent();
        } while (convergents.hasNext());

        assertEquals(r.negate(), convergent);

        r = Rational.create(23, 11);
        re = new RationalExpansion(r);
        negate = new NegateContinuedFraction(re);

        pqi = negate.partialQuotients();
        assertEquals(BigInteger.valueOf(-3), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(10), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());

        convergents = negate.convergents();

        do {
            convergent = convergents.nextConvergent();
        } while (convergents.hasNext());

        assertEquals(r.negate(), convergent);

        r = Rational.create(90, -23);
        re = new RationalExpansion(r);
        negate = new NegateContinuedFraction(re);

        pqi = negate.partialQuotients();
        assertEquals(BigInteger.valueOf(3), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(10), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(2), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());

        convergents = negate.convergents();

        do {
            convergent = convergents.nextConvergent();
        } while (convergents.hasNext());

        assertEquals(r.negate(), convergent);
    }

    public void testInfinite() {
        pqi = ContinuedFractionConstants.Pythagoras.negate().partialQuotients();

        assertEquals(BigInteger.valueOf(-2), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        for (int i = 0; i < 100; i++) {
            assertEquals(BigInteger.valueOf(2), pqi.nextPartialQuotient());
        }
        assertTrue(pqi.hasNext());

        pqi = ContinuedFractionConstants.E.negate().partialQuotients();

        assertEquals(BigInteger.valueOf(-3), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(3), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(4), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(6), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(8), pqi.nextPartialQuotient());
        assertTrue(pqi.hasNext());

    }

    protected void tearDown() throws Exception {
        r = null;
        re = null;
        pqi = null;
        convergents = null;
        convergent = null;
        negate = null;
    }

}
