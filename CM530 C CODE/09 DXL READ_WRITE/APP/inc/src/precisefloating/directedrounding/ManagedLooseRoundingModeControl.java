package precisefloating.directedrounding;

import precisefloating.Formulas;

/**
 * For every operation, we use the Java operators or the value computed by the Math class
 * if the operation is a function, and return the next/previous double number.
 * This works because the methods used from Math have the result must within 0.5 or 1 ulp of
 * the correctly rounded result. Our results are also semi-monotonic.
 * Quote from the Math class javadoc: <i>Informally, with a 1 ulp error bound, when the exact result is
 * a representable number the exact result should be returned; otherwise, either of the two
 * floating-point numbers closest to the exact result may be returned.</i>
 * Even if the Math class returns the exact result in certain cases, we usually don't. This implementation
 * is optimized for performance, rather than accuracy. The RoundingModeControl interface represens
 * rounding modes with the greatest possible accuracy.
 * This class is thread safe.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class ManagedLooseRoundingModeControl implements LooseRoundingModeControl {

    public double addRoundFloor(double x, double y) {
        double z = x + y;
        return Formulas.previousIfExists(z);
    }

    public double addRoundCeiling(double x, double y) {
        double z = x + y;
        return Formulas.nextIfExists(z);
    }

    public double[] add(double x, double y) {
        double[] a = new double[2];
        add(x, y, a, 0);
        return a;
    }

    public void add(double x, double y, double[] a, int start) {
        double z = x + y;
        wideInterval(z, a, start);
    }

    public double multiplyRoundFloor(double x, double y) {
        double z = x * y;
        return Formulas.previousIfExists(z);
    }

    public double multiplyRoundCeiling(double x, double y) {
        double z = x * y;
        return Formulas.nextIfExists(z);
    }

    public double[] multiply(double x, double y) {
        double[] a = new double[2];
        multiply(x, y, a, 0);
        return a;
    }

    public void multiply(double x, double y, double[] a, int start) {
        double z = x * y;
        wideInterval(z, a, start);
    }

    public double subtractRoundFloor(double x, double y) {
        double z = x - y;
        return Formulas.previousIfExists(z);
    }

    public double subtractRoundCeiling(double x, double y) {
        double z = x - y;
        return Formulas.nextIfExists(z);
    }

    public double[] subtract(double x, double y) {
        double[] a = new double[2];
        subtract(x, y, a, 0);
        return a;
    }

    public void subtract(double x, double y, double[] a, int start) {
        double z = x - y;
        wideInterval(z, a, start);
    }

    public double divideRoundFloor(double x, double y) {
        double z = x / y;
        return Formulas.previousIfExists(z);
    }

    public double divideRoundCeiling(double x, double y) {
        double z = x / y;
        return Formulas.nextIfExists(z);
    }

    public double[] divide(double x, double y) {
        double[] a = new double[2];
        divide(x, y, a, 0);
        return a;
    }

    public void divide(double x, double y, double[] a, int start) {
        double z = x / y;
        wideInterval(z, a, start);
    }

    public double squareRoundFloor(double x) {
        double z = x * x;

        if (z == 0) {
            return 0;
        } else {
            return Formulas.previousIfExists(z);
        }
    }

    public double squareRoundCeiling(double x) {
        double z = x * x;

        if (x == 0) {
            // return positive zero
            return 0;
        } else {
            return Formulas.nextIfExists(z);
        }
    }

    public double[] square(double x) {
        double[] a = new double[2];
        square(x, a, 0);
        return a;
    }

    public void square(double x, double[] a, int start) {
        double z = x * x;

        if (x == 0) {
            // return positive zero
            a[start] = a[start + 1] = 0;
        } else {
            wideInterval(z, a, start);
        }
    }

    public double sqrtRoundFloor(double x) {
        if (x == 0) {
            // If the argument is positive zero or negative zero, then
            // the result is the same as the argument.
            return x;
        } else {
            double z = Math.sqrt(x);
            assert z > 0;
            return Formulas.previousIfExists(z);
        }
    }

    public double sqrtRoundCeiling(double x) {
        if (x == 0) {
            // If the argument is positive zero or negative zero, then
            // the result is the same as the argument.
            return x;
        } else {
            double z = Math.sqrt(x);
            assert z > 0;
            return Formulas.nextIfExists(z);
        }
    }

    public double[] sqrt(double x) {
        double[] a = new double[2];
        sqrt(x, a, 0);
        return a;
    }

    public void sqrt(double x, double[] a, int start) {
        if (x == 0) {
            // If the argument is positive zero or negative zero, then
            // the result is the same as the argument.
            a[start] = a[start + 1] = x;
        } else {
            double z = Math.sqrt(x);
            assert z > 0;
            wideInterval(z, a, start);
        }
    }

    private void wideInterval(double z, double[] a, int start) {
        a[start] = Formulas.previousIfExists(z);
        a[start + 1] = Formulas.nextIfExists(z);
    }

    public double expRoundFloor(double x) {
        double z = Math.exp(x);

        if (z == 0) {
            return z;
        } else {
            if (x == 0) {
                return 1;
            } else {
                assert z > 0;
                return Formulas.previousIfExists(z);
            }
        }
    }

    public double expRoundCeiling(double x) {
        double z = Math.exp(x);

        if (x == 0) {
            return 1;
        } else {
            return Formulas.nextIfExists(z);
        }
    }

    public double[] exp(double x) {
        double[] a = new double[2];
        exp(x, a, 0);
        return a;
    }

    public void exp(double x, double[] a, int start) {
        double z = Math.exp(x);

        if (z == 0) {
            a[start] = 0;
            a[start + 1] = Double.MIN_VALUE;
        } else {
            assert z > 0;

            if (x == 0) {
                a[start] = a[start + 1] = 1;
            } else {
                wideInterval(z, a, start);
            }
        }
    }

}
