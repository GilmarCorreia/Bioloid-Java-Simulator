package tests.precisefloating;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import junit.framework.TestCase;
import precisefloating.DoublePrecisionNo;
import precisefloating.Formulas;

import java.util.logging.Logger;

/**
 * Unit tests for DoublePrecisionNo.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class DoublePrecisionNoTest extends TestCase {

    private static final Logger log = Logger.getLogger(DoublePrecisionNoTest.class.getName());

    protected void setUp() throws Exception {
    }

    public void testDoubleParsing() {
        // infiniy should work fine
        for (int i = Formulas.DOUBLE_MIN_TWO_EXPONENT; i < Formulas.DOUBLE_MAX_TWO_EXPONENT + 1; i++) {
            double d = Formulas.pow2(i);
            double reborn = new DoublePrecisionNo(d).exactFiniteDoubleValue();
            assertEquals(d, reborn, 0);

            d = -d;
            reborn = new DoublePrecisionNo(d).exactFiniteDoubleValue();
            assertEquals(d, reborn, 0);
        }
    }

    public void testOverflowNotAccepted() {
        try {
            new DoublePrecisionNo(Long.MIN_VALUE, Integer.MAX_VALUE - 62);
            fail("ArithmeticException expected");
        } catch (ArithmeticException e) {
            log.finer(e.toString());
        }
    }

    public void testCompareTo() {
        DoublePrecisionNo x, y;

        x = new DoublePrecisionNo(1, Integer.MIN_VALUE);
        y = new DoublePrecisionNo(1, Integer.MAX_VALUE);
        assertTrue(x.compareTo(y) < 0);

        x = new DoublePrecisionNo(-1, Integer.MIN_VALUE);
        y = new DoublePrecisionNo(-1, Integer.MAX_VALUE);
        assertTrue(x.compareTo(y) > 0);

        x = new DoublePrecisionNo(Long.MAX_VALUE, Integer.MIN_VALUE);
        y = new DoublePrecisionNo(Long.MAX_VALUE, Integer.MAX_VALUE);
        assertTrue(x.compareTo(y) < 0);

        x = new DoublePrecisionNo(Long.MIN_VALUE + 1, Integer.MIN_VALUE);
        y = new DoublePrecisionNo(Long.MIN_VALUE + 1, Integer.MAX_VALUE);
        assertTrue(x.compareTo(y) > 0);

        x = new DoublePrecisionNo(Long.MIN_VALUE + 1, Integer.MIN_VALUE);
        y = new DoublePrecisionNo(Long.MAX_VALUE, Integer.MAX_VALUE);
        assertTrue(x.compareTo(y) < 0);
    }

    public void testMakeOdd() {
        int e = -1;
        Monitor mon;

        mon = MonitorFactory.start("fast");
        for (int i = -100000; i <= 100000; i++) {
            if (i != 0) {
                e = slowMakeOddFraction(i, 1000);
                int e1 = new DoublePrecisionNo(i, 1000).getExponent();
                assertEquals(e, e1);
            }
        }
        mon.stop();
        log.info(mon.toString());

        assertEquals(1005, e);
    }

    protected void tearDown() throws Exception {
    }

    private static int slowMakeOddFraction(long m, int e) {
        assert m != 0;

        int lowZeroBitCount = 0;
        while (m % 2 == 0) {
            m >>= 1;
            lowZeroBitCount++;
        }

        if ((long) Integer.MAX_VALUE - (long) lowZeroBitCount < (long) e) {
            throw new ArithmeticException("Cannot makeOddFraction number");
        } else {
            e += lowZeroBitCount;
        }

        return e;
    }

}
