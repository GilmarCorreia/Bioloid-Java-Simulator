package precisefloating.directedrounding;

/**
 * Represents loose directed rounding modes. All the methods with a <code>double[] a</double> and a
 * <code>int start</code> parameter throw <code>ArrayIndexOutOfBoundsException</code> if the given
 * array does not have the <code>start</code> and <code>start + 1<code> elements.
 * Otherwise, none of the operations of this class should throw exceptions
 * (other than OutOfMemoryError or internal virtual machine errors).
 * Methods ending in RoundFloor round the result towards negative infinity, while those ending
 * in RoundCeiling round the result towards positive infinity. The open intervals computed by the methods
 * with a double[] parameter can contain at most one double precision number. If an operation result is
 * exactly representable, there is no guarantee that the exact result is computed, rather than
 * an open interval that contains the result.
 * This interface can be implemented much more efficiently than RoundingModeControl.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public interface LooseRoundingModeControl {

    double addRoundFloor(double x, double y);

    double addRoundCeiling(double x, double y);

    double[] add(double x, double y);

    void add(double x, double y, double[] a, int start);

    double multiplyRoundFloor(double x, double y);

    double multiplyRoundCeiling(double x, double y);

    double[] multiply(double x, double y);

    void multiply(double x, double y, double[] a, int start);

    double subtractRoundFloor(double x, double y);

    double subtractRoundCeiling(double x, double y);

    double[] subtract(double x, double y);

    void subtract(double x, double y, double[] a, int start);

    double divideRoundFloor(double x, double y);

    double divideRoundCeiling(double x, double y);

    double[] divide(double x, double y);

    void divide(double x, double y, double[] a, int start);

    double squareRoundFloor(double x);

    double squareRoundCeiling(double x);

    double[] square(double x);

    void square(double x, double[] a, int start);

    double sqrtRoundFloor(double x);

    double sqrtRoundCeiling(double x);

    double[] sqrt(double x);

    void sqrt(double x, double[] a, int start);

    double expRoundFloor(double x);

    double expRoundCeiling(double x);

    double[] exp(double x);

    void exp(double x, double[] a, int start);

}
