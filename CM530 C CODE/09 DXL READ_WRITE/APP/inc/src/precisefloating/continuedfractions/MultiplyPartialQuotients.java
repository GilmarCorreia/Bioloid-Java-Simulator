package precisefloating.continuedfractions;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class MultiplyPartialQuotients extends TensorStartPartialQuotients {

    private PartialQuotients zPartialQuotients;

    protected MultiplyPartialQuotients(PartialQuotients[] factors) {
        this(factors[0], factors[1]);
    }

    public MultiplyPartialQuotients(PartialQuotients xPartialQuotients,
            PartialQuotients yPartialQuotients) {
        super(1, 0, 0, 0, 0, 0, 0, 1, xPartialQuotients, yPartialQuotients);
    }

}
