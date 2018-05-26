package precisefloating.continuedfractions;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class BufferedContinuedFraction extends ContinuedFraction
        implements Serializable, Cloneable {

    private final List partialQuotients;

    public BufferedContinuedFraction(BigInteger partialQuotient) {
        partialQuotients = Collections.singletonList(partialQuotient);
    }

    public BufferedContinuedFraction(List partialQuotients) {
        if (partialQuotients.isEmpty()) {
            throw new IllegalArgumentException("partialQuotients must not be empty");
        }

        this.partialQuotients = new ArrayList();
        this.partialQuotients.addAll(partialQuotients);
    }

    public PartialQuotients partialQuotients() {
        return new PartialQuotientsDecorator(partialQuotients.iterator());
    }

}
