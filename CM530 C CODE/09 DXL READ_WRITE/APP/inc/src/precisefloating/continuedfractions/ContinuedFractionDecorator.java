package precisefloating.continuedfractions;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class ContinuedFractionDecorator extends ContinuedFraction {

    private final ContinuedFraction inner;

    public PartialQuotients partialQuotients() {
        return inner.partialQuotients();
    }

    public ContinuedFractionDecorator(ContinuedFraction inner) {
        this.inner = inner;
    }

    public ContinuedFraction getInner() {
        return inner;
    }

    public Convergents convergents() {
        return inner.convergents();
    }

    public ContinuedFraction inverse() {
        return inner.inverse();
    }

    public ContinuedFraction negate() {
        return inner.negate();
    }

    public ContinuedFraction add(ContinuedFraction y) {
        return inner.add(y);
    }

    public ContinuedFraction subtract(ContinuedFraction y) {
        return inner.subtract(y);
    }

    public ContinuedFraction multiply(ContinuedFraction y) {
        return inner.multiply(y);
    }

    public ContinuedFraction square() {
        return inner.square();
    }

    public ContinuedFraction power(long exponent) {
        return inner.power(exponent);
    }

    public ContinuedFraction power(BigInteger exponent) {
        return inner.power(exponent);
    }

    public ContinuedFraction divide(ContinuedFraction y) {
        return inner.divide(y);
    }

    public BigInteger floor() {
        return inner.floor();
    }

    public BigInteger ceil() {
        return inner.ceil();
    }

}
