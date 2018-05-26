package precisefloating.continuedfractions;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class SubtractContinuedFraction extends ContinuedFraction {

    private final ContinuedFraction x, y;

    public SubtractContinuedFraction(ContinuedFraction x, ContinuedFraction y) {
        this.x = x;
        this.y = y;
    }

    public PartialQuotients partialQuotients() {
        return new SubtractPartialQuotients(x.partialQuotients(), y.partialQuotients());
    }

}
