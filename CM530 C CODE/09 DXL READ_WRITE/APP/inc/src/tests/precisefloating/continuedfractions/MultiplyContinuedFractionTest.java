package tests.precisefloating.continuedfractions;

import junit.framework.TestCase;
import precisefloating.Rational;
import precisefloating.continuedfractions.*;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class MultiplyContinuedFractionTest extends TestCase {

    Rational x, y;
    ContinuedFraction xExpansion, yExpansion;
    TensorStartExpansion tse;
    PartialQuotients pqi;
    Convergents convergents;
    Rational convergent;
    ContinuedFraction product;

    public void testMultiplyWithZero() {
        x = Rational.valueOf(0);
        y = Rational.valueOf(0);
        xExpansion = new RationalExpansion(x);
        yExpansion = new RationalExpansion(y);

        product = xExpansion.multiply(yExpansion);

        pqi = product.partialQuotients();
        assertEquals(BigInteger.valueOf(0), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());
    }

    public void testMultiplyPositive() {
        product = ContinuedFractionConstants.E.multiply(ContinuedFractionConstants.E);
        pqi = product.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{7, 2, 1, 1, 3, 18, 5, 1, 1, 6, 30}, true, pqi);

        product = ContinuedFractionConstants.Pythagoras.multiply(ContinuedFractionConstants.E);
        pqi = product.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{3, 1, 5, 2, 2, 1, 1, 1, 1, 1, 1, 13, 1, 1, 1, 94, 1, 9, 1, 1, 1},
                true, pqi);

        x = Rational.create(123, 456); // [0, 3, 1, 2, 2, 2, 2]
        y = Rational.create(124, 455); // [0, 3, 1, 2, 41]
        xExpansion = new RationalExpansion(x);
        yExpansion = new RationalExpansion(y);
        product = xExpansion.multiply(yExpansion);
        pqi = product.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{0, 13, 1, 1, 1, 1, 10, 1, 21}, false, pqi);

        x = Rational.create(3, 4); // [0, 1, 3]
        y = Rational.create(1, 2); // [1, 2]
        xExpansion = new RationalExpansion(x);
        yExpansion = new RationalExpansion(y);

        product = xExpansion.multiply(yExpansion);
        pqi = product.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{0, 2, 1, 2}, false, pqi);

        x = Rational.create(0.5); // [0, 2]
        y = Rational.create(1.5); // [1, 2]
        xExpansion = new RationalExpansion(x);
        yExpansion = new RationalExpansion(y);

        product = xExpansion.multiply(yExpansion);
        pqi = product.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{0, 1, 3}, false, pqi);
    }

    public void testMultiplyNegative() {
        product = ContinuedFractionConstants.E.negate().multiply(
                ContinuedFractionConstants.E.negate());
        pqi = product.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{7, 2, 1, 1, 3, 18, 5, 1, 1, 6, 30}, true, pqi);

        product = ContinuedFractionConstants.Pythagoras.negate().multiply(
                ContinuedFractionConstants.E);
        pqi = product.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{-4, 6, 2, 2, 1, 1, 1, 1, 1, 1, 13, 1, 1, 1, 94, 1, 9, 1, 1, 1, 2},
                true, pqi);

        x = Rational.create(123, 456); // [0, 3, 1, 2, 2, 2, 2]
        y = Rational.create(-124, 455); // [-1, 1, 2, 1, 2, 41]
        xExpansion = new RationalExpansion(x);
        yExpansion = new RationalExpansion(y);

        product = xExpansion.multiply(yExpansion);
        pqi = product.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{-1, 1, 12, 1, 1, 1, 1, 10, 1, 21}, false, pqi);

        x = Rational.create(3, -4); // [-1, 4]
        y = Rational.create(1, 2); // [1, 2]
        xExpansion = new RationalExpansion(x);
        yExpansion = new RationalExpansion(y);
        product = xExpansion.multiply(yExpansion);
        pqi = product.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{-1, 1, 1, 1, 2}, false, pqi);

        x = Rational.create(-0.5); // [-1, 2]
        y = Rational.create(-1.5); // [-2, 2]
        xExpansion = new RationalExpansion(x);
        yExpansion = new RationalExpansion(y);

        product = xExpansion.multiply(yExpansion);
        pqi = product.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{0, 1, 3}, false, pqi);
    }

    protected void tearDown() throws Exception {
        x = y = null;
        xExpansion = yExpansion = null;
        tse = null;
        pqi = null;
        convergents = null;
        convergent = null;
        product = null;
    }

}
