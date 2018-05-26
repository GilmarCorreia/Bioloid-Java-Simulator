package tests.precisefloating.continuedfractions;

import junit.framework.TestCase;
import precisefloating.Rational;
import precisefloating.continuedfractions.*;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class AddContinuedFractionTest extends TestCase {

    Rational x, y;
    ContinuedFraction xExpansion, yExpansion;
    TensorStartExpansion tse;
    PartialQuotients pqi;
    Convergents convergents;
    Rational convergent;
    ContinuedFraction sum;

    public void testAddWithZero() {
        x = Rational.valueOf(0);
        y = Rational.valueOf(0);
        xExpansion = new RationalExpansion(x);
        yExpansion = new RationalExpansion(y);
        sum = xExpansion.add(yExpansion);
        pqi = sum.partialQuotients();
        assertEquals(BigInteger.valueOf(0), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());

        x = Rational.valueOf(0);
        xExpansion = new RationalExpansion(x);
        yExpansion = ContinuedFractionConstants.E;
        sum = xExpansion.add(yExpansion);
        pqi = sum.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{2, 1, 2, 1, 1, 4, 1, 1, 6, 1, 1, 8, 1, 1, 10, 1, 1, 12, 1, 1, 14},
                true, pqi);

        y = Rational.valueOf(0);
        xExpansion = ContinuedFractionConstants.E.negate();
        yExpansion = new RationalExpansion(y);
        sum = xExpansion.add(yExpansion);
        pqi = sum.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{-3, 3, 1, 1, 4, 1, 1, 6, 1, 1, 8, 1, 1, 10, 1, 1, 12, 1, 1, 14, 1},
                true, pqi);
    }

    public void testAddPositive() {
        x = Rational.create(123, 456); // [0, 3, 1, 2, 2, 2, 2]
        y = Rational.create(124, 455); // [0, 3, 1, 2, 41]
        xExpansion = new RationalExpansion(x);
        yExpansion = new RationalExpansion(y);
        sum = xExpansion.add(yExpansion);
        pqi = sum.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{0, 1, 1, 5, 2, 2, 2, 4, 5, 1, 1, 2, 1, 2}, false, pqi);

        sum = ContinuedFractionConstants.E.add(ContinuedFractionConstants.E);
        pqi = sum.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{5, 2, 3, 2, 3, 1, 2, 1, 3, 4, 3, 1, 4, 1, 3, 6, 3, 1, 6, 1, 3},
                true, pqi);

        sum = ContinuedFractionConstants.Pythagoras.add(ContinuedFractionConstants.E);
        pqi = sum.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{4, 7, 1, 1, 4, 1, 3, 2, 1, 3, 2, 3, 2, 58, 16, 6, 3, 1, 1, 5, 2},
                true, pqi);

        x = Rational.create(3, 4); // [0, 1, 3]
        y = Rational.create(1, 2); // [1, 2]
        xExpansion = new RationalExpansion(x);
        yExpansion = new RationalExpansion(y);
        sum = xExpansion.add(yExpansion);
        pqi = sum.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{1, 4}, false, pqi);

        x = Rational.create(0.5); // [0, 2]
        y = Rational.create(1.5); // [1, 2]
        xExpansion = new RationalExpansion(x);
        yExpansion = new RationalExpansion(y);
        sum = xExpansion.add(yExpansion);
        pqi = sum.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{2}, false, pqi);
    }

    public void testAddNegative() {
        x = Rational.create(3, -4); // [-1, 4]
        y = Rational.create(1, 2); // [1, 2]
        xExpansion = new RationalExpansion(x);
        yExpansion = new RationalExpansion(y);
        sum = xExpansion.add(yExpansion);
        pqi = sum.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{-1, 1, 3}, false, pqi);

        if (true) {
            return;
        }

        sum = ContinuedFractionConstants.E.negate().add(
                ContinuedFractionConstants.E.negate());
        pqi = sum.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{-6, 1, 1, 3, 2, 3, 1, 2, 1, 3, 4, 3, 1, 4, 1, 3, 6, 3, 1, 6, 1},
                true, pqi);

        sum = ContinuedFractionConstants.Pythagoras.negate().add(
                ContinuedFractionConstants.E);
        pqi = sum.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{1, 3, 3, 2, 6, 3, 17, 1, 1, 3, 3, 1, 8, 2, 20, 2, 6, 1, 1, 2, 1},
                true, pqi);

        x = Rational.create(123, 456); // [0, 3, 1, 2, 2, 2, 2]
        y = Rational.create(-124, 455); // [-1, 1, 2, 1, 2, 41]
        xExpansion = new RationalExpansion(x);
        yExpansion = new RationalExpansion(y);
        sum = xExpansion.add(yExpansion);
        pqi = sum.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{-1, 1, 357, 2, 1, 12, 5}, false, pqi);

        x = Rational.create(-0.5); // [-1, 2]
        y = Rational.create(-1.5); // [-2, 2]
        xExpansion = new RationalExpansion(x);
        yExpansion = new RationalExpansion(y);
        sum = xExpansion.add(yExpansion);
        pqi = sum.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{-2}, false, pqi);
    }

    protected void tearDown() throws Exception {
        x = y = null;
        xExpansion = yExpansion = null;
        tse = null;
        pqi = null;
        convergents = null;
        convergent = null;
        sum = null;
    }

}
