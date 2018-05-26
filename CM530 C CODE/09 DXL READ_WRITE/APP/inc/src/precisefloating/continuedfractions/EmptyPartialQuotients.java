package precisefloating.continuedfractions;

import java.math.BigInteger;

/**
 * Empty (and invalid) partial quotients iterator.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class EmptyPartialQuotients extends PartialQuotients {

    private final static EmptyPartialQuotients instance = new EmptyPartialQuotients();

    public static EmptyPartialQuotients getInstance() {
        return instance;
    }

    protected BigInteger computeNext() {
        throw new IllegalStateException();
    }

    public boolean hasNext() {
        return false;
    }

}
