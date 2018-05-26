package precisefloating.continuedfractions;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public interface ContinuedFractionConstants {

    /**
     * The limiting rabbit sequence written as a binary fraction.
     * 
     * @link http://mathworld.wolfram.com/RabbitConstant.html
     */
    ContinuedFraction Rabbit = new ContinuedFraction() {

        public PartialQuotients partialQuotients() {
            return new RabitPartialQuotients();
        }

    };

    /**
     * sqrt(2)
     * 
     * @link http://mathworld.wolfram.com/PythagorassConstant.html
     */
    ContinuedFraction Pythagoras = new PeriodicContinuedFraction(
            new BigInteger[]{BigInteger.ONE}, new BigInteger[]{BigInteger.valueOf(2)});

    /**
     * The base of the natural logarithm, whose symbol "e" honors Euler.
     * 
     * @link http://mathworld.wolfram.com/e.html
     */
    ContinuedFraction E = new ContinuedFraction() {

        public PartialQuotients partialQuotients() {
            return new EPartialQuotients();
        }

    };

    /**
     * A number often encountered when taking the ratios of distances in simple geometric figures
     * such as the pentagram, decagon and dodecagon. It is known as the divine proportion,
     * golden mean, and golden section and is a Pisot-Vijayaraghavan constant. It's value is
     * (1 + sqrt(5)) / 2.
     * 
     * @link http://mathworld.wolfram.com/GoldenRatio.html
     */
    ContinuedFraction GoldenRatio = new PeriodicContinuedFraction(
            new BigInteger[]{BigInteger.ONE});

    /**
     * sqrt(2) + 1
     * 
     * @link http://mathworld.wolfram.com/SilverRatio.html
     */
    ContinuedFraction SilverRatio = new PeriodicContinuedFraction(
            new BigInteger[]{BigInteger.valueOf(2)});

}
