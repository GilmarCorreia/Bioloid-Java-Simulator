package precisefloating.continuedfractions;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class NegateContinuedFraction extends ContinuedFraction {

    private final ContinuedFraction x;

    public NegateContinuedFraction(ContinuedFraction x) {
        this.x = x;
    }

    public PartialQuotients partialQuotients() {
        return new NegatePartialQuotients(x.partialQuotients());
    }

    public ContinuedFraction negate() {
        return x;
    }

}
