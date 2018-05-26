package precisefloating.continuedfractions;

import precisefloating.DivideByZeroException;
import precisefloating.Rational;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class InversePartialQuotients extends PartialQuotients {

    private PartialQuotients xIterator;

    /**
     * @throws DivideByZeroException when this iterator reprezents zero
     */
    protected BigInteger computeNext() {
        if (!isStarted()) {
            assert xIterator.hasNext();
            BigInteger a = xIterator.nextPartialQuotient();

            if (a.signum() == 0) {
                if (!xIterator.hasNext()) {
                    // 1/0 throws DivideByZeroException
                    throw new DivideByZeroException("cannot divide one over zero");
                } else {
                    return xIterator.nextPartialQuotient();
                }
            } else {
                if (a.signum() == +1) {
                    if (a.equals(BigInteger.ONE)) {
                        if (xIterator.hasNext()) {
                            xIterator = new PartialQuotientsChain(
                                    Rational.valueOf(a).expansion().partialQuotients(),
                                    xIterator);
                            return BigInteger.ZERO;
                        } else {
                            return BigInteger.ONE;
                        }
                    } else {
                        assert a.compareTo(BigInteger.valueOf(2)) >= 0;
                        xIterator = new PartialQuotientsChain(
                                Rational.valueOf(a).expansion().partialQuotients(),
                                xIterator);
                        return BigInteger.ZERO;
                    }
                } else {
                    assert a.signum() == -1;

                    if (!xIterator.hasNext()) {
                        // treat simple cases here
                        if (a.equals(BigInteger.valueOf(-1))) {
                            return a;
                        } else {
                            if (a.equals(BigInteger.valueOf(-2))) {
                                // 1/-2 = [-1, 2];
                                xIterator = Rational.valueOf(2).expansion().partialQuotients();
                            } else {
                                assert a.compareTo(BigInteger.valueOf(-3)) <= 0;
                                // a <= -3 implies -a - 1 >=2
                                // 1/a = [-1, 1, -a -1]
                                xIterator = Rational.create(a,
                                        a.add(BigInteger.ONE)).expansion().partialQuotients();
                            }

                            return BigInteger.valueOf(-1);
                        }
                    } else {
                        // construct 6 partial quotients iterators
                        PartialQuotients xReloadedIterator =
                                new PartialQuotientsChain(
                                        Rational.valueOf(a).expansion().partialQuotients(),
                                        xIterator);
                        NegatePartialQuotients negateReloadedIterator =
                                new NegatePartialQuotients(xReloadedIterator);
                        InversePartialQuotients inverseNegateReloaded =
                                new InversePartialQuotients(
                                        negateReloadedIterator);
                        xIterator = new NegatePartialQuotients(inverseNegateReloaded);

                        return xIterator.nextPartialQuotient();
                    }
                }
            }
        } else {
            return xIterator.nextPartialQuotient();
        }
    }

    public boolean hasNext() {
        return xIterator.hasNext();
    }

    /**
     * @param xIterator May already be started or not, but it must have a next element,
     *                  in order for the algorithm to succeeed. If the given iterator has alredy been started,
     *                  the results produced will not probably be of any use.
     */
    public InversePartialQuotients(PartialQuotients xIterator) {
        if (!xIterator.hasNext()) {
            throw new IllegalStateException("xIterator must still have at least one element");
        }

        this.xIterator = xIterator;
    }

}
