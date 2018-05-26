package tests.precisefloating.continuedfractions;

import junit.framework.Assert;
import precisefloating.Rational;
import precisefloating.continuedfractions.Convergents;
import precisefloating.continuedfractions.PartialQuotients;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public final class ContinuedFractionTestUtils {

    /**
     * Do not instantiate this class.
     */
    private ContinuedFractionTestUtils() {
    }

    public static void assertStartsWith(long[] expectedValues,
            PartialQuotients actualValues) {
        BigInteger[] bigints = longToBigIntegers(expectedValues);
        assertStartsWith(bigints, actualValues);
    }

    public static void assertStartsWith(long[] expectedValues, boolean expectedHasNext,
            PartialQuotients actualValues) {
        BigInteger[] bigints = longToBigIntegers(expectedValues);
        assertStartsWith(bigints, expectedHasNext, actualValues);
    }

    public static BigInteger[] longToBigIntegers(long[] expectedValues) {
        BigInteger[] bigints = new BigInteger[expectedValues.length];

        for (int i = 0; i < bigints.length; i++) {
            bigints[i] = BigInteger.valueOf(expectedValues[i]);
        }
        return bigints;
    }

    public static void assertStartsWith(BigInteger[] expectedValues,
            PartialQuotients actualValues) {
        for (int i = 0; i < expectedValues.length; i++) {
            BigInteger expected = expectedValues[i];
            BigInteger actual = actualValues.nextPartialQuotient();
            Assert.assertEquals("i = " + i, expected, actual);
        }
    }

    public static void assertStartsWith(BigInteger[] expectedValues, boolean expectedHasNext,
            PartialQuotients actualValues) {
        assertStartsWith(expectedValues, actualValues);
        Assert.assertEquals(expectedHasNext, actualValues.hasNext());
    }

    public static void assertLastConvergentEquals(Rational rational,
            Convergents convergents) {
        Rational convergent;

        do {
            convergent = convergents.nextConvergent();
        } while (convergents.hasNext());

        Assert.assertEquals(rational, convergent);
    }

}
