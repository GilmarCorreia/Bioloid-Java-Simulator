package tests.precisefloating.continuedfractions;

import junit.framework.TestCase;
import precisefloating.Rational;
import precisefloating.continuedfractions.PartialQuotients;
import precisefloating.continuedfractions.RationalExpansion;

import java.math.BigInteger;
import java.util.Iterator;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class RationalExpansionTest extends TestCase {

    public void testIntegers() {
        Rational r;
        RationalExpansion re;
        PartialQuotients pqi;

        r = Rational.ZERO;
        re = new RationalExpansion(r);

        pqi = re.partialQuotients();
        assertEquals(BigInteger.ZERO, pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());

        r = Rational.valueOf(-1);
        re = new RationalExpansion(r);

        pqi = re.partialQuotients();
        assertEquals(BigInteger.valueOf(-1), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());
    }

    public void testRationals() {
        Rational r;
        RationalExpansion re;
        PartialQuotients pqi;
        r = Rational.create(BigInteger.valueOf(67), BigInteger.valueOf(29));
        re = new RationalExpansion(r);

        pqi = re.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{2, 3, 4, 2}, false, pqi);

        Iterator ci = re.convergents();
        assertEquals(Rational.valueOf(2), ci.next());
        assertEquals(Rational.create(BigInteger.valueOf(7), BigInteger.valueOf(3)), ci.next());
        assertEquals(Rational.create(BigInteger.valueOf(30), BigInteger.valueOf(13)), ci.next());
        assertEquals(r, ci.next());
        assertFalse(ci.hasNext());

        r = Rational.create(1.5);
        re = new RationalExpansion(r);

        pqi = re.partialQuotients();
        assertEquals(BigInteger.ONE, pqi.nextPartialQuotient());
        assertEquals(BigInteger.valueOf(2), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());

        r = Rational.create(456, 789);
        re = new RationalExpansion(r);

        pqi = re.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{0, 1, 1, 2, 1, 2, 2, 2, 2}, false, pqi);
    }

}
