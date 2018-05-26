package precisefloating.directedrounding;

/**
 * Represents directed rounding modes. All the methods with a <code>double[] a</double> and a
 * <code>int start</code> parameter throw <code>ArrayIndexOutOfBoundsException</code> if the given
 * array does not have the <code>start</code> and <code>start + 1<code> elements.
 * Otherwise, none of the operations of this class should throw exceptions
 * (other than OutOfMemoryError or internal virtual machine errors).
 * Methods ending in RoundFloor round the result towards negative infinity, while those ending
 * in RoundCeiling round the result towards positice infinity.
 * The open intervals computed by the methods with a <code>double[]</<code> parameter
 * cannot contain any finite double precision number. If an operation result is
 * exactly representable, it is guaranteed that the exact result is computed (and represented
 * by an empty interval with lower bound equal to the upper bound and equal to that exact value).
 * For addition and subtraction, there a special case when the exact result is zero.
 * A RoundingModeControl object can always be used as a LooseRoundingModeControl.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public interface RoundingModeControl extends LooseRoundingModeControl {

    double addRoundFloor(double x, double y);

    double addRoundCeiling(double x, double y);

    /**
     * Computes the minimal floating point interval that contains
     * the exact value <code>x</code> + <code>y</code>. The returned array has two elements.
     * The two elements are equal iff the exact sum is representable into the double value set.
     */
    double[] add(double x, double y);

    /**
     * Computes the minimal floating point interval that contains
     * the exact value <code>x</code> + <code>y</code>. The two elements a set in the variables
     * <code>a[start]</code> and <code>a[start + 1]</code>.
     * The two elements are equal iff the exact sum is representable into the double value set.
     * Note that if an error occurs, the <code>a[start]</code> and <code>a[start + 1]</code> values
     * are undefined. This permits some optimizations in certain implementations.
     */
    void add(double x, double y, double[] a, int start);

    double multiplyRoundFloor(double x, double y);

    double multiplyRoundCeiling(double x, double y);

    /**
     * Computes the minimal floating point interval that contains
     * the exact value <code>x</code> * <code>y</code>. The returned array has two elements.
     * The two elements are equal iff the exact product is representable into the double value set.
     */
    double[] multiply(double x, double y);

    /**
     * Computes the minimal floating point interval that contains
     * the exact value <code>x</code> * <code>y</code>. The two elements a set in the variables
     * <code>a[start]</code> and <code>a[start + 1]</code>.
     * The two elements are equal iff the exact product is representable into the double value set.
     * Note that if an error occurs, the <code>a[start]</code> and <code>a[start + 1]</code> values
     * are undefined. This permits some optimizations in certain implementations.
     */
    void multiply(double x, double y, double[] a, int start);

    double subtractRoundFloor(double x, double y);

    double subtractRoundCeiling(double x, double y);

    /**
     * Computes the minimal floating point interval that contains
     * the exact value <code>x</code> - <code>y</code>. The returned array has two elements.
     * The two elements are equal iff the exact difference is representable into the double value set.
     */
    double[] subtract(double x, double y);

    /**
     * Computes the minimal floating point interval that contains
     * the exact value <code>x</code> - <code>y</code>. The two elements a set in the variables
     * <code>a[start]</code> and <code>a[start + 1]</code>.
     * The two elements are equal iff the exact difference is representable into the double value set.
     * Note that if an error occurs, the <code>a[start]</code> and <code>a[start + 1]</code> values
     * are undefined. This permits some optimizations in certain implementations.
     */
    void subtract(double x, double y, double[] a, int start);

    double divideRoundFloor(double x, double y);

    double divideRoundCeiling(double x, double y);

    /**
     * Computes the minimal floating point interval that contains
     * the exact value <code>x</code> / <code>y</code>. The returned array has two elements.
     * The two elements are equal iff the exact quotient is representable into the double value set.
     */
    double[] divide(double x, double y);

    /**
     * Computes the minimal floating point interval that contains
     * the exact value <code>x</code> / <code>y</code>. The two elements a set in the variables
     * <code>a[start]</code> and <code>a[start + 1]</code>.
     * The two elements are equal iff the exact quotient is representable into the double value set.
     * Note that if an error occurs, the <code>a[start]</code> and <code>a[start + 1]</code> values
     * are undefined. This permits some optimizations in certain implementations.
     */
    void divide(double x, double y, double[] a, int start);

    double squareRoundFloor(double x);

    double squareRoundCeiling(double x);

    /**
     * Computes the minimal floating point interval that contains
     * the exact value <code>x</code> * <code>x</code>. The returned array has two elements.
     * The two elements are equal iff the exact square is representable into the double value set.
     */
    double[] square(double x);

    /**
     * Computes the minimal floating point interval that contains
     * the exact value <code>x</code> * <code>x</code>. The two elements a set in the variables
     * <code>a[start]</code> and <code>a[start + 1]</code>.
     * The two elements are equal iff the exact square is representable into the double value set.
     * Note that if an error occurs, the <code>a[start]</code> and <code>a[start + 1]</code> values
     * are undefined. This permits some optimizations in certain implementations.
     */
    void square(double x, double[] a, int start);

    double sqrtRoundFloor(double x);

    double sqrtRoundCeiling(double x);

    /**
     * Computes the minimal floating point interval that contains
     * the exact square root of <code>x</code>. The returned array has two elements.
     * The two elements are equal iff the exact square root is representable into the double value set.
     */
    double[] sqrt(double x);

    /**
     * Computes the minimal floating point interval that contains
     * the exact square root of <code>x</code>. The two elements a set in the variables
     * <code>a[start]</code> and <code>a[start + 1]</code>.
     * The two elements are equal iff the exact square root is representable into the double value set.
     * Note that if an error occurs, the <code>a[start]</code> and <code>a[start + 1]</code> values
     * are undefined. This permits some optimizations in certain implementations.
     */
    void sqrt(double x, double[] a, int start);

    double expRoundFloor(double x);

    double expRoundCeiling(double x);

    /**
     * Computes the minimal floating point interval that contains
     * the exact square root of <code>x</code>. The returned array has two elements.
     * The two elements are equal iff the exact square root is representable into the double value set.
     */
    double[] exp(double x);

    /**
     * Computes the minimal floating point interval that contains
     * the exact square root of <code>x</code>. The two elements a set in the variables
     * <code>a[start]</code> and <code>a[start + 1]</code>.
     * The two elements are equal iff the exact square root is representable into the double value set.
     * Note that if an error occurs, the <code>a[start]</code> and <code>a[start + 1]</code> values
     * are undefined. This permits some optimizations in certain implementations.
     */
    void exp(double x, double[] a, int start);

}
