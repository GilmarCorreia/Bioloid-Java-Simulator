package precisefloating.continuedfractions;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class SquarePartialQuotients extends MultiplyPartialQuotients {

    public SquarePartialQuotients(PartialQuotients inner) {
        super(factors(inner));
    }

    private static RepeatedPartialQuotientsDecorator[] factors(PartialQuotients inner) {
        RepeatedPartialQuotients twice = new RepeatedPartialQuotients(inner);
        return new RepeatedPartialQuotientsDecorator[]{
            new RepeatedPartialQuotientsDecorator(twice),
            new RepeatedPartialQuotientsDecorator(twice)
        };
    }

}
