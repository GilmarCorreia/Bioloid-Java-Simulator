package tests.precisefloating.continuedfractions;

import junit.framework.TestCase;
import precisefloating.Rational;
import precisefloating.continuedfractions.ContinuedFractionConstants;
import precisefloating.continuedfractions.PartialQuotients;
import precisefloating.continuedfractions.RationalExpansion;
import precisefloating.continuedfractions.SharedPartialQuotients;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class SharedPartialQuotientsTest extends TestCase {

    Rational r;
    RationalExpansion re;
    PartialQuotients xPqi, yPqi;
    SharedPartialQuotients[] a = new SharedPartialQuotients[2];
    long[] partialQuotients;

    public void testRationals() {
        r = Rational.create(1, 2);
        re = new RationalExpansion(r);
        xPqi = re.partialQuotients();
        yPqi = re.partialQuotients();
        SharedPartialQuotients.share(xPqi, yPqi, 1000, a, 0);

        a[0].next();
        a[1].next();
        a[0].hasNext();

        r = Rational.valueOf(BigInteger.ZERO);
        re = new RationalExpansion(r);
        xPqi = re.partialQuotients();
        yPqi = ContinuedFractionConstants.Pythagoras.partialQuotients();
        SharedPartialQuotients.share(xPqi, yPqi, 2, a, 0);
        partialQuotients = new long[]{0};
        for (int i = 0; i < 10; i++) {
            assertTrue(a[0].hasNext());
            assertTrue(a[1].hasNext());
        }
        ContinuedFractionTestUtils.assertStartsWith(partialQuotients, false, a[0]);
        ContinuedFractionTestUtils.assertStartsWith(partialQuotients, a[1]);

        r = Rational.create(BigInteger.valueOf(67), BigInteger.valueOf(29));
        re = new RationalExpansion(r);
        xPqi = re.partialQuotients();
        yPqi = ContinuedFractionConstants.GoldenRatio.partialQuotients();
        SharedPartialQuotients.share(xPqi, yPqi, 4, a, 0);
        partialQuotients = new long[]{2, 3, 4, 2};
        ContinuedFractionTestUtils.assertStartsWith(partialQuotients, false, a[0]);
        ContinuedFractionTestUtils.assertStartsWith(partialQuotients, a[1]);

        r = Rational.create(456, 789);
        re = new RationalExpansion(r);
        xPqi = re.partialQuotients();
        yPqi = re.partialQuotients();
        SharedPartialQuotients.share(xPqi, yPqi, 8, a, 0);
        partialQuotients = new long[]{0, 1, 1, 2, 1, 2, 2, 2, 2};
        ContinuedFractionTestUtils.assertStartsWith(partialQuotients, false, a[0]);
        ContinuedFractionTestUtils.assertStartsWith(partialQuotients, false, a[1]);

        r = Rational.create(456, 789);
        xPqi = re.partialQuotients();
        yPqi = re.partialQuotients();
        SharedPartialQuotients.share(xPqi, yPqi, 1, a, 0);
        partialQuotients = new long[]{0, 1, 1, 2, 1, 2, 2, 2, 2};
        ContinuedFractionTestUtils.assertStartsWith(partialQuotients, false, a[0]);
        ContinuedFractionTestUtils.assertStartsWith(partialQuotients, false, a[1]);
    }

    protected void tearDown() throws Exception {
        r = null;
        re = null;
        xPqi = null;
        a[0] = a[1] = null;
    }

}
