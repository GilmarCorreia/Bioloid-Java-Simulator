package precisefloating.continuedfractions;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class DividePartialQuotients extends TensorStartPartialQuotients {

    private PartialQuotients zPartialQuotients;

    protected DividePartialQuotients(PartialQuotients[] factors) {
        this(factors[0], factors[1]);
    }

    public DividePartialQuotients(PartialQuotients xPartialQuotients,
            PartialQuotients yPartialQuotients) {
        super(0, 1, 0, 0, 0, 0, 1, 0, xPartialQuotients, yPartialQuotients);
    }

}
