package precisefloating.continuedfractions;

import precisefloating.Rational;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * For any nozero x, e^x is always irrational. For any positive x, e^x > 1.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class ExponentialPartialQuotients extends PartialQuotientsDecorator {

    private final Rational exponent;
    private final ETable table;
    private static final Logger log = Logger.getLogger(ExponentialPartialQuotients.class.getName());

    public ExponentialPartialQuotients(Rational exponent) {
        this(exponent, EGeneratedTable.getInstance());
    }

    public ExponentialPartialQuotients(Rational exponent, ETable table) {
        super(computeInner(exponent.reduced(), table));

        this.exponent = exponent.reduced();
        this.table = table;
    }

    public Rational getExponent() {
        return exponent;
    }

    private static final int bufferSize = 1536;

    /**
     * @param exponent a positive number
     */
    private static PartialQuotients computeInner(Rational exponent, ETable table) {
        if (exponent.signum() != +1) {
            throw new IllegalArgumentException("exponent must be positive but it is " + exponent);
        }

        assert exponent.isReduced();

        final int n = exponent.getNumerator().bitCount();
        assert n >= 1;

        int[] setIndexes = new int[n];

        for (int i = 0, j = 0; i < n; j++) {
            assert j < exponent.getNumerator().bitLength();
            if (exponent.getNumerator().testBit(j)) {
                setIndexes[i++] = j;
            }
        }

        int denominatorAsInt;

        if (exponent.getDenominator().bitCount() == 1) {
            denominatorAsInt = exponent.getDenominator().bitLength() - 1;
            assert denominatorAsInt >= 0;
        } else {
            denominatorAsInt = -1;
        }

        assert denominatorAsInt >= -1;

        PartialQuotients[] twoPowers = new PartialQuotients[n];
        SharedPartialQuotients[] a = new SharedPartialQuotients[2];

        PartialQuotients radical;

        if (denominatorAsInt == 0) {
            radical = new EPartialQuotients();
        } else {
            radical = new ERadicalPartialQuotients(exponent.getDenominator());
        }

        List radicalList;

        if (denominatorAsInt >= 0) {
            radicalList = table.getBuffer(-denominatorAsInt);
        } else {
            assert denominatorAsInt == -1;
            radicalList = null;
        }

        if (radicalList != null) {
            radical = new CachePartialQuotientsDecorator(radicalList, radical);
        }

        for (int i = 0; i < twoPowers.length; i++) {
            PartialQuotients radicalClone;

            try {
                radicalClone = (PartialQuotients) radical.clone();
            } catch (CloneNotSupportedException e) {
                throw new InternalError("could not clone radical because " + e);
            }

            List list = null;

            if (denominatorAsInt >= 0) {
                list = table.getBuffer(setIndexes[i] - denominatorAsInt);
            } else {
                list = null;
            }

            if (i == 0) {
                twoPowers[0] = PowerPartialQuotients.repeatedSquaring(radicalClone, setIndexes[0]);
            } else {
                PartialQuotients xi_1 = PowerPartialQuotients.repeatedSquaring(radicalClone, setIndexes[i - 1]);
                SharedPartialQuotients.share(twoPowers[i - 1], xi_1, bufferSize, a, 0);
                twoPowers[i - 1] = a[0];
                twoPowers[i] = PowerPartialQuotients.repeatedSquaring(a[1], setIndexes[i] - setIndexes[i - 1]);
            }

            if (list != null) {
                twoPowers[i] = new CachePartialQuotientsDecorator(list, twoPowers[i]);
            }
        }

        if (n == 1) {
            return twoPowers[0];
        } else {
            return completeBinaryTree(twoPowers);
            // return leftToRight(twoPowers);
            // return rightToLeft(twoPowers);
        }
    }

    private static PartialQuotients rightToLeft(PartialQuotients[] twoPowers) {
        PartialQuotients product = twoPowers[twoPowers.length - 1];

        for (int i = twoPowers.length - 2; i >= 0; i--) {
            product = new MultiplyPartialQuotients(product, twoPowers[i]);
        }

        return product;
    }

    private static PartialQuotients leftToRight(PartialQuotients[] twoPowers) {
        PartialQuotients product = twoPowers[0];

        for (int i = 1; i < twoPowers.length; i++) {
            product = new MultiplyPartialQuotients(product, twoPowers[i]);
        }

        return product;
    }

    private static PartialQuotients completeBinaryTree(PartialQuotients[] twoPowers) {
        int l = twoPowers.length;

        log.finer("starting while loop");

        while (l > 1) {
            if (log.isLoggable(Level.FINEST)) {
                log.finest("l = " + l);
            }

            for (int i = 0; i < l / 2; i++) {
                twoPowers[i] = new MultiplyPartialQuotients(twoPowers[i * 2], twoPowers[i * 2 + 1]);
                if (log.isLoggable(Level.FINEST)) {
                    log.finest(i + " <-- " + (i * 2) + " * " + (i * 2 + 1));
                }
            }

            if (l % 2 == 1) {
                twoPowers[(l - 1) / 2] = twoPowers[l - 1];
                if (log.isLoggable(Level.FINEST)) {
                    log.finest((l - 1) / 2 + " <-- " + (l - 1));
                }
            }

            l = (l + 1) / 2;
        }

        return twoPowers[0];
    }

}
