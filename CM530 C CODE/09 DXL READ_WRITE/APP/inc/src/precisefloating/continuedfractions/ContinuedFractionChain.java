package precisefloating.continuedfractions;

import org.apache.commons.collections.iterators.IteratorChain;

import java.util.List;

/**
 * Description adapted from <code>org.apache.commons.collections.iterators.IteratorChain</code>.
 * <p/>
 * A ContinuedFractionChain is an ContinuedFraction that wraps one or more
 * SimpleContinuedFractions.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 * @see IteratorChain
 */
public class ContinuedFractionChain extends ContinuedFraction {

    ContinuedFraction[] continuedFractions;

    public ContinuedFractionChain(ContinuedFraction x,
            ContinuedFraction y) {
        this(new ContinuedFraction[]{x, y});
    }

    public ContinuedFractionChain(ContinuedFraction[] array) {
        if (array.length == 0) {
            throw new IllegalArgumentException("at least one element is required");
        }

        continuedFractions = array;
    }

    public ContinuedFractionChain(List list) {
        this((ContinuedFraction[]) list.toArray(new ContinuedFraction[list.size()]));
    }

    public PartialQuotients partialQuotients() {
        PartialQuotients[] iterators =
                new PartialQuotients[continuedFractions.length];

        for (int i = 0; i < continuedFractions.length; i++) {
            ContinuedFraction simpleContinuedFraction = continuedFractions[i];
            iterators[i] = simpleContinuedFraction.partialQuotients();
        }

        return new PartialQuotientsChain(iterators);
    }

}
