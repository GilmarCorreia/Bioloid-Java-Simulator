package precisefloating.continuedfractions;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class SubtractPartialQuotients extends TensorStartPartialQuotients {

    public SubtractPartialQuotients(PartialQuotients xPartialQuotients,
            PartialQuotients yPartialQuotients) {
        super(0, 1, -1, 0, 0, 0, 0, 1, xPartialQuotients, yPartialQuotients);
    }

}
