package precisefloating.continuedfractions;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class SingletonPartialQuotient extends PartialQuotients {

    private final BigInteger x;

    public SingletonPartialQuotient(BigInteger x) {
        this.x = x;
    }

    protected BigInteger computeNext() {
        return x;
    }

    public boolean hasNext() {
        return !isStarted();
    }
}
