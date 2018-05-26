package tests.precisefloating.directedrounding;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import junit.framework.TestCase;
import precisefloating.DoublePrecisionNo;
import precisefloating.Formulas;
import precisefloating.directedrounding.ManagedRoundingModeControl;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Unit tests for ManagedRoundingModeControl.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class ManagedRoundingModeControlTest extends TestCase {

    protected ManagedRoundingModeControl r = new ManagedRoundingModeControl();
    double x, y;
    double[] a = new double[2];

    private static final Logger log = Logger.getLogger(ManagedRoundingModeControlTest.class.getName());

    public void testAdd() {
        double x, y;
        double[] a = new double[2];

        x = Double.MAX_VALUE;
        y = -Double.MAX_VALUE;
        r.add(x, y, a, 0);
        assertEquals(Formulas.DOUBLE_NEGATIVE_ZERO_BITS, Double.doubleToLongBits(a[0]));
        assertEquals(Formulas.DOUBLE_POSITIVE_ZERO_BITS, Double.doubleToLongBits(a[1]));

        x = +0.0;
        y = +0.0;
        r.add(x, y, a, 0);
        assertEquals(Formulas.DOUBLE_POSITIVE_ZERO_BITS, Double.doubleToLongBits(a[0]));
        assertEquals(Formulas.DOUBLE_POSITIVE_ZERO_BITS, Double.doubleToLongBits(a[1]));

        x = -0.0;
        y = -0.0;
        r.add(x, y, a, 0);
        assertEquals(Formulas.DOUBLE_NEGATIVE_ZERO_BITS, Double.doubleToLongBits(a[0]));
        assertEquals(Formulas.DOUBLE_NEGATIVE_ZERO_BITS, Double.doubleToLongBits(a[1]));

        x = Double.MAX_VALUE;
        y = Double.MIN_VALUE;
        r.add(x, y, a, 0);
        assertEquals(Double.MAX_VALUE, a[0], 0);
        assertEquals(Double.POSITIVE_INFINITY, a[1], 0);

        x = -Double.MAX_VALUE;
        y = -Double.MIN_VALUE;

        r.add(x, y, a, 0);
        assertEquals(Double.NEGATIVE_INFINITY, a[0], 0);
        assertEquals(-Double.MAX_VALUE, a[1], 0);

        x = Formulas.DOUBLE_MIN_NORMAL;
        y = -Double.MIN_VALUE;

        r.add(x, y, a, 0);
        assertEquals(Formulas.DOUBLE_MAX_SUBNORMAL, a[0], 0);
        assertEquals(Formulas.DOUBLE_MAX_SUBNORMAL, a[1], 0);

        long step = 1L << 39;

        Monitor mon = null;

        // from +0.0 to Formulas.DOUBLE_MIN_NORMAL the numbers are equally spaced with distance Double.MIN_VALUE
        for (long bits = Formulas.DOUBLE_POSITIVE_ZERO_BITS + step; bits <= Formulas.DOUBLE_MIN_NORMAL_BITS; bits += step) {
            double current = Double.longBitsToDouble(bits);
            double previous = Double.longBitsToDouble(bits - 1);

            x = current;
            y = -Double.MIN_VALUE;

            mon = MonitorFactory.start("+0.0 .. DOUBLE_MIN_NORMAL");
            r.add(x, y, a, 0);
            mon.stop();

            assertEquals(previous, a[0], 0);
            assertEquals(previous, a[1], 0);

            x = previous;
            y = -current;

            mon = MonitorFactory.start("+0.0 .. DOUBLE_MIN_NORMAL");
            r.add(x, y, a, 0);
            mon.stop();

            assertEquals(-Double.MIN_VALUE, a[0], 0);
            assertEquals(-Double.MIN_VALUE, a[1], 0);
        }

        log.info(mon.toString());
    }

    public void testMultiply() {
        double x, y;
        double[] a = new double[2];

        x = Double.MAX_VALUE;
        y = Double.MIN_VALUE;

        r.multiply(x, y, a, 0);
        assertEquals((2 - Formulas.pow2(-52)) * Formulas.pow2(-51), a[0], 0);
        assertEquals(a[0], a[1], 0);

        x = Double.MIN_VALUE;
        y = -0.5;

        r.multiply(x, y, a, 0);
        assertEquals(-Double.MIN_VALUE, a[0], 0);
        assertEquals(0, a[1], 0);

        x = Double.MAX_VALUE;
        y = Formulas.next(1);

        r.multiply(x, y, a, 0);
        assertEquals(Double.MAX_VALUE, a[0], 0);
        assertEquals(Double.POSITIVE_INFINITY, a[1], 0);

        x = Double.MAX_VALUE;
        y = Formulas.previous(-1);

        r.multiply(x, y, a, 0);
        assertEquals(Double.NEGATIVE_INFINITY, a[0], 0);
        assertEquals(-Double.MAX_VALUE, a[1], 0);

        // bigint
        x = Formulas.next(Formulas.pow2(31));
        y = Formulas.next(Formulas.pow2(32));

        r.multiply(x, y, a, 0);
        assertEquals(Formulas.next(a[0]), a[1], 0);
    }

    public void testMultiplySpeed() {
        final int n = 2000000;

        Formulas.class.toString();

        double x, y;
        Monitor mon;

        x = Formulas.next(Formulas.pow2(31));
        y = Formulas.next(Formulas.pow2(32));

        Monitor mon1 = MonitorFactory.start("slow");
        for (int i = 0; i < n; i++) {
            r.multiply(x, y, a, 0);
        }
        mon1.stop();
        log.info(mon1.toString());

        System.out.println("*********************************************");

        x = 1000;
        y = 0.0001;

        mon = MonitorFactory.start("fast");
        for (int i = 0; i < n; i++) {
            r.multiply(x, y, a, 0);
        }
        mon.stop();
        log.info(mon.toString());
    }

    public void testAddSpeed() {
        final int n = 2000000;

        Formulas.class.toString();

        double x, y;
        double[] a = new double[2];
        Monitor mon;

        x = 1000;
        y = 0.0001;

        Monitor mon1 = MonitorFactory.start("slow");
        for (int i = 0; i < n; i++) {
            r.add(x, y, a, 0);
        }
        mon1.stop();
        log.info(mon1.toString());

        System.out.println("*********************************************");

        x = 1000;
        y = 1;

        mon = MonitorFactory.start("fast");
        for (int i = 0; i < n; i++) {
            r.add(x, y, a, 0);
        }
        mon.stop();
        log.info(mon.toString());
    }

    public void testDivide() {
        x = (2 - Formulas.pow2(-52)) * Formulas.pow2(-51);
        y = Double.MIN_VALUE;

        r.divide(x, y, a, 0);
        assertEquals(Double.MAX_VALUE, a[0], 0);
        assertEquals(a[0], a[1], 0);

        x = Double.MIN_VALUE;
        y = -2;

        r.divide(x, y, a, 0);
        assertEquals(-Double.MIN_VALUE, a[0], 0);
        assertEquals(0, a[1], 0);

        x = Double.MAX_VALUE;
        y = Formulas.previous(1);

        r.divide(x, y, a, 0);
        assertEquals(Double.MAX_VALUE, a[0], 0);
        assertEquals(Double.POSITIVE_INFINITY, a[1], 0);

        x = Double.MAX_VALUE;
        y = Formulas.next(-1);

        r.divide(x, y, a, 0);
        assertEquals(Double.NEGATIVE_INFINITY, a[0], 0);
        assertEquals(-Double.MAX_VALUE, a[1], 0);

        // bigint
        x = Formulas.next(Formulas.pow2(31));
        y = Formulas.previous(Formulas.pow2(-32));

        r.divide(x, y, a, 0);
        assertEquals(Formulas.next(a[0]), a[1], 0);
    }

    public void testSqrt() {
        exactSqrt(Double.MIN_VALUE, a);

        for (int i = Formulas.DOUBLE_MIN_TWO_EXPONENT; i < Formulas.DOUBLE_MAX_TWO_EXPONENT; i += 2) {
            assert i % 2 == 0;

            r.sqrt(Formulas.pow2(i), a, 0);
            double exact = Formulas.pow2(i / 2);
            assertEquals(exact, a[0], 0);
            assertEquals(exact, a[1], 0);

            double y = Formulas.next(Formulas.pow2(i));
            r.sqrt(y, a, 0);
            double nearest = Math.sqrt(y);
            assertTrue(a[0] < a[1]);
            assertTrue(nearest == a[0] || nearest == a[1]);
        }
    }

    private void exactSqrt(double x, double[] a) {
        r.sqrt(x, a, 0);
        assertEquals(a[0], a[1], 0);
        System.out.println("a[0] = " + a[0]);
        System.out.println("a[1] = " + a[1]);

        r.square(a[0], a, 0);
        assertEquals(x, a[0], 0);
        assertEquals(x, a[1], 0);
    }

    public void testExp() {
        x = 1.5;
        r.exp(x, a, 0);
        assertTrue(a[0] == Math.exp(x) || a[1] == Math.exp(x));

        x = Double.MAX_VALUE;
        r.exp(x, a, 0);
        assertEquals(Double.MAX_VALUE, a[0], 0);
        assertEquals(Double.POSITIVE_INFINITY, a[1], 0);

        x = 710;
        r.exp(x, a, 0);
        assertEquals(Double.MAX_VALUE, a[0], 0);
        assertEquals(Double.POSITIVE_INFINITY, a[1], 0);

        x = -745;
        r.exp(x, a, 0);
        assertEquals(0, a[0], 0);
        assertEquals(Double.MIN_VALUE, a[1], 0);
    }

    public void testExpSpeed() {
        // -1 .. 217 for eradical
        x = 709.782712893383;
        r.exp(x, a, 0);
        assertTrue(a[1] < Double.MAX_VALUE);
        assertTrue(a[0] < a[1]);

        x = Formulas.next(x);
        r.exp(x, a, 0);
        assertTrue(a[1] < Double.MAX_VALUE);
        assertTrue(a[0] < a[1]);

        x = Formulas.next(x);
        r.exp(x, a, 0);
        assertTrue(a[1] < Double.MAX_VALUE);
        assertTrue(a[0] < a[1]);
    }

    public static void main(String[] args) throws IOException {
        new ManagedRoundingModeControlTest().testManyOnes();
    }

    public void testSmallPositive() {
        x = Formulas.previous(ManagedRoundingModeControl.EXP_SMALL_ONE);
        r.exp(x, a, 0);
        assertTrue(a[0] == 1);

        x = ManagedRoundingModeControl.EXP_SMALL_ONE;
        r.exp(x, a, 0);
        assertTrue(a[0] > 1);
    }

    public void testLargePositive() throws IOException {
        x = Math.log(Double.MAX_VALUE);
        System.out.println("1: " + x);
        r.exp(x, a, 0);
        assertTrue(a[1] < Double.POSITIVE_INFINITY);

        x = Formulas.next(x);
        System.out.println("2: " + x);
        r.exp(x, a, 0);
        assertEquals(Double.POSITIVE_INFINITY, a[1], 0);
    }

    public void testSmallMagnitudeNegative() {
        x = -Double.MIN_VALUE;
        r.exp(x, a, 0);
        assertTrue(a[1] == 1);

        x = ManagedRoundingModeControl.EXP_BIG_ONE;
        r.exp(x, a, 0);
        assertTrue(a[1] == 1);

        x = Formulas.previous(x);
        r.exp(x, a, 0);
        assertTrue(a[1] < 1);
    }

    public void testLargeMagnitudeNegative() {
        x = ManagedRoundingModeControl.EXP_SMALL_ZERO;
        r.exp(x, a, 0);
        assertEquals(0, a[0], 0);
        assertEquals(Double.MIN_VALUE, a[1], 0);

        x = Formulas.next(ManagedRoundingModeControl.EXP_SMALL_ZERO);
        r.exp(x, a, 0);
        assertTrue(a[0] == Double.MIN_VALUE);
    }

    public void testManyOnes() {
        short expSmallZeroExponentBits = DoublePrecisionNo.extractExponentBits(
                ManagedRoundingModeControl.EXP_SMALL_ZERO);
        int expSmallZeroExponent = expSmallZeroExponentBits - Formulas.DOUBLE_BIAS;
        short expBigOneExponentBits = DoublePrecisionNo.extractExponentBits(
                ManagedRoundingModeControl.EXP_BIG_ONE);
        int expBigOneExponent = expBigOneExponentBits - Formulas.DOUBLE_BIAS;
        short expSmallOneExponentBits = DoublePrecisionNo.extractExponentBits(
                ManagedRoundingModeControl.EXP_SMALL_ONE);
        int expSmallOneExponent = expSmallOneExponentBits - Formulas.DOUBLE_BIAS;
        short expPositiveInfinityExponentBits = DoublePrecisionNo.extractExponentBits(
                ManagedRoundingModeControl.EXP_POSITIVE_INFINITY);
        int expPositiveInfinityExponent = expPositiveInfinityExponentBits - Formulas.DOUBLE_BIAS;

        System.out.println("expSmallZeroExponentBits = " + expSmallZeroExponentBits);
        System.out.println("expBigOneExponentBits = " + expBigOneExponentBits);
        System.out.println("expSmallOneExponentBits = " + expSmallOneExponentBits);
        System.out.println("expPositiveInfinityExponentBits = " + expPositiveInfinityExponentBits);

        System.out.println("expSmallZeroExponent = " + expSmallZeroExponent);
        System.out.println("expBigOneExponent = " + expBigOneExponent);
        System.out.println("expSmallOneExponent = " + expSmallOneExponent);
        System.out.println("expPositiveInfinityExponent = " + expPositiveInfinityExponent);

        for (int i = -expSmallZeroExponent; i > expBigOneExponent; i--) {
            double p = -Formulas.pow2(i);
            double x = Formulas.next(p);
            measureExp(i, x);
        }

        for (int i = expSmallOneExponent + 1; i <= expPositiveInfinityExponent; i++) {
            double p = Formulas.pow2(i);
            double x = Formulas.previous(p);
            measureExp(i, x);
        }
    }

    private void measureExp(int i, double x) {
        System.out.println("i = " + i + " x = " + x
                + " = 0x" + Long.toHexString(Double.doubleToLongBits(x)));

        double mathExp = Math.exp(x);
//        System.out.println("Math.exp(x) = " + mathExp);

        Monitor mon = MonitorFactory.start();
        r.exp(x, a, 0);
        mon.stop();
        assert a[0] == mathExp || a[1] == mathExp;
        System.out.println("mon = " + mon);
    }

}
