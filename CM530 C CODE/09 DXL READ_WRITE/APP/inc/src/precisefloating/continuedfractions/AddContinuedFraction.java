package precisefloating.continuedfractions;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class AddContinuedFraction extends ContinuedFraction {

    private final ContinuedFraction x, y;

    public AddContinuedFraction(ContinuedFraction x, ContinuedFraction y) {
        this.x = x;
        this.y = y;
    }

    public PartialQuotients partialQuotients() {
        return new AddPartialQuotients(x.partialQuotients(), y.partialQuotients());
    }

}
