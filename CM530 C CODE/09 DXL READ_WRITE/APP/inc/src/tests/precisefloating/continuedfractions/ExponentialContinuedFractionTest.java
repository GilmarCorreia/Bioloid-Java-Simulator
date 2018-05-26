package tests.precisefloating.continuedfractions;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import junit.framework.TestCase;
import precisefloating.Rational;
import precisefloating.continuedfractions.ContinuedFraction;
import precisefloating.continuedfractions.Convergents;
import precisefloating.continuedfractions.ExponentialContinuedFraction;
import precisefloating.continuedfractions.PartialQuotients;

import java.math.BigInteger;
import java.util.Random;
import java.util.logging.Logger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class ExponentialContinuedFractionTest extends TestCase {

    Rational r;
    PartialQuotients pqi;
    Rational convergent;
    ContinuedFraction power;

    private static final Logger log = Logger.getLogger(PowerContinuedFractionTest.class.getName());

    public void testZero() {
        power = new ExponentialContinuedFraction(Rational.ZERO);
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{1}, false, pqi);
        Convergents convergents = power.convergents();
        ContinuedFractionTestUtils.assertLastConvergentEquals(Rational.ONE, convergents);
    }

    public void testNegativeIntegers() {
        power = new ExponentialContinuedFraction(Rational.valueOf(-1));
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{0, 2, 1, 2, 1, 1, 4, 1, 1, 6, 1},
                true, pqi);

        power = new ExponentialContinuedFraction(Rational.valueOf(-2));
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{0, 7, 2, 1, 1, 3, 18, 5, 1, 1, 6},
                true, pqi);

        power = new ExponentialContinuedFraction(Rational.valueOf(-3));
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{0, 20, 11, 1, 2, 4, 3, 1, 5, 1, 2},
                true, pqi);
    }

    public void testPositiveIntegers() {
        power = new ExponentialContinuedFraction(Rational.ONE);
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{2, 1, 2, 1, 1, 4, 1, 1, 6, 1, 1},
                true, pqi);

        power = new ExponentialContinuedFraction(Rational.valueOf(2));
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{7, 2, 1, 1, 3, 18, 5, 1, 1, 6, 30, 8, 1, 1, 9, 42, 11, 1, 1, 12, 54}, true, pqi);

        power = new ExponentialContinuedFraction(Rational.valueOf(3));
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{20, 11, 1, 2, 4, 3, 1, 5, 1, 2, 16, 1, 1, 16, 2, 13, 14, 4, 6, 2, 1}, true, pqi);
    }

    public void testRationals() {
        power = new ExponentialContinuedFraction(Rational.create(3, 2));
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{4, 2, 13, 6, 1, 1, 6, 1, 6, 1, 20, 1, 87, 1, 1, 2, 1, 5, 1, 2, 9, 1, 7, 1, 1, 61, 1, 9, 4, 2, 2, 1, 2, 2, 5, 2, 1, 2, 1, 1, 30, 2, 78, 5, 1, 1, 5, 1, 12, 2, 1, 4, 2, 3, 1, 2, 1, 1, 1, 4, 9, 1, 1, 3, 2, 1, 24, 1, 5, 1, 3, 5, 6, 3, 9, 4, 8, 1, 2, 1, 2, 2, 1, 2, 3, 1, 1, 3, 1, 8, 4, 1, 41, 1, 2, 13, 5, 1, 55, 2, 1},
                true, pqi);

        power = new ExponentialContinuedFraction(Rational.create(789, 456));
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{5, 1, 1, 1, 3, 1, 6, 3, 5, 3, 15, 3, 170, 1, 3, 1, 5, 1, 1, 21, 3, 1, 2, 4, 3, 1, 13, 21, 1, 1, 10, 2, 3, 1, 1, 1, 1, 1, 1, 3, 106, 1, 1, 1, 3, 2, 1, 16, 15, 3, 2, 2, 1, 1, 271, 1, 2, 1, 1, 1, 2, 8, 5, 5, 1, 5, 2, 4, 7, 1, 1, 2, 3, 4, 4, 5, 4, 5, 1, 5, 1, 6, 1, 13, 1, 10, 1, 2, 2, 3, 2, 138, 2, 1, 3, 2, 34, 4, 1, 31, 1},
                true, pqi);

        power = new ExponentialContinuedFraction(Rational.create(-789, 456));
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{0, 5, 1, 1, 1, 3, 1, 6, 3, 5, 3, 15, 3, 170, 1, 3, 1, 5, 1, 1, 21, 3, 1, 2, 4, 3, 1, 13, 21, 1, 1, 10, 2, 3, 1, 1, 1, 1, 1, 1, 3, 106, 1, 1, 1, 3, 2, 1, 16, 15, 3, 2, 2, 1, 1, 271, 1, 2, 1, 1, 1, 2, 8, 5, 5, 1, 5, 2, 4, 7, 1, 1, 2, 3, 4, 4, 5, 4, 5, 1, 5, 1, 6, 1, 13, 1, 10, 1, 2, 2, 3, 2, 138, 2, 1, 3, 2, 34, 4, 1, 31},
                true, pqi);

        power = new ExponentialContinuedFraction(Rational.create(5, 23));
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{1, 4, 8, 2, 7, 7, 8, 3, 83, 1, 2, 3, 4, 1, 1, 7, 3, 8, 1, 1, 9, 2, 3, 1, 10, 1, 6, 3, 3, 1, 1, 9, 2, 1, 14, 7, 1, 3, 7, 3, 2, 2, 2, 3, 1, 1, 2, 1, 2, 4, 1, 9, 3, 2, 1, 14, 3, 1, 1, 8, 1, 3, 27, 1, 1, 1, 2, 1, 5, 67, 6, 1, 127, 11, 2, 7, 1, 58, 1, 2, 3, 2, 5, 1, 1, 1, 4, 1, 1, 2, 3, 1, 2, 4, 1, 5, 1, 6, 1, 1, 5},
                true, pqi);
    }

    public void testExponentialSpeed() {
        Random rnd = new Random(/*0*/);
        Monitor powerMon, multiplyMon;

        powerMon = multiplyMon = null;

        int bitCount = 10;
        for (int i = 0; i < 5; i++) {
            r = new Rational(bitCount, rnd);
            // log.info("r = " + r);

            // log.info("start power");
            power = new ExponentialContinuedFraction(r);
            powerMon = MonitorFactory.start("rational power");
            pqi = power.partialQuotients();
            if (!r.equals(BigInteger.ONE)) {
                for (int j = 0; j < 50; j++) {
                    pqi.nextPartialQuotient();
                }
                assert pqi.hasNext();
            }
            powerMon.stop();
            // log.info("end power");
        }

        log.info("rational power " + powerMon.toString());

        powerMon = multiplyMon = null;

        BigInteger[] expected = new BigInteger[100];

        expected[0] = new BigInteger("17w47dfzr2z3lnvufbnlwd3r00v3mmf4m17zbj5eordwybyt2ux337bt9qku4iad7fducpp0au8tw4mld7nvtxiaotrbnkhcxrtlgvwg0b905dj4npaujeyfoero3bvvbuoswj1vqp0bd8l0lzdjpixksga7bduva4z5askfftu5n6nuiwhqk7ngcfedrvdca8torxbnt07rs4nt0x89vilut6t5opmeh8jup5qgw6r8cigjysmg3p7i5iwklf9y16tr3hc101r8xmw71tm3sacg", Character.MAX_RADIX);
        System.arraycopy(ContinuedFractionTestUtils.longToBigIntegers(
                new long[]{
                    4, 2, 2, 3, 1, 1, 1, 1, 11, 9, 15, 1, 2, 1, 1, 1, 1, 2, 2, 1, 105, 63, 16, 1, 4, 7, 2, 1, 1, 5, 1, 2, 1, 5, 1, 1, 1, 92, 1, 4, 1, 7, 7, 1, 4, 1, 2, 81, 3, 1, 4, 1, 3, 34, 2, 1, 1, 1, 1, 2, 1, 1, 1, 2, 6, 2, 2, 6, 1, 1, 1, 1, 1, 1, 1, 19, 6, 1, 2, 1, 9, 2, 4, 1, 1, 5, 3, 6, 4, 1, 4, 4, 21, 1, 3, 5, 3, 1, 1, 5}),
                0,
                expected, 1, expected.length - 1);

        for (int i = 0; i < 5; i++) {
            power = new ExponentialContinuedFraction(1000);
            powerMon = MonitorFactory.start("exponential1000");
            ContinuedFractionTestUtils.assertStartsWith(expected, true, power.partialQuotients());
            powerMon.stop();
        }

        log.info("exponential1000 " + powerMon.toString());

        expected[0] = BigInteger.ZERO;
        expected[1] = new BigInteger("26881171418161354484126255515800135873611118");

        System.arraycopy(ContinuedFractionTestUtils.longToBigIntegers(
                new long[]{
                    1, 3, 2, 2, 1, 1, 1, 1, 2, 6, 1, 2, 1, 21, 3, 1, 15, 1, 8, 3, 3, 2, 19, 1, 4, 3, 1, 1, 1, 20, 1, 2, 29, 2, 4, 1, 1, 1, 2, 1, 1, 3, 5, 1, 11, 1, 39, 1, 1, 3, 1, 3, 13, 5, 1, 51, 1, 19, 1, 1, 9, 2, 4, 2, 2, 1, 1, 48, 1, 1, 3, 4, 1, 1, 40, 1, 1, 2, 9, 9, 2, 3, 1, 28, 2, 1, 1, 14, 3, 4, 1, 7, 7, 5, 1, 16, 4, 4, 2}),
                0,
                expected, 2, expected.length - 2);

        for (int i = 0; i < 5; i++) {
            power = new ExponentialContinuedFraction(-100);
            powerMon = MonitorFactory.start("exponential-100");
            ContinuedFractionTestUtils.assertStartsWith(expected, true, power.partialQuotients());
            powerMon.stop();
        }

        log.info("exponential-100 " + powerMon.toString());
    }

    protected void tearDown() throws Exception {
        r = null;
        pqi = null;
        convergent = null;
        power = null;
    }

}
