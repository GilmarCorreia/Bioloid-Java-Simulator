package precisefloating.continuedfractions;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class SquareContinuedFraction extends ContinuedFraction {

    private final ContinuedFraction inner;

    public ContinuedFraction getInner() {
        return inner;
    }

    public SquareContinuedFraction(ContinuedFraction inner) {
        this.inner = inner;
    }

    public PartialQuotients partialQuotients() {
        return new SquarePartialQuotients(inner.partialQuotients());
    }

}
