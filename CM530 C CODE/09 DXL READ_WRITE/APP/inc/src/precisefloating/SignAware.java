package precisefloating;

/**
 * Represents numbers that are at least partially aware of their sign. If any method
 * cannot be implemented with very good performance, the result should be simply
 * be <code>null<code>.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public interface SignAware {

    Boolean tryIsNegative();

    Boolean tryIsPositive();

    Boolean tryIsZero();

    Integer trySignum();

}
