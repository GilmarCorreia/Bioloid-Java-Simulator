package precisefloating.continuedfractions;

import precisefloating.Rational;

import java.math.BigInteger;
import java.util.logging.Logger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class ComputedConvergents extends Convergents {

    protected PartialQuotients qi;
    protected Rational previousConvergent;
    protected Rational convergent;

    private static final Logger log = Logger.getLogger(ComputedConvergents.class.getName());

    public ComputedConvergents(PartialQuotients qi) {
        if (qi.isStarted()) {
            throw new IllegalStateException("the iterator has directionIndex " + qi.getIndex());
        }

        this.qi = qi;
    }

    public boolean hasNext() {
        return qi.hasNext();
    }

    public boolean isStarted() {
        boolean superIsStarted = super.isStarted();
        assert superIsStarted == qi.isStarted();
        return super.isStarted();
    }

    public long getIndex() {
        long superIndex = super.getIndex();
        assert superIndex == qi.getIndex();
        return superIndex;
    }

    public Rational computeNext() {
        BigInteger ak = qi.nextPartialQuotient();

        if (convergent == null) {
            // first time
            assert qi.getIndex() == 0;
            assert previousConvergent == null;

            convergent = Rational.valueOf(ak);
        } else {
            if (previousConvergent == null) {
                // second time
                assert qi.getIndex() == 1;

                previousConvergent = convergent;
                convergent = Rational.create(
                        ak.multiply(convergent.getNumerator()).add(BigInteger.ONE), ak);
            } else {
                Rational c = Rational.create(
                        ak.multiply(convergent.getNumerator()).add(previousConvergent.getNumerator()),
                        ak.multiply(convergent.getDenominator()).add(previousConvergent.getDenominator()));
                previousConvergent = convergent;
                convergent = c;
            }
        }

        assert convergent.isReduced();

        return convergent;
    }

}
