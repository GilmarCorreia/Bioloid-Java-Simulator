package precisefloating.continuedfractions;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class DivideContinuedFraction extends ContinuedFraction {

    private final ContinuedFraction x, y;

    public DivideContinuedFraction(ContinuedFraction x, ContinuedFraction y) {
        this.x = x;
        this.y = y;
    }

    public PartialQuotients partialQuotients() {
        return new DividePartialQuotients(x.partialQuotients(), y.partialQuotients());
    }

}
