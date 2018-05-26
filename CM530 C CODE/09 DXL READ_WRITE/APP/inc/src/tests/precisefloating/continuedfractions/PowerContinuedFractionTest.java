package tests.precisefloating.continuedfractions;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import junit.framework.TestCase;
import precisefloating.Rational;
import precisefloating.continuedfractions.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class PowerContinuedFractionTest extends TestCase {

    Rational r;
    RationalExpansion re;
    PartialQuotients pqi;
    Rational convergent;
    ContinuedFraction power;

    private static final Logger log = Logger.getLogger(PowerContinuedFractionTest.class.getName());

    public void testIntegers() {
        r = Rational.ZERO;
        re = new RationalExpansion(r);
        power = re.power(2);
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{0}, false, pqi);
        Convergents convergents = power.convergents();
        ContinuedFractionTestUtils.assertLastConvergentEquals(Rational.ZERO, convergents);
    }

    public void testNegativeIntegers() {
        r = Rational.valueOf(-3);
        re = new RationalExpansion(r);
        power = re.power(2);
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{9}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.square(), power.convergents());

        r = Rational.valueOf(-2);
        re = new RationalExpansion(r);
        power = re.power(2);
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{4}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.square(), power.convergents());

        r = Rational.valueOf(-1);
        re = new RationalExpansion(r);
        power = re.power(2);
        pqi = power.partialQuotients();
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.square(), power.convergents());
    }

    public void testPositiveIntegers() {
        r = Rational.valueOf(1);
        re = new RationalExpansion(r);
        power = re.power(2);
        pqi = power.partialQuotients();
        assertEquals(BigInteger.valueOf(1), pqi.nextPartialQuotient());
        assertFalse(pqi.hasNext());
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.square(), power.convergents());

        r = Rational.valueOf(2);
        re = new RationalExpansion(r);
        power = re.power(2);
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{4}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.square(), power.convergents());

        r = Rational.valueOf(3);
        re = new RationalExpansion(r);
        power = re.power(2);
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{9}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.square(), power.convergents());
    }

    public void testPositiveRationals() {
        r = Rational.create(789, 456);
        re = new RationalExpansion(r);
        power = re.power(2);
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{2, 1, 160, 1, 1, 3, 3, 1, 4}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.square(), power.convergents());

        r = Rational.create(-789, 456);
        re = new RationalExpansion(r);
        power = re.power(2);
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{2, 1, 160, 1, 1, 3, 3, 1, 4}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.square(), power.convergents());

        r = Rational.create(5, 23);
        re = new RationalExpansion(r);
        power = re.power(2);
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{0, 21, 6, 4}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.square(), power.convergents());
    }

    public void testNegativePowers() {
        r = Rational.create(789, 456);
        re = new RationalExpansion(r);
        power = re.power(-4);
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{0, 8, 1, 25, 1, 21, 2, 48, 11, 3, 4, 2, 1, 5, 1, 2},
                false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.power(-4), power.convergents());

        r = Rational.create(789, 456);
        re = new RationalExpansion(r);
        power = re.power(-8);
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{0, 80, 2, 1, 400, 1, 2, 1, 1, 1, 1, 1, 4, 1, 13, 1, 2, 1, 26, 1, 52, 1, 2, 1, 12, 1, 2, 3, 81, 1, 1, 1, 62, 2},
                false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.power(-8), power.convergents());

        // todo solve this bug !!!!

        r = Rational.create(1, 2);
        re = new RationalExpansion(r);
        power = re.power(1);
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{0, 2}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.power(1), power.convergents());

        r = Rational.create(1, 2);
        re = new RationalExpansion(r);
        power = re.power(2);
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{0, 4}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.power(2), power.convergents());

        r = Rational.create(1, 2);
        re = new RationalExpansion(r);
        power = re.power(3);
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{0, 8}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.power(3), power.convergents());

        r = Rational.create(789, 456);
        re = new RationalExpansion(r);
        power = re.power(3);
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{5, 5, 1, 1, 4, 4, 1, 3, 3, 37, 29}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.power(3), power.convergents());

        r = Rational.create(789, 456);
        re = new RationalExpansion(r);
        power = re.power(-5);
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{0, 15, 1, 1, 30, 15, 16, 1, 4, 1, 1, 5, 2, 3, 20, 1, 2, 4, 1, 2, 3, 1, 2},
                false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.power(-5), power.convergents());

        r = Rational.create(-789, 456);
        re = new RationalExpansion(r);
        power = re.power(2);
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(
                new long[]{2, 1, 160, 1, 1, 3, 3, 1, 4}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.square(), power.convergents());

        r = Rational.create(5, 23);
        re = new RationalExpansion(r);
        power = re.power(2);
        pqi = power.partialQuotients();
        ContinuedFractionTestUtils.assertStartsWith(new long[]{0, 21, 6, 4}, false, pqi);
        ContinuedFractionTestUtils.assertLastConvergentEquals(r.square(), power.convergents());
    }

    public void testPowerSpeed() {
        Random rnd = new Random(/*0*/);
        Monitor powerMon, multiplyMon;

        powerMon = multiplyMon = null;

        int bitCount = 10, y = 250;
        for (int i = 0; i < 4; i++) {
            r = new Rational(bitCount, rnd);
            Rational ry = r.power(y);
            // log.info("r = " + r);
            // log.info("ry = " + ry);
            re = new RationalExpansion(r);

            // log.info("start power");
            power = re.power(y);
            powerMon = MonitorFactory.start("rational power");

            ContinuedFractionTestUtils.assertLastConvergentEquals(ry, power.convergents());
            powerMon.stop();
            // log.info("end power");

            // log.info("start blind multiplication");
            power = re;
            for (int j = 1; j < y; j++) {
                power = power.multiply(re);
            }

            multiplyMon = MonitorFactory.start("rational power by multiplication");
            ContinuedFractionTestUtils.assertLastConvergentEquals(ry, power.convergents());
            multiplyMon.stop();
            // log.info("end blind multiplication");
        }

        log.info("rational power " + powerMon.toString());
        log.info("rational power by multiplication " + multiplyMon.toString());

        powerMon = multiplyMon = null;

        long[] expected;

        expected = new long[]{7, 2, 1, 1, 3, 18, 5, 1, 1, 6, 30, 8, 1, 1, 9, 42, 11, 1, 1, 12, 54, 14, 1, 1, 15, 66, 17, 1, 1, 18, 78, 20, 1, 1, 21, 90, 23, 1, 1, 24, 102, 26, 1, 1, 27, 114, 29, 1, 1, 30, 126, 32, 1, 1, 33, 138, 35, 1, 1, 36, 150, 38, 1, 1, 39, 162, 41, 1, 1, 42, 174, 44, 1, 1, 45, 186, 47, 1, 1, 48, 198, 50, 1, 1, 51, 210, 53, 1, 1, 54, 222, 56, 1, 1, 57, 234, 59, 1, 1, 60, 246, 62, 1, 1, 63, 258, 65, 1, 1, 66, 270, 68, 1, 1, 69, 282, 71, 1, 1, 72, 294, 74, 1, 1, 75, 306, 77, 1, 1, 78, 318, 80, 1, 1, 81, 330, 83, 1, 1, 84, 342, 86, 1, 1, 87, 354, 89, 1, 1, 90, 366, 92, 1, 1, 93, 378, 95, 1, 1, 96, 390, 98, 1, 1, 99, 402, 101, 1, 1, 102, 414, 104, 1, 1, 105, 426, 107, 1, 1, 108, 438, 110, 1, 1, 111, 450, 113, 1, 1, 114, 462, 116, 1, 1, 117, 474, 119, 1, 1, 120, 486, 122, 1, 1, 123, 498, 125, 1, 1, 126, 510, 128, 1, 1, 129, 522, 131, 1, 1, 132, 534, 134, 1, 1, 135, 546, 137, 1, 1, 138, 558, 140, 1, 1, 141, 570, 143, 1, 1, 144, 582, 146, 1, 1, 147, 594, 149, 1, 1, 150, 606, 152, 1, 1, 153, 618, 155, 1, 1, 156, 630, 158, 1, 1, 159, 642, 161, 1, 1, 162, 654, 164, 1, 1, 165, 666, 167, 1, 1, 168, 678, 170, 1, 1, 171, 690, 173, 1, 1, 174, 702, 176, 1, 1, 177, 714, 179, 1, 1, 180, 726, 182, 1, 1, 183, 738, 185, 1, 1, 186, 750, 188, 1, 1, 189, 762, 191, 1, 1, 192, 774, 194, 1, 1, 195, 786, 197, 1, 1, 198, 798, 200, 1, 1, 201, 810, 203, 1, 1, 204, 822, 206, 1, 1, 207, 834, 209, 1, 1, 210, 846, 212, 1, 1, 213, 858, 215, 1, 1, 216, 870, 218, 1, 1, 219, 882, 221, 1, 1, 222, 894, 224, 1, 1, 225, 906, 227, 1, 1, 228, 918, 230, 1, 1, 231, 930, 233, 1, 1, 234, 942, 236, 1, 1, 237, 954, 239, 1, 1, 240, 966, 242, 1, 1, 243, 978, 245, 1, 1, 246, 990, 248, 1, 1, 249, 1002, 251, 1, 1, 252, 1014, 254, 1, 1, 255, 1026, 257, 1, 1, 258, 1038, 260, 1, 1, 261, 1050, 263, 1, 1, 264, 1062, 266, 1, 1, 267, 1074, 269, 1, 1, 270, 1086, 272, 1, 1, 273, 1098, 275, 1, 1, 276, 1110, 278, 1, 1, 279, 1122, 281, 1, 1, 282, 1134, 284, 1, 1, 285, 1146, 287, 1, 1, 288, 1158, 290, 1, 1, 291, 1170, 293, 1, 1, 294, 1182, 296, 1, 1, 297, 1194, 299, 1, 1, 300, 1206, 302, 1, 1, 303, 1218, 305, 1, 1, 306, 1230, 308, 1, 1, 309, 1242, 311, 1, 1, 312, 1254, 314, 1, 1, 315, 1266, 317, 1, 1, 318, 1278, 320, 1, 1, 321, 1290, 323, 1, 1, 324, 1302, 326, 1, 1, 327, 1314, 329, 1, 1, 330, 1326, 332, 1, 1, 333, 1338, 335, 1, 1, 336, 1350, 338, 1, 1, 339, 1362, 341, 1, 1, 342, 1374, 344, 1, 1, 345, 1386, 347, 1, 1, 348, 1398, 350, 1, 1, 351, 1410, 353, 1, 1, 354, 1422, 356, 1, 1, 357, 1434, 359, 1, 1, 360, 1446, 362, 1, 1, 363, 1458, 365, 1, 1, 366, 1470, 368, 1, 1, 369, 1482, 371, 1, 1, 372, 1494, 374, 1, 1, 375, 1506, 377, 1, 1, 378, 1518, 380, 1, 1, 381, 1530, 383, 1, 1, 384, 1542, 386, 1, 1, 387, 1554, 389, 1, 1, 390, 1566, 392, 1, 1, 393, 1578, 395, 1, 1, 396, 1590, 398, 1, 1, 399, 1602, 401, 1, 1, 402, 1614, 404, 1, 1, 405, 1626, 407, 1, 1, 408, 1638, 410, 1, 1, 411, 1650, 413, 1, 1, 414, 1662, 416, 1, 1, 417, 1674, 419, 1, 1, 420, 1686, 422, 1, 1, 423, 1698, 425, 1, 1, 426, 1710, 428, 1, 1, 429, 1722, 431, 1, 1, 432, 1734, 434, 1, 1, 435, 1746, 437, 1, 1, 438, 1758, 440, 1, 1, 441, 1770, 443, 1, 1, 444, 1782, 446, 1, 1, 447, 1794, 449, 1, 1, 450, 1806, 452, 1, 1, 453, 1818, 455, 1, 1, 456, 1830, 458, 1, 1, 459, 1842, 461, 1, 1, 462, 1854, 464, 1, 1, 465, 1866, 467, 1, 1, 468, 1878, 470, 1, 1, 471, 1890, 473, 1, 1, 474, 1902, 476, 1, 1, 477, 1914, 479, 1, 1, 480, 1926, 482, 1, 1, 483, 1938, 485, 1, 1, 486, 1950, 488, 1, 1, 489, 1962, 491, 1, 1, 492, 1974, 494, 1, 1, 495, 1986, 497, 1, 1, 498, 1998, 500, 1, 1, 501, 2010, 503, 1, 1, 504, 2022, 506, 1, 1, 507, 2034, 509, 1, 1, 510, 2046, 512, 1, 1, 513, 2058, 515, 1, 1, 516, 2070, 518, 1, 1, 519, 2082, 521, 1, 1, 522, 2094, 524, 1, 1, 525, 2106, 527, 1, 1, 528, 2118, 530, 1, 1, 531, 2130, 533, 1, 1, 534, 2142, 536, 1, 1, 537, 2154, 539, 1, 1, 540, 2166, 542, 1, 1, 543, 2178, 545, 1, 1, 546, 2190, 548, 1, 1, 549, 2202, 551, 1, 1, 552, 2214, 554, 1, 1, 555, 2226, 557, 1, 1, 558, 2238, 560, 1, 1, 561, 2250, 563, 1, 1, 564, 2262, 566, 1, 1, 567, 2274, 569, 1, 1, 570, 2286, 572, 1, 1, 573, 2298, 575, 1, 1, 576, 2310, 578, 1, 1, 579, 2322, 581, 1, 1, 582, 2334, 584, 1, 1, 585, 2346, 587, 1, 1, 588, 2358, 590, 1, 1, 591, 2370, 593, 1, 1, 594, 2382, 596, 1, 1, 597, 2394, 599, 1, 1, 600, 2406};

        for (int i = 0; i < 10; i++) {
            power = ContinuedFractionConstants.E.power(2);
            powerMon = MonitorFactory.start("E power");
            ContinuedFractionTestUtils.assertStartsWith(expected, true, power.partialQuotients());
            powerMon.stop();

            power = ContinuedFractionConstants.E.multiply(ContinuedFractionConstants.E);
            multiplyMon = MonitorFactory.start("E power by multiplication");
            ContinuedFractionTestUtils.assertStartsWith(expected, true, power.partialQuotients());
            multiplyMon.stop();
        }

        log.info("e power " + powerMon.toString());
        log.info("e power by multiplication " + multiplyMon.toString());

        expected = new long[1000];
        expected[0] = 2;
        Arrays.fill(expected, 1, expected.length, 1);

        for (int i = 0; i < 10; i++) {
            power = ContinuedFractionConstants.GoldenRatio.power(2);
            powerMon = MonitorFactory.start("GoldenRatio power");
            ContinuedFractionTestUtils.assertStartsWith(expected, true, power.partialQuotients());
            powerMon.stop();

            power = ContinuedFractionConstants.GoldenRatio.multiply(
                    ContinuedFractionConstants.GoldenRatio);
            multiplyMon = MonitorFactory.start("GoldenRatio power by multiplication");
            ContinuedFractionTestUtils.assertStartsWith(expected, true, power.partialQuotients());
            multiplyMon.stop();
        }

        log.info("GoldenRatio power " + powerMon.toString());
        log.info("GoldenRatio power by multiplication " + multiplyMon.toString());
    }

    protected void tearDown() throws Exception {
        r = null;
        re = null;
        pqi = null;
        convergent = null;
        power = null;
    }

}
