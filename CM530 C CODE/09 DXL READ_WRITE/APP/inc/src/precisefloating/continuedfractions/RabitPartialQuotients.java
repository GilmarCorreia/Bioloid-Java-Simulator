package precisefloating.continuedfractions;

import java.math.BigInteger;

/**
 * Actually, only the first 47 partial quotients are computed if requested. After that,
 * an ArithmeticException is thrown.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
class RabitPartialQuotients extends InfinitePartialQuotients {

    private int a = 2, b = -1;

    protected BigInteger computeNext() {
        long c = a + b;

        if (c > Integer.MAX_VALUE) {
            throw new ArithmeticException("patialQuotient too large");
        }

        a = b;
        b = (int) c;

        BigInteger next = BigInteger.ONE.shiftLeft(b);

        return next;
    }

}
