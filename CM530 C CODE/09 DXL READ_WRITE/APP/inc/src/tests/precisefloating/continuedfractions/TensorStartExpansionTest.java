package tests.precisefloating.continuedfractions;

import junit.framework.TestCase;
import precisefloating.Rational;
import precisefloating.continuedfractions.*;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class TensorStartExpansionTest extends TestCase {

    Rational x, y;
    RationalExpansion xExpansion, yExpansion;
    TensorStartExpansion tse;
    PartialQuotients pqi;
    BigInteger a, b, c, d, e, f, g, h;
    Convergents convergents;
    Rational convergent;

    public void testExpansion() {
        x = Rational.ONE;
        y = Rational.ONE;
        xExpansion = new RationalExpansion(x);
        yExpansion = new RationalExpansion(y);
        a = BigInteger.ONE;
        b = BigInteger.ONE;
        c = BigInteger.ONE;
        d = BigInteger.ONE;
        e = BigInteger.ONE;
        f = BigInteger.ONE;
        g = BigInteger.ONE;
        h = BigInteger.ONE;
        tse = new TensorStartExpansion(a, b, c, d, e, f, g, h, xExpansion, yExpansion);

        pqi = tse.partialQuotients();
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());

        convergents = tse.convergents();

        do {
            convergent = convergents.nextConvergent();
        } while (convergents.hasNext());

        assertEquals(Rational.ONE, convergent);
    }

    public void testNegativeCoeffs() {
        x = Rational.valueOf(-1);
        y = Rational.valueOf(-1);
        xExpansion = new RationalExpansion(x);
        yExpansion = new RationalExpansion(y);
        a = BigInteger.valueOf(-1);
        b = BigInteger.valueOf(2);
        c = BigInteger.valueOf(3);
        d = BigInteger.valueOf(-4);
        e = BigInteger.valueOf(10);
        f = BigInteger.valueOf(-11);
        g = BigInteger.valueOf(-12);
        h = BigInteger.valueOf(13);
        tse = new TensorStartExpansion(a, b, c, d, e, f, g, h, xExpansion, yExpansion);

        pqi = tse.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{-1, 1, 3, 1, 1, 2}, false, pqi);

        convergents = tse.convergents();

        do {
            convergent = convergents.nextConvergent();
        } while (convergents.hasNext());

        assertEquals(Rational.create(-5, 23), convergent);
    }

    public void testAdd() {
        ContinuedFraction sum;

        x = Rational.valueOf(0); // [0]
        y = Rational.valueOf(0); // [0]
        xExpansion = new RationalExpansion(x);
        yExpansion = new RationalExpansion(y);

        sum = xExpansion.add(yExpansion); // [0]

        pqi = sum.partialQuotients();
        assertEquals(BigInteger.valueOf(0), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());

        x = Rational.create(0.5); // [0, 2]
        y = Rational.create(1.5); // [1, 2]
        xExpansion = new RationalExpansion(x);
        yExpansion = new RationalExpansion(y);

        sum = xExpansion.add(yExpansion); // [2]

        pqi = sum.partialQuotients();
        assertEquals(BigInteger.valueOf(2), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());
    }

}
