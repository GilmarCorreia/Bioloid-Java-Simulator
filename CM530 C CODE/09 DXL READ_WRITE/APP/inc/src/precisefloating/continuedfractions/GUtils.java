package precisefloating.continuedfractions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public final class GUtils {

    private GUtils() {
    }

    static List toList(BigInteger[] bigints, int[] values, boolean nonnegative, int bufferSize) {
        List list = new ArrayList(nonnegative ? bufferSize : bufferSize * 3 + 1);

        if (nonnegative) {
            for (int i = 0; i < values.length; i++) {
                if (values[i] >= 0) {
                    list.add(bigints[values[i]]);
                } else {
                    assert values[i] <= -2;
                    int x = values[i - 1];
                    for (int j = 0; j < -values[i]; j++) {
                        list.add(bigints[++x]);
                    }
                }
            }
        } else {
            list.add(BigInteger.ONE);

            for (int i = 0; i < values.length; i++) {
                if (values[i] >= 0) {
                    list.add(bigints[values[i]]);
                    list.add(BigInteger.ONE);
                    list.add(BigInteger.ONE);
                } else {
                    assert values[i] <= -2;
                    int x = values[i - 1];
                    for (int j = 0; j < -values[i]; j++) {
                        list.add(bigints[++x]);
                        list.add(BigInteger.ONE);
                        list.add(BigInteger.ONE);
                    }
                }
            }
        }

        list = Collections.unmodifiableList(list);
        return list;
    }

    static BigInteger[] parse(String s) {
        StringTokenizer st = new StringTokenizer(s, "/");
        BigInteger[] a = new BigInteger[st.countTokens()];

        for (int i = 0; i < a.length; i++) {
            a[i] = new BigInteger(st.nextToken(), Character.MAX_RADIX);
        }

        assert !st.hasMoreTokens();

        return a;
    }

    static BigInteger[] parse(String[] sa) {
        StringTokenizer[] sta = new StringTokenizer[sa.length];

        int count = 0;
        for (int i = 0; i < sta.length; i++) {
            sta[i] = new StringTokenizer(sa[i], "/");
            count += sta[i].countTokens();
        }

        BigInteger[] a = new BigInteger[count];

        for (int i = 0, j = 0; i < sta.length; i++) {
            while (sta[i].hasMoreTokens()) {
                a[j++] = new BigInteger(sta[i].nextToken(), Character.MAX_RADIX);
            }
        }

        assert !sta[sta.length - 1].hasMoreTokens();

        return a;
    }

}
