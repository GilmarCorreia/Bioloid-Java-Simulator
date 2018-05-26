package precisefloating.continuedfractions;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
class EPartialQuotients extends InfinitePartialQuotients implements Cloneable, Serializable {

    byte b;
    BigInteger x;

    protected BigInteger computeNext() {
        BigInteger next;

        if (!isStarted()) {
            next = BigInteger.valueOf(2);
            x = BigInteger.ZERO;
            b = 2;
        } else {
            if (b == 1) {
                next = BigInteger.ONE;
                b = 2;
            } else {
                if (b == 2) {
                    next = BigInteger.ONE;
                    b = 0;
                } else {
                    assert b == 0;

                    x = x.add(BigInteger.valueOf(2));
                    next = x;
                    b = 1;
                }
            }

        }

        return next;
    }

}
