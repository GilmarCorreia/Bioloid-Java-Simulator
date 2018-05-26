package tests.precisefloating.continuedfractions;

import junit.framework.TestCase;
import precisefloating.Formulas;
import precisefloating.Rational;
import precisefloating.continuedfractions.ContinuedFraction;
import precisefloating.continuedfractions.Convergents;
import precisefloating.continuedfractions.ERadicalExpansion;
import precisefloating.continuedfractions.ERadicalPartialQuotients;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class ERadicalExpansionTest extends TestCase {

    BigInteger p;
    ERadicalPartialQuotients pqi;
    ContinuedFraction eRadical;
    Rational r;
    Convergents convergents;

    public void testAcceptedIndexes() {
        p = BigInteger.valueOf(2);
        eRadical = new ERadicalExpansion(p);
        pqi = (ERadicalPartialQuotients) eRadical.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{1, 1, 1, 1, 5, 1, 1, 9, 1, 1, 13, 1, 1, 17, 1, 1, 21, 1, 1, 25, 1, 1, 29, 1, 1, 33, 1, 1, 37, 1, 1, 41, 1, 1, 45, 1, 1, 49, 1, 1, 53, 1, 1, 57, 1, 1, 61, 1, 1, 65, 1, 1, 69, 1, 1, 73, 1, 1, 77, 1, 1, 81, 1, 1, 85, 1, 1, 89, 1, 1, 93, 1, 1, 97, 1, 1, 101, 1, 1, 105, 1, 1, 109, 1, 1, 113, 1, 1, 117, 1, 1, 121, 1, 1, 125, 1, 1, 129, 1, 1, 133}, true, pqi);

        p = BigInteger.ONE.shiftRight(Formulas.DOUBLE_MIN_TWO_EXPONENT);
        eRadical = new ERadicalExpansion(p);
        pqi = (ERadicalPartialQuotients) eRadical.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new BigInteger[]{
            BigInteger.ONE, p.subtract(BigInteger.ONE)}, true, pqi);

        Convergents convergents = eRadical.convergents();
        r = convergents.nextConvergent();
        assertEquals(Rational.ONE, r);
        r = convergents.nextConvergent();
        assertEquals(Rational.ONE.add(Rational.create(BigInteger.ONE, p.subtract(BigInteger.ONE))), r);
    }

    public void testScroll() {
        p = BigInteger.valueOf(2);
        eRadical = new ERadicalExpansion(p);
        pqi = (ERadicalPartialQuotients) eRadical.partialQuotients();
        pqi.setIndex(3);
        assertEquals(BigInteger.valueOf(5), pqi.nextPartialQuotient());
        pqi.setIndex(6);
        assertEquals(BigInteger.valueOf(9), pqi.nextPartialQuotient());
        pqi.setIndex(2);
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        pqi.setIndex(-1);
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{1, 1, 1, 1, 5, 1, 1, 9, 1, 1, 13, 1, 1, 17, 1, 1, 21, 1, 1, 25, 1, 1, 29, 1, 1, 33, 1, 1, 37, 1, 1, 41, 1, 1, 45, 1, 1, 49, 1, 1, 53, 1, 1, 57, 1, 1, 61, 1, 1, 65, 1, 1, 69, 1, 1, 73, 1, 1, 77, 1, 1, 81, 1, 1, 85, 1, 1, 89, 1, 1, 93, 1, 1, 97, 1, 1, 101, 1, 1, 105, 1, 1, 109, 1, 1, 113, 1, 1, 117, 1, 1, 121, 1, 1, 125, 1, 1, 129, 1, 1, 133}, true, pqi);
    }

    protected void tearDown() throws Exception {
        p = null;
        pqi = null;
        eRadical = null;
        r = null;
        convergents = null;
    }

}
