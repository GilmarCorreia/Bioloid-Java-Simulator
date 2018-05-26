package tests.precisefloating.continuedfractions;

import junit.framework.TestCase;
import precisefloating.Rational;
import precisefloating.continuedfractions.ContinuedFraction;
import precisefloating.continuedfractions.ContinuedFractionConstants;

import java.util.Random;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class ContinuedFractionTest extends TestCase {
    Rational r1, r2;
    int cmp;
    Random rnd;
    ContinuedFraction cf1, cf2;

    protected void setUp() throws Exception {
        rnd = new Random(/*0*/);
    }

    public void testCompareTo() {
        r1 = Rational.create(100, 101); // [0, 1, 100]
        r2 = Rational.create(101, 102); // [0, 1, 101]
        cmp = r1.compareTo(r2);
        assertTrue(cmp < 0);

        r1 = new Rational(1000, rnd);
        cmp = r1.compareTo(r1);
        assertTrue(cmp == 0);

        cf1 = ContinuedFractionConstants.E;
        cf2 = ContinuedFractionConstants.Pythagoras.multiply(Rational.valueOf(2).expansion());
        cmp = cf1.compareTo(cf2);
        assertTrue(cmp < 0);

        r1 = Rational.create(225, 157); // [1, 2, 3, 4, 5]
        r2 = Rational.create(1393, 972); // [1, 2, 3, 4, 5, 6]
        cmp = r1.compareTo(r2);
        assertTrue(cmp < 0);
    }

    protected void tearDown() throws Exception {
        r1 = r2 = null;
        cf1 = cf2 = null;
    }

}
