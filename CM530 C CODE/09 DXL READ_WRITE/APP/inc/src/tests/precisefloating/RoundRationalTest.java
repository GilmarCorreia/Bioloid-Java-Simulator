package tests.precisefloating;

import junit.framework.TestCase;
import precisefloating.Rational;
import precisefloating.RoundRational;
import precisefloating.Formulas;
import precisefloating.directedrounding.RoundingMode;

import java.util.Random;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class RoundRationalTest extends TestCase {

    Rational r;
    RoundRational rr;
    double d, dComputed;
    float f, fComputed;

    protected void tearDown() throws Exception {
        random = null;
    }

    protected void setUp() throws Exception {
        random = new Random();
    }

    Random random;

    private static final int N = 400;

    public void testDoubleRoundFloor() {
        d = 0.7596474877068577;
        r = Rational.create(d);
        rr = new RoundRational(r);
        rr.setRoundingMode(RoundingMode.ROUND_FLOOR);
        dComputed = rr.doubleValue();
        assertEquals(d, dComputed, 0);

        for (int i = 0; i < N; i++) {
            d = random.nextGaussian();
            r = Rational.create(d);
            rr = new RoundRational(r);
            rr.setRoundingMode(RoundingMode.ROUND_FLOOR);
            dComputed = rr.doubleValue();
            assertEquals(d, dComputed, 0);

            double dPrevious = Formulas.previous(d), dNext = Formulas.next(d);
            Rational dPreviousAsRational = Rational.create(dPrevious),
                    dAsRational = Rational.create(d), dNextAsRational = Rational.create(dNext),
                    a = dPreviousAsRational.add(dAsRational).multiplyTwoPower(-1),
                    b = dAsRational.add(dNextAsRational).multiplyTwoPower(-1);

            rr = new RoundRational(a);
            rr.setRoundingMode(RoundingMode.ROUND_FLOOR);
            double aFloor = rr.doubleValue();

            rr = new RoundRational(b);
            rr.setRoundingMode(RoundingMode.ROUND_FLOOR);
            double bFloor = rr.doubleValue();

            assertEquals(dPrevious, aFloor, 0);
            assertEquals(d, bFloor, 0);

            Rational small = Rational.DOUBLE_HALF_MIN_VALUE.multiplyTwoPower(-1024);
            Rational t = a.subtract(small), x = a.add(small), y = b.subtract(small), z = b.add(small);

            rr = new RoundRational(t);
            rr.setRoundingMode(RoundingMode.ROUND_FLOOR);
            double cFloor = rr.doubleValue();

            rr = new RoundRational(x);
            rr.setRoundingMode(RoundingMode.ROUND_FLOOR);
            double dFloor = rr.doubleValue();

            rr = new RoundRational(y);
            rr.setRoundingMode(RoundingMode.ROUND_FLOOR);
            double eFloor = rr.doubleValue();

            rr = new RoundRational(z);
            rr.setRoundingMode(RoundingMode.ROUND_FLOOR);
            double fFloor = rr.doubleValue();

            assertEquals(dPrevious, cFloor, 0);
            assertEquals(dPrevious, dFloor, 0);
            assertEquals(d, eFloor, 0);
            assertEquals(d, fFloor, 0);
        }
    }

    public void testDoubleRoundCeil() {
        d = 0.7596474877068577;
        r = Rational.create(d);
        rr = new RoundRational(r);
        rr.setRoundingMode(RoundingMode.ROUND_CEILING);
        dComputed = rr.doubleValue();
        assertEquals(d, dComputed, 0);

        for (int i = 0; i < N; i++) {
            d = random.nextGaussian();
            r = Rational.create(d);
            rr = new RoundRational(r);
            rr.setRoundingMode(RoundingMode.ROUND_CEILING);
            dComputed = rr.doubleValue();
            assertEquals(d, dComputed, 0);

            double dPrevious = Formulas.previous(d), dNext = Formulas.next(d);
            Rational dPreviousAsRational = Rational.create(dPrevious),
                    dAsRational = Rational.create(d), dNextAsRational = Rational.create(dNext),
                    a = dPreviousAsRational.add(dAsRational).multiplyTwoPower(-1),
                    b = dAsRational.add(dNextAsRational).multiplyTwoPower(-1);

            rr = new RoundRational(a);
            rr.setRoundingMode(RoundingMode.ROUND_CEILING);
            double aCeiling = rr.doubleValue();

            rr = new RoundRational(b);
            rr.setRoundingMode(RoundingMode.ROUND_CEILING);
            double bCeiling = rr.doubleValue();

            assertEquals(d, aCeiling, 0);
            assertEquals(dNext, bCeiling, 0);

            Rational small = Rational.DOUBLE_HALF_MIN_VALUE.multiplyTwoPower(-1024);
            Rational t = a.subtract(small), x = a.add(small), y = b.subtract(small), z = b.add(small);

            rr = new RoundRational(t);
            rr.setRoundingMode(RoundingMode.ROUND_CEILING);
            double cCeiling = rr.doubleValue();

            rr = new RoundRational(x);
            rr.setRoundingMode(RoundingMode.ROUND_CEILING);
            double dCeiling = rr.doubleValue();

            rr = new RoundRational(y);
            rr.setRoundingMode(RoundingMode.ROUND_CEILING);
            double eCeiling = rr.doubleValue();

            rr = new RoundRational(z);
            rr.setRoundingMode(RoundingMode.ROUND_CEILING);
            double fCeiling = rr.doubleValue();

            assertEquals(d, cCeiling, 0);
            assertEquals(d, dCeiling, 0);
            assertEquals(dNext, eCeiling, 0);
            assertEquals(dNext, fCeiling, 0);
        }
    }

    public void testDoubleRoundNearest() {
        d = 0.7596474877068577;
        r = Rational.create(d);
        rr = new RoundRational(r);
        rr.setRoundingMode(RoundingMode.ROUND_HALF_EVEN);
        dComputed = rr.doubleValue();
        assertEquals(d, dComputed, 0);

        for (int i = 0; i < N; i++) {
            d = random.nextGaussian();
            r = Rational.create(d);
            dComputed = r.doubleValue();
            assertEquals(d, dComputed, 0);

            double dPrevious = Formulas.previous(d), dNext = Formulas.next(d);
            Rational dPreviousAsRational = Rational.create(dPrevious),
                    dAsRational = Rational.create(d), dNextAsRational = Rational.create(dNext),
                    a = dPreviousAsRational.add(dAsRational).multiplyTwoPower(-1),
                    b = dAsRational.add(dNextAsRational).multiplyTwoPower(-1);
            double aNearest = a.doubleValue(), bNearest = b.doubleValue();

            long bits = Double.doubleToLongBits(d);
            if ((bits & 1) == 0) {
                assertEquals(d, aNearest, 0);
                assertEquals(d, bNearest, 0);
            } else {
                assertEquals(dPrevious, aNearest, 0);
                assertEquals(dNext, bNearest, 0);
            }

            Rational small = Rational.DOUBLE_HALF_MIN_VALUE.multiplyTwoPower(-1024);
            Rational t = a.subtract(small), x = a.add(small), y = b.subtract(small), z = b.add(small);

            double cNearest = t.doubleValue(), dNearest = x.doubleValue(),
                    eNearest = y.doubleValue(), fNearest = z.doubleValue();
            assertEquals(dPrevious, cNearest, 0);
            assertEquals(d, dNearest, 0);
            assertEquals(d, eNearest, 0);
            assertEquals(dNext, fNearest, 0);
        }
    }

    public void testFloatRoundFloor() {
        f = 0.7596475F;
        System.out.println("f = " + f);
        r = Rational.create(f);
        rr = new RoundRational(r);
        rr.setRoundingMode(RoundingMode.ROUND_FLOOR);
        dComputed = rr.floatValue();
        assertEquals(f, dComputed, 0);

        for (int i = 0; i < N; i++) {
            f = (float)random.nextGaussian();
            r = Rational.create(f);
            rr = new RoundRational(r);
            rr.setRoundingMode(RoundingMode.ROUND_FLOOR);
            dComputed = rr.floatValue();
            assertEquals(f, dComputed, 0);

            float fPrevious = Formulas.previous(f), fNext = Formulas.next(f);
            Rational dPreviousAsRational = Rational.create(fPrevious),
                    dAsRational = Rational.create(f), dNextAsRational = Rational.create(fNext),
                    a = dPreviousAsRational.add(dAsRational).multiplyTwoPower(-1),
                    b = dAsRational.add(dNextAsRational).multiplyTwoPower(-1);

            rr = new RoundRational(a);
            rr.setRoundingMode(RoundingMode.ROUND_FLOOR);
            float aFloor = rr.floatValue();

            rr = new RoundRational(b);
            rr.setRoundingMode(RoundingMode.ROUND_FLOOR);
            float bFloor = rr.floatValue();

            assertEquals(fPrevious, aFloor, 0);
            assertEquals(f, bFloor, 0);

            Rational small = Rational.DOUBLE_HALF_MIN_VALUE.multiplyTwoPower(-1024);
            Rational t = a.subtract(small), x = a.add(small), y = b.subtract(small), z = b.add(small);

            rr = new RoundRational(t);
            rr.setRoundingMode(RoundingMode.ROUND_FLOOR);
            float cFloor = rr.floatValue();

            rr = new RoundRational(x);
            rr.setRoundingMode(RoundingMode.ROUND_FLOOR);
            float dFloor = rr.floatValue();

            rr = new RoundRational(y);
            rr.setRoundingMode(RoundingMode.ROUND_FLOOR);
            float eFloor = rr.floatValue();

            rr = new RoundRational(z);
            rr.setRoundingMode(RoundingMode.ROUND_FLOOR);
            float fFloor = rr.floatValue();

            assertEquals(fPrevious, cFloor, 0);
            assertEquals(fPrevious, dFloor, 0);
            assertEquals(f, eFloor, 0);
            assertEquals(f, fFloor, 0);
        }
    }

    public void testFloatRoundCeil() {
        f = 0.7596475F;
        r = Rational.create(f);
        rr = new RoundRational(r);
        rr.setRoundingMode(RoundingMode.ROUND_CEILING);
        dComputed = rr.floatValue();
        assertEquals(f, dComputed, 0);

        for (int i = 0; i < N; i++) {
            f = (float)random.nextGaussian();
            r = Rational.create(f);
            rr = new RoundRational(r);
            rr.setRoundingMode(RoundingMode.ROUND_CEILING);
            dComputed = rr.floatValue();
            assertEquals(f, dComputed, 0);

            float fPrevious = Formulas.previous(f), fNext = Formulas.next(f);
            Rational dPreviousAsRational = Rational.create(fPrevious),
                    dAsRational = Rational.create(f), dNextAsRational = Rational.create(fNext),
                    a = dPreviousAsRational.add(dAsRational).multiplyTwoPower(-1),
                    b = dAsRational.add(dNextAsRational).multiplyTwoPower(-1);

            rr = new RoundRational(a);
            rr.setRoundingMode(RoundingMode.ROUND_CEILING);
            float aCeiling = rr.floatValue();

            rr = new RoundRational(b);
            rr.setRoundingMode(RoundingMode.ROUND_CEILING);
            float bCeiling = rr.floatValue();

            assertEquals(f, aCeiling, 0);
            assertEquals(fNext, bCeiling, 0);

            Rational small = Rational.DOUBLE_HALF_MIN_VALUE.multiplyTwoPower(-1024);
            Rational t = a.subtract(small), x = a.add(small), y = b.subtract(small), z = b.add(small);

            rr = new RoundRational(t);
            rr.setRoundingMode(RoundingMode.ROUND_CEILING);
            float cCeiling = rr.floatValue();

            rr = new RoundRational(x);
            rr.setRoundingMode(RoundingMode.ROUND_CEILING);
            float dCeiling = rr.floatValue();

            rr = new RoundRational(y);
            rr.setRoundingMode(RoundingMode.ROUND_CEILING);
            float eCeiling = rr.floatValue();

            rr = new RoundRational(z);
            rr.setRoundingMode(RoundingMode.ROUND_CEILING);
            float fCeiling = rr.floatValue();

            assertEquals(f, cCeiling, 0);
            assertEquals(f, dCeiling, 0);
            assertEquals(fNext, eCeiling, 0);
            assertEquals(fNext, fCeiling, 0);
        }
    }

    public void testFloatRoundNearest() {
        f = 0.7596475F;
        r = Rational.create(f);
        rr = new RoundRational(r);
        rr.setRoundingMode(RoundingMode.ROUND_HALF_EVEN);
        dComputed = rr.floatValue();
        assertEquals(f, dComputed, 0);

        for (int i = 0; i < N; i++) {
            f = (float)random.nextGaussian();
            r = Rational.create(f);
            dComputed = r.floatValue();
            assertEquals(f, dComputed, 0);

            float fPrevious = Formulas.previous(f), fNext = Formulas.next(f);
            Rational dPreviousAsRational = Rational.create(fPrevious),
                    dAsRational = Rational.create(f), dNextAsRational = Rational.create(fNext),
                    a = dPreviousAsRational.add(dAsRational).multiplyTwoPower(-1),
                    b = dAsRational.add(dNextAsRational).multiplyTwoPower(-1);
            float aNearest = a.floatValue(), bNearest = b.floatValue();

            int bits = Float.floatToIntBits(f);
            if ((bits & 1) == 0) {
                assertEquals(f, aNearest, 0);
                assertEquals(f, bNearest, 0);
            } else {
                assertEquals(fPrevious, aNearest, 0);
                assertEquals(fNext, bNearest, 0);
            }

            Rational small = Rational.DOUBLE_HALF_MIN_VALUE.multiplyTwoPower(-1024);
            Rational t = a.subtract(small), x = a.add(small), y = b.subtract(small), z = b.add(small);

            float cNearest = t.floatValue(), dNearest = x.floatValue(),
                    eNearest = y.floatValue(), fNearest = z.floatValue();
            assertEquals(fPrevious, cNearest, 0);
            assertEquals(f, dNearest, 0);
            assertEquals(f, eNearest, 0);
            assertEquals(fNext, fNearest, 0);
        }
    }

}
