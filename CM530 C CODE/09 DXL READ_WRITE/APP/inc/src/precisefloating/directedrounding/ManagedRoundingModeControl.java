package precisefloating.directedrounding;

import precisefloating.DoublePrecisionNo;
import precisefloating.Formulas;
import precisefloating.Rational;
import precisefloating.continuedfractions.ContinuedFraction;

import java.math.BigInteger;
import java.util.logging.Logger;

/**
 * Pure managed RoundingModeControl implementation. This class is not thread safe.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class ManagedRoundingModeControl implements RoundingModeControl {

    private static final Logger log = Logger.getLogger(ManagedRoundingModeControl.class.getName());

    private final double[] temp = new double[2];

    public double addRoundFloor(double x, double y) {
        add(x, y, temp, 0);
        return temp[0];
    }

    public double addRoundCeiling(double x, double y) {
        add(x, y, temp, 0);
        return temp[1];
    }

    public double[] add(double x, double y) {
        double[] a = new double[2];
        add(x, y, a, 0);
        return a;
    }

    /**
     * IEEE 754 quote:
     * When the sum of two operands with opposite signs (or the difference of two operands with
     * like signs) is exactly zero, the sign of that sum (or difference) shall be “+” in
     * all rounding modes except round toward -infinity, in which mode that sign shall be “-”.
     * However, x + x = x - (-x) retains the same sign as x even when x is zero.
     */
    public void add(double x, double y, double[] a, int start) {
        double nearest = x + y;

        if (Double.isNaN(nearest)) {
            a[start] = a[start + 1] = nearest;
            return;
        }

        if (Double.isInfinite(nearest)) {
            infiniteResult(nearest, a, start);
            return;
        }

        // x + y = 0 in any/all rounding modes iff x + y = 0 exact due to gradual underflow support.

        if (nearest == 0) {
            if (x == 0 && Double.doubleToLongBits(x) == Double.doubleToLongBits(y)) {
                assert Double.doubleToLongBits(nearest) == Double.doubleToLongBits(x);
                // x + x = x - (-x) retains the same sign as x even when x is zero
                exactValue(nearest, a, start);
            } else {
                assert Double.doubleToLongBits(nearest) == Formulas.DOUBLE_POSITIVE_ZERO_BITS;
                // Note: -0.0 is required rather than -0, because the integer value -0 is actually 0,
                // which would be converted to +0.0
                a[start] = -0.0;
                a[start + 1] = +0.0;
            }

            return;
        }

        if (y == 0) {
            a[start] = a[start + 1] = x;
            return;
        }

        if (x == 0) {
            a[start] = a[start + 1] = y;
            return;
        }

        DoublePrecisionNo xDpn = new DoublePrecisionNo(x);
        DoublePrecisionNo yDpn = new DoublePrecisionNo(y);

        if (xDpn.getExponent() > yDpn.getExponent()) {
            DoublePrecisionNo aux = xDpn;
            xDpn = yDpn;
            yDpn = aux;
        }

        assert xDpn.getExponent() <= yDpn.getExponent() && yDpn.getExponent() <= xDpn.getExponent() + 1022 + 1075;

        long absMx = Math.abs(xDpn.getMantissa()), absMy = Math.abs(yDpn.getMantissa());

        int c1 = Formulas.highestSetBit(absMx);
        int c2 = Formulas.highestSetBit(absMy) + yDpn.getExponent() - xDpn.getExponent();

        boolean sameSignum = Formulas.sameSignum(xDpn.getMantissa(), yDpn.getMantissa());

        DoublePrecisionNo nearestSum = new DoublePrecisionNo(nearest);
        int order;

        if (Math.max(c1, c2) <= 61 + (sameSignum ? 0 : 1)) {
            log.finest("long is enough");

            long mantissa = xDpn.getMantissa() + (yDpn.getMantissa() << (yDpn.getExponent() - xDpn.getExponent()));
            DoublePrecisionNo exact = new DoublePrecisionNo(mantissa, xDpn.getExponent(), false);

            order = exact.compareTo(nearestSum);

            assert order != 0 || nearest == exact.exactFiniteDoubleValue();
        } else {
            log.finest("BigInteger needed");

            BigInteger bigXm = BigInteger.valueOf(xDpn.getMantissa());
            BigInteger bigYm = BigInteger.valueOf(yDpn.getMantissa()).shiftLeft(yDpn.getExponent() - xDpn.getExponent());
            BigInteger exactM = bigXm.add(bigYm);
            BigInteger nearestM = BigInteger.valueOf(nearestSum.getMantissa());

            order = computeOrder(exactM, nearestM, xDpn.getExponent(), nearestSum.getExponent());
        }

        result(order, nearest, a, start);
    }

    public double multiplyRoundFloor(double x, double y) {
        multiply(x, y, temp, 0);
        return temp[0];
    }

    public double multiplyRoundCeiling(double x, double y) {
        multiply(x, y, temp, 0);
        return temp[1];
    }

    private static int computeOrder(BigInteger exactM, BigInteger nearestM,
            int exactE, int nearestE) {
        if (exactE < nearestE) {
            nearestM = nearestM.shiftLeft(nearestE - exactE);
        } else {
            if (exactE > nearestE) {
                exactM = exactM.shiftLeft(exactE - nearestE);
            }
        }

        int order = exactM.compareTo(nearestM);
        return order;
    }

    private static void result(int order, double nearest, double[] a, int start) {
        if (order < 0) {
            // exact < nearest
            tooBig(nearest, a, start);
        } else {
            if (order > 0) {
                // exact > nearest
                tooSmall(nearest, a, start);
            } else {
                assert order == 0;
                exactValue(nearest, a, start);
            }
        }

        assert Double.NEGATIVE_INFINITY <= a[start] && a[start] <= a[start + 1]
                && a[start + 1] <= Double.POSITIVE_INFINITY;
    }

    private static void infiniteResult(double nearest, double[] a, int start) {
        // I should check the spec
        if (nearest == Double.POSITIVE_INFINITY) {
            a[start] = Double.MAX_VALUE;
            a[start + 1] = Double.POSITIVE_INFINITY;
        } else {
            assert nearest == Double.NEGATIVE_INFINITY;

            a[start] = Double.NEGATIVE_INFINITY;
            a[start + 1] = -Double.MAX_VALUE;
        }
    }

    public double[] multiply(double x, double y) {
        double[] a = new double[2];
        multiply(x, y, a, 0);
        return a;
    }

    public void multiply(double x, double y, double[] a, int start) {
        double nearest = x * y;

        if (Double.isNaN(nearest)) {
            exactValue(nearest, a, start);
            return;
        }

        if (Double.isInfinite(nearest)) {
            infiniteResult(nearest, a, start);
            return;
        }

        assert !Double.isNaN(nearest) && !Double.isInfinite(nearest);
        assert !Double.isNaN(x) && !Double.isInfinite(x);
        assert !Double.isNaN(y) && !Double.isInfinite(y);

        if (x == 0 || y == 0) {
            assert nearest == 0;
            // keep the sign
            exactValue(nearest, a, start);
            return;
        }

        if (nearest == 0) {
            // result is not exact

            long nearestBits = Double.doubleToLongBits(nearest);

            if (nearestBits == Formulas.DOUBLE_POSITIVE_ZERO_BITS) {
                a[start] = +0.0;
                a[start + 1] = Double.MIN_VALUE;
            } else {
                assert nearestBits == Formulas.DOUBLE_NEGATIVE_ZERO_BITS;
                a[start] = -Double.MIN_VALUE;
                a[start + 1] = -0.0;
            }

            return;
        }

        DoublePrecisionNo xDpn = new DoublePrecisionNo(x);
        DoublePrecisionNo yDpn = new DoublePrecisionNo(y);

        long absMx = Math.abs(xDpn.getMantissa()), absMy = Math.abs(yDpn.getMantissa());

        int c1 = Formulas.highestSetBit(absMx);
        int c2 = Formulas.highestSetBit(absMy);

        DoublePrecisionNo nearestProduct = new DoublePrecisionNo(nearest);
        int order;

        int exactE = xDpn.getExponent() + yDpn.getExponent() - Formulas.N_DOUBLE + 1;
        assert -2044 - 52 <= exactE && exactE <= 2150 - 52;

        if (c1 + c2 <= 61) {
            log.finest("long is enough");

            long exactM = xDpn.getMantissa() * yDpn.getMantissa();
            DoublePrecisionNo exact = new DoublePrecisionNo(exactM, exactE, false);

            order = exact.compareTo(nearestProduct);
            assert order != 0 || nearest == exact.exactFiniteDoubleValue();
        } else {
            log.finest("BigInteger needed");

            BigInteger bigXm = BigInteger.valueOf(xDpn.getMantissa());
            BigInteger bigYm = BigInteger.valueOf(yDpn.getMantissa());

            BigInteger exactM = bigXm.multiply(bigYm);
            BigInteger nearestM = BigInteger.valueOf(nearestProduct.getMantissa());

            order = computeOrder(exactM, nearestM, exactE, nearestProduct.getExponent());
        }

        result(order, nearest, a, start);
    }

    public double subtractRoundFloor(double x, double y) {
        subtract(x, y, temp, 0);
        return temp[0];
    }

    public double subtractRoundCeiling(double x, double y) {
        subtract(x, y, temp, 0);
        return temp[1];
    }

    public double[] subtract(double x, double y) {
        return add(x, -y);
    }

    public void subtract(double x, double y, double[] a, int start) {
        add(x, -y, a, start);
    }

    public double divideRoundFloor(double x, double y) {
        divide(x, y, temp, 0);
        return temp[0];
    }

    public double divideRoundCeiling(double x, double y) {
        divide(x, y, temp, 0);
        return temp[1];
    }

    public double[] divide(double x, double y) {
        double[] a = new double[2];
        divide(x, y, a, 0);
        return a;
    }

    public void divide(double x, double y, double[] a, int start) {
        double nearest = x / y;

        if (Double.isNaN(nearest)) {
            exactValue(nearest, a, start);
            return;
        }

        if (Double.isInfinite(nearest)) {
            infiniteResult(nearest, a, start);
            return;
        }

        assert y != 0;
        assert Formulas.isFinite(nearest);

        if (Double.isInfinite(y)) {
            assert nearest == 0 && Formulas.isFinite(x);
            // keep the sign
            exactValue(nearest, a, start);
            return;
        }

        assert Formulas.isFinite(y);

        // use a[start] and a[start + 1] as temporary variables
        multiply(nearest, y, a, start);

        double x0 = a[start], x1 = a[start + 1];

        if (x < x0 || (x == x0 && x0 < x1)) {
            // x is less than the exact product nearest * y
            if (y > 0) {
                // nearest is too big
                tooBig(nearest, a, start);
            } else {
                assert y < 0;
                // nearest is too small
                tooSmall(nearest, a, start);
            }

            return;
        }

        if (x > x1 || (x == x1 && x0 < x1)) {
            // x is greater than the exact product nearest * y
            if (y > 0) {
                // nearest is too small
                tooSmall(nearest, a, start);
            } else {
                assert y < 0;
                // nearest is too big
                tooBig(nearest, a, start);
            }

            return;
        }

        // nearest is exact
        assert x0 == x && x == x1;
        exactValue(nearest, a, start);
    }

    public double squareRoundFloor(double x) {
        square(x, temp, 0);
        return temp[0];
    }

    public double squareRoundCeiling(double x) {
        square(x, temp, 0);
        return temp[1];
    }

    private final static void tooSmall(double nearest, double[] a, int start) {
        a[start] = nearest;
        a[start + 1] = Formulas.next(nearest);
    }

    private final static void tooBig(double nearest, double[] a, int start) {
        a[start] = Formulas.previous(nearest);
        a[start + 1] = nearest;
    }

    private final static void exactValue(double x, double[] a, int start) {
        assert !Double.isInfinite(x);
        a[start] = a[start + 1] = x;
    }

    public double[] square(double x) {
        double[] a = new double[2];
        square(x, a, 0);
        return a;
    }

    public void square(double x, double[] a, int start) {
        // todo a special routine should be provided
        multiply(x, x, a, start);
    }

    public double sqrtRoundFloor(double x) {
        sqrt(x, temp, 0);
        return temp[0];
    }

    public double sqrtRoundCeiling(double x) {
        sqrt(x, temp, 0);
        return temp[1];
    }

    public double[] sqrt(double x) {
        double[] a = new double[2];
        sqrt(x, a, 0);
        return a;
    }

    /**
     * Except that sqrt(-0) shall be -0, every valid square root shall have a positive sign.
     */
    public void sqrt(double x, double[] a, int start) {
        if (x == 0) {
            // keep the sign of positive or negative zero
            exactValue(x, a, start);
            return;
        }

        double nearest = Math.sqrt(x);

        if (Double.isNaN(nearest)) {
            assert x < 0 || Double.isNaN(x);
            exactValue(nearest, a, start);
            return;
        }

        if (nearest == Double.POSITIVE_INFINITY) {
            assert x == Double.POSITIVE_INFINITY;
            exactValue(Double.POSITIVE_INFINITY, a, start);
            return;
        }

        assert Formulas.isFinite(x) && Formulas.isFinite(nearest);
        assert x > 0 && nearest > 0;

        // use a[start] and a[start + 1] as temporary variables
        square(nearest, a, start);

        double x0 = a[start], x1 = a[start + 1];

        if (x < x0 || (x == x0 && x0 < x1)) {
            // x is less than the exact square of nearest
            // nearest is too big
            tooBig(nearest, a, start);
            return;
        }

        if (x > x1 || (x == x1 && x0 < x1)) {
            // x is greater than the exact square of nearest
            // nearest is too small
            tooSmall(nearest, a, start);
            return;
        }

        // nearest is exact
        assert x0 == x && x == x1;
        exactValue(nearest, a, start);
    }

    public double expRoundFloor(double x) {
        exp(x, temp, 0);
        return temp[0];
    }

    public double expRoundCeiling(double x) {
        exp(x, temp, 0);
        return temp[1];
    }

    public double[] exp(double x) {
        double[] a = new double[2];
        exp(x, a, 0);
        return a;
    }

    public static final double EXP_SMALL_ZERO = -744.4400719213813,
    EXP_BIG_ONE = -Formulas.pow2(-53), EXP_SMALL_ONE = Formulas.pow2(-52),
    EXP_POSITIVE_INFINITY = 709.7827128933841;

    static {
        assert Formulas.inOpenInterval(EXP_SMALL_ZERO, -Formulas.pow2(10), -Formulas.pow2(9));
        assert Formulas.inOpenInterval(EXP_POSITIVE_INFINITY, Formulas.pow2(9), Formulas.pow2(10));
    }

    public void exp(double x, double[] a, int start) {
        double mathexp = Math.exp(x);

        assert mathexp >= 0;

        if (x == 0) {
            assert mathexp == 1;
            exactValue(1, a, start);
            return;
        }

        if (x > 0) {
            if (/*mathexp == 1 ||*/ x < EXP_SMALL_ONE) {
                tooSmall(1, a, start);
                return;
            } else {
                assert mathexp > 1 && x >= EXP_SMALL_ONE;

                if (/*mathexp == Double.POSITIVE_INFINITY ||*/ x >= EXP_POSITIVE_INFINITY) {
                    infiniteResult(Double.POSITIVE_INFINITY, a, start);
                    return;
                } else {
                    assert mathexp < Double.POSITIVE_INFINITY;
                }
            }
        }

        if (x < 0) {
            if (/*mathexp == 1 || */ x >= EXP_BIG_ONE) {
                tooBig(1, a, start);
                return;
            } else {
                assert 0 < mathexp && mathexp < 1;

                if (mathexp == 0 || x <= EXP_SMALL_ZERO) {
                    tooSmall(0, a, start);
                    return;
                }
            }
        }

        assert Formulas.isFinite(mathexp) && mathexp > 0;
        assert (EXP_SMALL_ZERO < x && x < EXP_BIG_ONE)
                || (EXP_SMALL_ONE <= x && x < EXP_POSITIVE_INFINITY);

        ContinuedFraction exact = ContinuedFraction.exponential(x);
        Rational mathexpRational = Rational.create(mathexp);

        int cmp = mathexpRational.expansion().compareTo(exact);

        // Lamber established in 1761 for the first time that exp(r) is irrational,
        // for any nonzero rational number r.
        assert cmp != 0;

        if (cmp < 0) {
            tooSmall(mathexp, a, start);
        } else {
            assert cmp > 0;
            tooBig(mathexp, a, start);
        }

        assert a[0] > 0 && a[1] < Double.POSITIVE_INFINITY && a[0] != 1 && a[1] != 1;
    }

}
