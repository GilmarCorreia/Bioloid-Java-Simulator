package precisefloating.continuedfractions;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class InverseContinuedFraction extends ContinuedFraction {

    private final ContinuedFraction x;

    public InverseContinuedFraction(ContinuedFraction x) {
        this.x = x;
    }

    public ContinuedFraction inverse() {
        return x;
    }

    public PartialQuotients partialQuotients() {
        return new InversePartialQuotients(x.partialQuotients());
    }

}
