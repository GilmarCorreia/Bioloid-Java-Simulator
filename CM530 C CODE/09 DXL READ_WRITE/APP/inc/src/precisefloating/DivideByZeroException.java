package precisefloating;

/**
 * Indicates an attempt to divide an integral by zero.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class DivideByZeroException extends ArithmeticException {

    public DivideByZeroException() {
    }

    public DivideByZeroException(String s) {
        super(s);
    }

}
