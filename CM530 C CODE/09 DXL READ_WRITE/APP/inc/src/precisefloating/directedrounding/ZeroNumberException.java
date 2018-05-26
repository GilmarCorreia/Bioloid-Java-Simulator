package precisefloating.directedrounding;

/**
 * Indicates a zero double precision number.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class ZeroNumberException extends ArithmeticException {

    public ZeroNumberException() {
    }

    public ZeroNumberException(String s) {
        super(s);
    }

}
