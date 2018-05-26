package precisefloating.continuedfractions;

import precisefloating.Rational;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Quote from <a href="http://mathworld.wolfram.com/EuclideanAlgorithm.html">
 * http://mathworld.wolfram.com/EuclideanAlgorithm.html</a>:
 * <p/>
 * For the Euclidean algorithm, Lame showed that the number of steps needed to arrive at
 * the greatest common divisor for two numbers less than <i>n</i> is
 * <i>steps <= (ln(n) + ln(sqrt(5))) / ln(golden ratio)</i>, and it is always <= 5 times
 * the number of digits in the smaller number. As shown by Lamé's theorem, the worst case occurs
 * when the algorithm is applied to two consecutive Fibonacci numbers.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class RationalPartialQuotients extends PartialQuotients
        implements Cloneable, Serializable {

    private Rational q;

    public RationalPartialQuotients(Rational r) {
        this.q = r.reduced();
    }

    /**
     * Very cheap operation.
     */
    public boolean hasNext() {
        return q != null;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new InternalError("impossible as we do implement Cloneable");
        }
    }

    /**
     * @link http://mathworld.wolfram.com/EuclideanAlgorithm.html
     */
    public BigInteger computeNext() {
        if (q.getDenominator().equals(BigInteger.ONE)) {
            BigInteger last = q.getNumerator();
            // assert uniqueness
            assert !isStarted() || last.compareTo(BigInteger.ONE) > 0;

            q = null;
            return last;
        } else {
            BigInteger x = q.floor();
            q = q.inverseFractionalValue();
            return x;
        }
    }

}
