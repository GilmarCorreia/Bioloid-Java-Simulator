package tests.precisefloating;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import junit.framework.TestCase;
import precisefloating.Formulas;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class FormulasTest extends TestCase {

    private double[] doubleTemp = new double[2];

    private static final Logger log = Logger.getLogger(FormulasTest.class.getName());

    protected void setUp() throws Exception {
        Arrays.fill(doubleTemp, 0);
    }

    protected void tearDown() throws Exception {
        Arrays.fill(doubleTemp, 0);
    }

    public void testDoublePrecision() {
        double[] x = new double[10];
        Arrays.fill(x, 0.1);

        double sum = Formulas.kahanSummation(x);
        log.info("sum of 10 number equal to 0.1 is " + sum);
        log.info("sum == 1.0  " + (sum == 1));
    }

    public void testSinglePrecision() {
        float[] x = new float[10];
        Arrays.fill(x, 0.1F);

        double sum = Formulas.kahanSummation(x);
        float singlePrecisionSum = (float) sum;
        log.info("singlePrecisionSum of 10 number equal to 0.1F is " + singlePrecisionSum);
        log.info("singlePrecisionSum == 1.0F  " + (singlePrecisionSum == 1));
    }

    public void testMaxTwoPower() {
        double maxTwoPower = Formulas.pow2(1023);

        assertTrue(!Double.isInfinite(maxTwoPower));
        assertTrue(!Double.isNaN(maxTwoPower));
        assertEquals(Double.POSITIVE_INFINITY, maxTwoPower * 2, 0);
    }

    public void testHighestSetBit() {
        assertEquals(-1, Formulas.highestSetBit(0));
        assertEquals(62, Formulas.highestSetBit(0x7FFFFFFFFFFFFFFFL));

        for (long i = 0; i <= Long.MAX_VALUE - Long.MAX_VALUE / 10000000 - 1; i += Long.MAX_VALUE / 10000000) {
            assertEquals(slowHighestSetBit(i), Formulas.highestSetBit(i));
        }

        Monitor slowMon = MonitorFactory.start("slow");
        for (long i = 0; i <= Long.MAX_VALUE - Long.MAX_VALUE / 10000000 - 1; i += Long.MAX_VALUE / 10000000) {
            slowHighestSetBit(i);
        }
        slowMon.stop();
        log.info("slow " + slowMon);

        Monitor fastMon = MonitorFactory.start("fast");
        for (long i = 0; i <= Long.MAX_VALUE - Long.MAX_VALUE / 10000000 - 1; i += Long.MAX_VALUE / 10000000) {
            Formulas.highestSetBit(i);
        }
        fastMon.stop();
        log.info("fast " + fastMon.toString());

        try {
            Formulas.highestSetBit(-1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testPrevious() {
        assertEquals(Formulas.DOUBLE_MAX_SUBNORMAL, Formulas.previous(Formulas.DOUBLE_MIN_NORMAL), 0);
        assertEquals(-Formulas.DOUBLE_MIN_NORMAL, Formulas.previous(-Formulas.DOUBLE_MAX_SUBNORMAL), 0);
        assertEquals(0, Formulas.previous(Double.MIN_VALUE), 0);
        assertEquals(Formulas.DOUBLE_NEGATIVE_ZERO_BITS, Double.doubleToLongBits(Formulas.previous(0.0)));
        assertEquals(-Double.MIN_VALUE, Formulas.previous(-0.0), 0);
        assertEquals(Double.NEGATIVE_INFINITY, Formulas.previous(-Double.MAX_VALUE), 0);
    }

    public void testNext() {
        assertEquals(Formulas.DOUBLE_MIN_NORMAL, Formulas.next(Formulas.DOUBLE_MAX_SUBNORMAL), 0);
        assertEquals(-Formulas.DOUBLE_MAX_SUBNORMAL, Formulas.next(-Formulas.DOUBLE_MIN_NORMAL), 0);
        assertEquals(Float.MIN_VALUE, Formulas.next(0), 0);
        assertEquals(Float.MIN_VALUE, Formulas.next(0L), 0);
        assertEquals(Float.MIN_VALUE, Formulas.next(0.0F), 0);
        assertEquals(Double.MIN_VALUE, Formulas.next(0.0), 0);
        assertEquals(Formulas.DOUBLE_POSITIVE_ZERO_BITS, Double.doubleToLongBits(Formulas.next(-0.0)));
        assertEquals(-0.0, Formulas.next(-Double.MIN_VALUE), 0);
        assertEquals(Double.POSITIVE_INFINITY, Formulas.next(Double.MAX_VALUE), 0);
    }

    public void testGetBytes() {
        for (int i = -100; i < 100; i++) {
            byte[] bytes = Formulas.getBytes(i);
            BigInteger bigInt = new BigInteger(bytes);
            assert(bigInt.intValue() == i);
        }
    }

    public void testLowestSetBit() {
        assertEquals(-1, Formulas.lowestSetBit(0));
        assertEquals(0, Formulas.lowestSetBit(1));
        assertEquals(63, Formulas.lowestSetBit(Long.MIN_VALUE));

        for (long i = Long.MIN_VALUE; i <= Long.MAX_VALUE - Long.MAX_VALUE / 10000000 - 1; i += Long.MAX_VALUE / 10000000) {
            assertEquals(slowLowestSetBit(i), Formulas.lowestSetBit(i));
        }

        Monitor slowMon = MonitorFactory.start("slow");
        for (long i = Long.MIN_VALUE; i <= Long.MAX_VALUE - Long.MAX_VALUE / 10000000 - 1; i += Long.MAX_VALUE / 10000000) {
            slowLowestSetBit(i);
        }
        slowMon.stop();
        log.info("slow " + slowMon);

        Monitor fastMon = MonitorFactory.start("fast");
        for (long i = Long.MIN_VALUE; i <= Long.MAX_VALUE - Long.MAX_VALUE / 10000000 - 1; i += Long.MAX_VALUE / 10000000) {
            Formulas.lowestSetBit(i);
        }
        fastMon.stop();
        log.info("fast " + fastMon.toString());
    }

    private static byte slowLowestSetBit(long x) {
        if (x == 0) {
            return -1;
        }

        for (byte i = 0; i <= 63; i++) {
            if ((x & (1L << i)) != 0) {
                return i;
            }
        }

        throw new Error("impossible");
    }

    private static byte slowHighestSetBit(long x) {
        if (x < 0) {
            throw new IllegalArgumentException("x must be positive or zero");
        }

        byte i;

        for (i = 62; i >= 0; i--) {
            if ((x & (1L << i)) != 0) {
                return i;
            }
        }

        assert x == 0;

        return -1;
    }

    public void testSingleNearestInfinityBounds() {
        assertEquals(Float.MAX_VALUE,
                (float) Formulas.previous(Formulas.SINGLE_SMALLEST_NEAREST_POSITIVE_INFINITY), 0);
        assertEquals(Float.POSITIVE_INFINITY,
                (float) Formulas.SINGLE_SMALLEST_NEAREST_POSITIVE_INFINITY, 0);
        assertEquals(Float.POSITIVE_INFINITY,
                (float) (Formulas.next(Formulas.SINGLE_SMALLEST_NEAREST_POSITIVE_INFINITY)), 0);

        assertEquals(Float.NEGATIVE_INFINITY,
                (float) (Formulas.previous(Formulas.SINGLE_LARGEST_NEAREST_NEGATIVE_INFINITY)), 0);
        assertEquals(Float.NEGATIVE_INFINITY,
                (float) Formulas.SINGLE_LARGEST_NEAREST_NEGATIVE_INFINITY, 0);
        assertEquals(-Float.MAX_VALUE,
                (float) Formulas.next(Formulas.SINGLE_LARGEST_NEAREST_NEGATIVE_INFINITY), 0);
    }

    public void testMaxIntegers() {
        assertEquals(Formulas.FLOAT_MAX_EXACT_INT, (int) (float) Formulas.FLOAT_MAX_EXACT_INT);
        assertEquals(Formulas.FLOAT_MAX_EXACT_INT, Formulas.pow2(Formulas.N_FLOAT), 0);
        assertEquals(Formulas.FLOAT_MAX_EXACT_INT, (float) (Formulas.FLOAT_MAX_EXACT_INT + 1), 0);
        assertEquals(Formulas.FLOAT_MAX_EXACT_INT - 1, (int) Formulas.previous(Formulas.FLOAT_MAX_EXACT_INT));
        assertEquals(Formulas.FLOAT_MAX_EXACT_INT + 2, (int) Formulas.next(Formulas.FLOAT_MAX_EXACT_INT));

        assertEquals(Formulas.DOUBLE_MAX_EXACT_LONG, (long) (double) Formulas.DOUBLE_MAX_EXACT_LONG);
        assertEquals(Formulas.DOUBLE_MAX_EXACT_LONG, Formulas.pow2(Formulas.N_DOUBLE), 0);
        assertEquals(Formulas.DOUBLE_MAX_EXACT_LONG, (double) (Formulas.DOUBLE_MAX_EXACT_LONG + 1), 0);
        assertEquals(Formulas.DOUBLE_MAX_EXACT_LONG - 1, (long) Formulas.previous((double) Formulas.DOUBLE_MAX_EXACT_LONG));
        assertEquals(Formulas.DOUBLE_MAX_EXACT_LONG + 2, (long) Formulas.next((double) Formulas.DOUBLE_MAX_EXACT_LONG));
    }

    public void testFloor() {
        float f;
        float floor;

        f = +0.0F;
        floor = Formulas.floor(f);
        assertEquals(Formulas.FLOAT_POSITIVE_ZERO_BITS, Float.floatToIntBits(floor));

        f = -0.0F;
        floor = Formulas.floor(f);
        assertEquals(Formulas.FLOAT_NEGATIVE_ZERO_BITS, Float.floatToIntBits(floor));

        f = Float.NaN;
        floor = Formulas.floor(f);
        assertTrue(Float.isNaN(floor));
        assertEquals(Float.floatToRawIntBits(f), Float.floatToRawIntBits(floor));

        f = Float.POSITIVE_INFINITY;
        floor = Formulas.floor(f);
        assertEquals(f, floor, 0);

        f = Float.NEGATIVE_INFINITY;
        floor = Formulas.floor(f);
        assertEquals(f, floor, 0);

        f = Float.MAX_VALUE;
        floor = Formulas.floor(f);
        assertEquals(f, floor, 0);

        f = -Float.MAX_VALUE;
        floor = Formulas.floor(f);
        assertEquals(f, floor, 0);

        f = Float.MIN_VALUE;
        floor = Formulas.floor(f);
        assertEquals(Formulas.FLOAT_POSITIVE_ZERO_BITS, Float.floatToIntBits(floor));

        f = -Float.MIN_VALUE;
        floor = Formulas.floor(f);
        assertEquals(-1, floor, 0);

        f = Formulas.previous(Formulas.FLOAT_MAX_EXACT_INT);
        floor = Formulas.floor(f);
        assertEquals(Formulas.FLOAT_MAX_EXACT_INT - 1, floor, 0);

        f = Formulas.next(Formulas.FLOAT_MAX_EXACT_INT);
        floor = Formulas.floor(f);
        assertEquals(f, floor, 0);

        f = Formulas.next(-Formulas.FLOAT_MAX_EXACT_INT);
        floor = Formulas.floor(f);
        assertEquals(f, floor, 0);

        f = Formulas.previous(-Formulas.FLOAT_MAX_EXACT_INT);
        floor = Formulas.floor(f);
        assertEquals(f, floor, 0);
    }

    public void testDoubleInterval() {
        Formulas.doubleInterval(0L, doubleTemp);
        assertEquals(Formulas.DOUBLE_POSITIVE_ZERO_BITS, Double.doubleToLongBits(doubleTemp[0]));
        assertEquals(Formulas.DOUBLE_POSITIVE_ZERO_BITS, Double.doubleToLongBits(doubleTemp[1]));

        Formulas.doubleInterval(Long.MAX_VALUE, doubleTemp);
        assertEquals(Formulas.previous(Formulas.pow2(63)), doubleTemp[0], 0);
        assertEquals(Formulas.pow2(63), doubleTemp[1], 0);

        Formulas.doubleInterval(Long.MAX_VALUE - 1, doubleTemp);
        assertEquals(Formulas.previous(Formulas.pow2(63)), doubleTemp[0], 0);
        assertEquals(Formulas.pow2(63), doubleTemp[1], 0);

        Formulas.doubleInterval(Long.MIN_VALUE, doubleTemp);
        assertEquals(-Formulas.pow2(63), doubleTemp[0], 0);
        assertEquals(-Formulas.pow2(63), doubleTemp[1], 0);

        Formulas.doubleInterval(-Long.MAX_VALUE, doubleTemp);
        assertEquals(-Formulas.pow2(63), doubleTemp[0], 0);
        assertEquals(Formulas.next(-Formulas.pow2(63)), doubleTemp[1], 0);

        Formulas.doubleInterval(Formulas.DOUBLE_MAX_EXACT_LONG, doubleTemp);
        assertEquals(Formulas.pow2(Formulas.N_DOUBLE), doubleTemp[0], 0);
        assertEquals(Formulas.pow2(Formulas.N_DOUBLE), doubleTemp[1], 0);

        Formulas.doubleInterval(-Formulas.DOUBLE_MAX_EXACT_LONG, doubleTemp);
        assertEquals(-Formulas.pow2(Formulas.N_DOUBLE), doubleTemp[0], 0);
        assertEquals(-Formulas.pow2(Formulas.N_DOUBLE), doubleTemp[1], 0);

        Formulas.doubleInterval(Formulas.DOUBLE_MAX_EXACT_LONG + 1, doubleTemp);
        assertEquals(Formulas.pow2(Formulas.N_DOUBLE), doubleTemp[0], 0);
        assertEquals(Formulas.next(Formulas.pow2(Formulas.N_DOUBLE)), doubleTemp[1], 0);

        Formulas.doubleInterval(-Formulas.DOUBLE_MAX_EXACT_LONG - 1, doubleTemp);
        assertEquals(Formulas.previous(-Formulas.pow2(Formulas.N_DOUBLE)), doubleTemp[0], 0);
        assertEquals(-Formulas.pow2(Formulas.N_DOUBLE), doubleTemp[1], 0);

        Formulas.doubleInterval(1L << 62, doubleTemp);
        assertEquals(Formulas.pow2(62), doubleTemp[0], 0);
        assertEquals(Formulas.pow2(62), doubleTemp[1], 0);
    }

    public void testIsExactLong() {
        assertFalse(Formulas.isExactLong(Double.NaN));
        assertFalse(Formulas.isExactLong(Double.NEGATIVE_INFINITY));
        assertFalse(Formulas.isExactLong(-Double.MAX_VALUE));
        assertFalse(Formulas.isExactLong(-0.5));
        assertFalse(Formulas.isExactLong(-Double.MIN_VALUE));
        assertFalse(Formulas.isExactLong(Double.MIN_VALUE));
        assertFalse(Formulas.isExactLong(0.5));
        assertFalse(Formulas.isExactLong(Double.MAX_VALUE));
        assertFalse(Formulas.isExactLong(Double.POSITIVE_INFINITY));

        assertTrue(Formulas.isExactLong(Formulas.previous(-Formulas.DOUBLE_MAX_EXACT_LONG)));
        assertTrue(Formulas.isExactLong(-Formulas.DOUBLE_MAX_EXACT_LONG));
        assertTrue(Formulas.isExactLong(Formulas.next(-Formulas.DOUBLE_MAX_EXACT_LONG)));
        assertTrue(Formulas.isExactLong(Formulas.previous((double) -(1L << (Formulas.N_DOUBLE - 1)))));
        assertFalse(Formulas.isExactLong(Formulas.next((double) -(1L << (Formulas.N_DOUBLE - 1)))));
        assertTrue(Formulas.isExactLong((double) -(1L << (Formulas.N_DOUBLE - 1))));

        assertTrue(Formulas.isExactLong(Formulas.next(Formulas.DOUBLE_MAX_EXACT_LONG)));
        assertTrue(Formulas.isExactLong(Formulas.DOUBLE_MAX_EXACT_LONG));
        assertTrue(Formulas.isExactLong(Formulas.previous(Formulas.DOUBLE_MAX_EXACT_LONG)));
        assertTrue(Formulas.isExactLong(Formulas.next((double) (1L << (Formulas.N_DOUBLE - 1)))));
        assertFalse(Formulas.isExactLong(Formulas.previous((double) (1L << (Formulas.N_DOUBLE - 1)))));
        assertTrue(Formulas.isExactLong((double) (1L << (Formulas.N_DOUBLE - 1))));
    }

    public void testLessThanOrEqual() {
        assertFalse(Formulas.lessThanOrEqual(Long.MAX_VALUE, Formulas.previous(Formulas.pow2(63))));
        assertTrue(Formulas.lessThanOrEqual(Long.MAX_VALUE, Formulas.pow2(63)));
        assertTrue(Formulas.lessThanOrEqual(Long.MAX_VALUE, Formulas.next(Formulas.pow2(63))));

        assertTrue(Formulas.lessThanOrEqual(Long.MIN_VALUE, Formulas.previous(Formulas.pow2(63))));
        assertTrue(Formulas.lessThanOrEqual(Long.MIN_VALUE, -Formulas.pow2(63)));
        assertTrue(Formulas.lessThanOrEqual(Long.MIN_VALUE, Formulas.next(-Formulas.pow2(63))));

        try {
            Formulas.lessThanOrEqual(0, Double.NaN);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
        }

        assertTrue(Formulas.lessThanOrEqual(Long.MAX_VALUE, Double.POSITIVE_INFINITY));
        assertFalse(Formulas.lessThanOrEqual(Long.MAX_VALUE, Double.NEGATIVE_INFINITY));
        assertTrue(Formulas.lessThanOrEqual(0, -0.0));
        assertTrue(Formulas.lessThanOrEqual(0, +0.0));
    }

    public void testNormalSubnormal() {
        assert Formulas.pow2(-1022) == Formulas.DOUBLE_MIN_NORMAL;
        System.out.println("Formulas.pow2(-1022) = " + Formulas.pow2(-1022));
        System.out.println("Formulas.DOUBLE_MIN_NORMAL = " + Formulas.DOUBLE_MIN_NORMAL);
        assertTrue(Formulas.DOUBLE_MIN_NORMAL == Formulas.next(Formulas.DOUBLE_MAX_SUBNORMAL));
        assertTrue(Formulas.FLOAT_MIN_NORMAL == Formulas.next(Formulas.FLOAT_MAX_SUBNORMAL));
        assertEquals(Formulas.DOUBLE_MIN_NORMAL, Formulas.DOUBLE_MAX_SUBNORMAL + Double.MIN_VALUE, 0);
    }

    public void testFloatInterval() {
        float[] floatTemp = new float[2];

        // exact
        double d = ((double) Formulas.FLOAT_MAX_SUBNORMAL + (double) Formulas.FLOAT_MIN_NORMAL) / 2;
        Formulas.floatInterval(d, floatTemp);
        assertEquals(Formulas.FLOAT_MAX_SUBNORMAL, floatTemp[0], 0);
        assertEquals(Formulas.FLOAT_MIN_NORMAL, floatTemp[1], 0);

        d = 0.6902357277154542;
        Formulas.floatInterval(d, floatTemp);
        System.out.println("floatTemp[0] = " + floatTemp[0]);
        System.out.println("floatTemp[1] = " + floatTemp[1]);
    }

    public void testFloatMax() {
        double d = (double)Float.MAX_VALUE + Formulas.pow2(
                Formulas.FLOAT_MAX_TWO_EXPONENT - Formulas.N_FLOAT);

        float f = (float)d;
        assertEquals(Float.POSITIVE_INFINITY, f, 0);

        f = (float)Formulas.previous(d);
        assertEquals(Float.MAX_VALUE, f, 0);

        f = (float)Formulas.pow2(Formulas.FLOAT_MAX_TWO_EXPONENT) * 2;
        assertEquals(Float.POSITIVE_INFINITY, f, 0);

        d = Formulas.pow2(Formulas.DOUBLE_MAX_TWO_EXPONENT) * 2;
        assertEquals(Double.POSITIVE_INFINITY, d, 0);
    }

    public void testOperators() {
        for (int i = Short.MIN_VALUE; i <= Short.MAX_VALUE; i++) {
            short s = (short)i;

            short expected = (short) (s & (short)-s);
            short actual = (short)(s & -s);

            assertEquals(expected, actual);
        }
    }

    public void testT() {
        System.out.println("Double.doubleToRawLongBits(Double.NEGATIVE_INFINITY) = " + Long.toHexString(Double.doubleToRawLongBits(Double.NEGATIVE_INFINITY)));
        System.out.println("Double.doubleToRawLongBits(Double.POSITIVE_INFINITY) = " + Long.toHexString(Double.doubleToRawLongBits(Double.POSITIVE_INFINITY)));
    }

}
