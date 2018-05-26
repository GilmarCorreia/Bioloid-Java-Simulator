package precisefloating.continuedfractions;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class UnsupportedPartialQuotients extends PartialQuotients {

    private static final UnsupportedPartialQuotients instance = new UnsupportedPartialQuotients();

    protected BigInteger computeNext() {
        throw new UnsupportedOperationException();
    }

    public boolean hasNext() {
        throw new UnsupportedOperationException();
    }

    public static UnsupportedPartialQuotients getInstance() {
        return instance;
    }

}
