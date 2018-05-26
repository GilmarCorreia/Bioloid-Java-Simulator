package tests.precisefloating.continuedfractions;

import junit.framework.TestCase;
import precisefloating.Rational;
import precisefloating.continuedfractions.*;

import java.math.BigInteger;
import java.util.List;


/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class CachePartialQuotientsDecoratorTest extends TestCase {

    Rational r;
    RationalExpansion re;
    PartialQuotients pqi;
    CachePartialQuotientsDecorator cache;

    public void testIntegers() {
        r = Rational.ZERO;
        re = new RationalExpansion(r);
        pqi = re.partialQuotients();
        cache = new CachePartialQuotientsDecorator(new BigInteger[]{BigInteger.ZERO}, pqi);
        assertEquals(BigInteger.ZERO, cache.nextPartialQuotient());
        assertFalse(cache.hasNext());

        r = Rational.valueOf(-1);
        re = new RationalExpansion(r);
        pqi = re.partialQuotients();
        cache = new CachePartialQuotientsDecorator(new BigInteger[]{BigInteger.valueOf(-1)}, pqi);
        assertEquals(BigInteger.valueOf(-1), cache.nextPartialQuotient());
        assertFalse(cache.hasNext());
    }

    public void testRationals() {
        Rational r;
        RationalExpansion re;
        PartialQuotients pqi;
        r = Rational.create(BigInteger.valueOf(67), BigInteger.valueOf(29));
        re = new RationalExpansion(r);
        pqi = re.partialQuotients();
        cache = new CachePartialQuotientsDecorator(
                new BigInteger[]{BigInteger.valueOf(2), BigInteger.valueOf(3)}, pqi);
        ContinuedFractionTestUtils.assertStartsWith(new long[]{2, 3, 4, 2}, false, cache);
    }

    public void testExpRadicalCache() {
        for (int i = -105; i < -105 + 115; i++) {
            System.out.println(i);
            List list = EGeneratedTable.getInstance().getBuffer(i);

            if (list != null) {
                PartialQuotients pqi = ERadicalPowersGenerator.eTwoPower(i);
                ContinuedFractionTestUtils.assertStartsWith(
                        (BigInteger[]) list.toArray(new BigInteger[list.size()]), true, pqi);
            }
        }
    }

}
