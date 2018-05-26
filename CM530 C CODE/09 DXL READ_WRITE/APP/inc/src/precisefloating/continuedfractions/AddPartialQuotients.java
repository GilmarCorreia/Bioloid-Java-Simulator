package precisefloating.continuedfractions;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class AddPartialQuotients extends TensorStartPartialQuotients {

    public AddPartialQuotients(PartialQuotients xPartialQuotients,
            PartialQuotients yPartialQuotients) {
        super(0, 1, 1, 0, 0, 0, 0, 1, xPartialQuotients, yPartialQuotients);
    }

}
