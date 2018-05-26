package tests.precisefloating.continuedfractions;

import junit.framework.TestCase;
import precisefloating.continuedfractions.ContinuedFraction;
import precisefloating.continuedfractions.ContinuedFractionConstants;
import precisefloating.continuedfractions.RoundContinuedFraction;
import precisefloating.Rational;
import precisefloating.directedrounding.RoundingMode;

import java.util.Random;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class RoundContinuedFractionTest extends TestCase {

    private ContinuedFraction cf;
    private RoundContinuedFraction rcf;
    private Random rnd;

    protected void setUp() throws Exception {
        rnd = new Random();
    }

    public void testNearestEuler() {
        cf = ContinuedFractionConstants.E;
        assertEquals(Math.E, cf.doubleValue(), 0);
    }

    public void testHalf() {
        cf = Rational.create(0.5).expansion();
        rcf = new RoundContinuedFraction(cf);

        rcf.setRoundingMode(RoundingMode.ROUND_FLOOR);
        assertEquals(0, rcf.longValue());

        rcf.setRoundingMode(RoundingMode.ROUND_HALF_EVEN);
        assertEquals(0, rcf.longValue());

        rcf.setRoundingMode(RoundingMode.ROUND_CEILING);
        assertEquals(1, rcf.longValue());
    }

    public void testExactNumbers() {
        for (int i = 0; i < 1000; i++) {
            double d = rnd.nextGaussian();

            d = -0.616283820679282;

            cf = Rational.create(d).expansion();
            rcf = new RoundContinuedFraction(cf);

            rcf.setRoundingMode(RoundingMode.ROUND_FLOOR);
            assertEquals(d, rcf.doubleValue(), 0);

            rcf.setRoundingMode(RoundingMode.ROUND_HALF_EVEN);
            assertEquals(d, rcf.doubleValue(), 0);

            rcf.setRoundingMode(RoundingMode.ROUND_CEILING);
            assertEquals(d, rcf.doubleValue(), 0);
        }
    }

}
