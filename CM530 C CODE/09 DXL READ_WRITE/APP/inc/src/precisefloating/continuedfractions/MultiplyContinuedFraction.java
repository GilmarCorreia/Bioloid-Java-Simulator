package precisefloating.continuedfractions;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class MultiplyContinuedFraction extends ContinuedFraction {

    private final ContinuedFraction x, y;

    public MultiplyContinuedFraction(ContinuedFraction x, ContinuedFraction y) {
        this.x = x;
        this.y = y;
    }

    public PartialQuotients partialQuotients() {
        return new MultiplyPartialQuotients(x.partialQuotients(), y.partialQuotients());
    }

}
