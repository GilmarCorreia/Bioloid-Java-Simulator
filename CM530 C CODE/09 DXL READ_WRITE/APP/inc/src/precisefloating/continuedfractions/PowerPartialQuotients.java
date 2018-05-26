package precisefloating.continuedfractions;

import precisefloating.Formulas;
import precisefloating.Rational;

import java.math.BigInteger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class PowerPartialQuotients extends PartialQuotients {

    private PartialQuotients[] x;
    private final BigInteger exponent;

    public PowerPartialQuotients(PartialQuotients[] x, BigInteger exponent) {
        this.x = x;
        this.exponent = exponent;
    }

    public BigInteger getExponent() {
        return exponent;
    }

    protected BigInteger computeNext() {
        if (!isStarted()) {
            BigInteger x0 = x[0].nextPartialQuotient();

            if (!x[0].hasNext()) {
                if (x0.signum() == 0) {
                    if (exponent.signum() <= 0) {
                        // cannot rely on BigInteger.pow because it defines 0 ^ x = 1, where x <= 0
                        // and that is not desired
                        throw new ArithmeticException();
                    } else {
                        x[0] = new SingletonPartialQuotient(BigInteger.ZERO);
                    }
                } else {
                    BigInteger absExponent = exponent.abs();

                    if (absExponent.compareTo(Formulas.BIG_MAX_INTEGER) <= 0) {
                        // BigInteger can handle it
                        BigInteger pow = x0.pow(absExponent.intValue());
                        assert pow.signum() == +1;
                        if (exponent.signum() == -1) {
                            // 1 / pow
                            x[0] = Rational.create(BigInteger.ONE, pow).expansion().partialQuotients();
                        } else {
                            x[0] = new SingletonPartialQuotient(pow);
                        }
                    }
                }
            } else {
                if (exponent.signum() == 0) {
                    assert x[0].hasNext(); // x is not equal to 0
                    x[0] = new SingletonPartialQuotient(BigInteger.ONE);
                } else {
                    PartialQuotients xReloaded = new BeforePartialQuotients(x0, x[0]);
                    PartialQuotients[] absReloadedX = new PartialQuotients[x.length];

                    if (x0.signum() == -1) {
                        absReloadedX[0] = new NegatePartialQuotients(xReloaded);
                        for (int i = 1; i < x.length; i++) {
                            absReloadedX[i] = new NegatePartialQuotients(x[i]);
                        }
                    } else {
                        absReloadedX[0] = xReloaded;

                        for (int i = 1; i < x.length; i++) {
                            absReloadedX[i] = x[i];
                        }
                    }

                    BigInteger absExponent = exponent.abs();

                    x[0] = positiveXPositivePow(absReloadedX, absExponent);

                    // odd numbers have bit 0 set, no matter the sign
                    if (x0.signum() == -1 && exponent.testBit(0)) {
                        x[0] = new NegatePartialQuotients(x[0]);
                    }

                    if (exponent.signum() == -1) {
                        x[0] = new InversePartialQuotients(x[0]);
                    }
                }
            }
        }

        return x[0].nextPartialQuotient();
    }

    private static final int bufferSize = 1536;

    /**
     * @param x        is a positive number with at least 2 partial quotients
     * @param exponent a positive number
     */
    private static PartialQuotients positiveXPositivePow(PartialQuotients[] x,
            BigInteger exponent) {
        assert exponent.signum() == +1;

        final int n = exponent.bitCount();
        assert n >= 1;

        int[] setIndexes = new int[n];
        for (int i = 0, j = 0; i < n; j++) {
            assert j < exponent.bitLength();
            if (exponent.testBit(j)) {
                setIndexes[i++] = j;
            }
        }

        PartialQuotients[] twoPowers = new PartialQuotients[n];
        SharedPartialQuotients[] a = new SharedPartialQuotients[2];

        for (int i = 0; i < twoPowers.length; i++) {
//            twoPowers[i] = repeatedSquaring(x[i], setIndexes[i]);

            if (i == 0) {
                twoPowers[0] = repeatedSquaring(x[0], setIndexes[0]);
            } else {
                PartialQuotients xi_1 = repeatedSquaring(x[i], setIndexes[i - 1]);
                SharedPartialQuotients.share(twoPowers[i - 1], xi_1, bufferSize, a, 0);
                twoPowers[i - 1] = a[0];
                twoPowers[i] = repeatedSquaring(a[1], setIndexes[i] - setIndexes[i - 1]);
            }
        }

        if (n == 1) {
            return twoPowers[0];
        } else {
            PartialQuotients product = twoPowers[n - 1];

            for (int i = n - 2; i >= 0; i--) {
                product = new MultiplyPartialQuotients(product, twoPowers[i]);
            }

            return product;
        }
    }

    static PartialQuotients repeatedSquaring(
            PartialQuotients x, int twoExponent) {
        assert twoExponent >= 0;

        for (int i = 0; i < twoExponent; i++) {
            x = new SquarePartialQuotients(x);
        }

        return x;
    }

    public boolean hasNext() {
        return x[0].hasNext();
    }

}
