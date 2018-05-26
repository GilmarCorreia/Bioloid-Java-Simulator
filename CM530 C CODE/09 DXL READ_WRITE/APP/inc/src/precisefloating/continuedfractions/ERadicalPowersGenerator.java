package precisefloating.continuedfractions;

import precisefloating.Rational;
import precisefloating.directedrounding.ManagedRoundingModeControl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.*;

/**
 * Used to populate the cache tables used by the exponentiation routine.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class ERadicalPowersGenerator {

    private static int twoPowerStart, twoPowerCount, bufferSize;
    private static PrintWriter out;
    private static Map bigToIndex;
    private static int[][] v;
    private static List bigints;

    /**
     * [0, 61] - [0, 104] = [-104, 61]
     * 
     * @param args -105 115 1280 src/com/deployedprimes/precisefloating/continuedfractions/EGeneratedTable.java
     */
    public static void main(String[] args) throws FileNotFoundException {
        twoPowerStart = Integer.parseInt(args[0]);
        twoPowerCount = Integer.parseInt(args[1]);
        bufferSize = Integer.parseInt(args[2]);
        bigToIndex = new HashMap(twoPowerCount * bufferSize + 1, 1);
        v = new int[twoPowerCount][bufferSize];
        bigints = new ArrayList();

        out = new PrintWriter(new FileOutputStream(args[3]));

        try {
            start();

            Rational expPositiveInfinityAsRational = Rational.create(ManagedRoundingModeControl.EXP_POSITIVE_INFINITY);

            for (int i = twoPowerStart; i < twoPowerStart + twoPowerCount; i++) {
                startTwoPowerIndex(i);

                Rational p = Rational.valueOf(BigInteger.ONE.shiftLeft(i));

                // do not generate not needed numbers
                assert p.compareTo(expPositiveInfinityAsRational) <= 0;

                System.out.print(i + " ");

                PartialQuotients x = eTwoPower(i);

                for (int k = 0; k < bufferSize; k++) {
                    BigInteger partialQuotient = x.nextPartialQuotient();
                    partialQuotient(i, k, partialQuotient);
                }

                endTwoPowerIndex(i);
            }

            end();
        } finally {
            out.close();
        }
    }

    private static List lists = new ArrayList();

    private static void end() {

        if (sb.length() > 0) {
            bigStrings.add(sb.toString());
        }

        for (int i = 0; i < bigStrings.size(); i++) {
            out.print("bigString" + i + "(), ");
        }

        out.println();
        out.println("       });");
        out.println();

        out.println("        table = new List[] {");

        ArrayList indexes = new ArrayList(bufferSize * 3 + 1);

        for (int i = twoPowerStart; i < twoPowerStart + twoPowerCount; i++) {
            indexes.clear();

            for (int j = 0; j < bufferSize && v[i - twoPowerStart][j] >= 0; j++) {
                if (j > 0) {
                    int k;

                    for (k = j + 1; k < bufferSize && v[i - twoPowerStart][k] >= 0
                            && v[i - twoPowerStart][k] == v[i - twoPowerStart][k - 1] + 1; k++) {
                    }

                    int consecutiveCount = k - j;

                    if (consecutiveCount > 2) {
                        indexes.add(new Integer(v[i - twoPowerStart][j]));
                        // assert ((Integer) indexes.get(indexes.size() - 1)).intValue() >= 0;

                        indexes.add(new Integer(1 - consecutiveCount));
                        j = k - 1;
                    } else {
                        indexes.add(new Integer(v[i - twoPowerStart][j]));
                    }
                } else {
                    indexes.add(new Integer(v[i - twoPowerStart][j]));
                }
            }

            if (indexes.size() < 128) {
                out.println("                GUtils.toList(bigints, new int[] {");
                out.print("                     ");

                for (int j = 0; j < indexes.size(); j++) {
                    out.print(indexes.get(j) + ", ");
                }

                out.println();
                out.println("               }, " + (i >= 0) + ", " + bufferSize + "),");
            } else {
                out.println("                GUtils.toList(bigints, intArray" + lists.size() + "(), " + (i >= 0) + "," + bufferSize + "),");
                lists.add(indexes.clone());
            }
        }

        out.println("        };");
        out.println("    }");
        out.println();

        int j = 0;
        for (Iterator iterator = bigStrings.iterator(); iterator.hasNext();) {
            String s = (String) iterator.next();

            out.println("   private static String bigString" + j + "() {");
            out.print("         return \"");
            out.print(s);
            out.println("\";");
            out.println("   }");
            out.println();

            j++;
        }

        for (int i = 0; i < lists.size(); i++) {
            List list = (List) lists.get(i);
            out.println("   private static int[] intArray" + i + "() {");
            out.println("       return new int[] {");
            out.print("         ");
            for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                out.print(iterator.next() + ", ");
            }
            out.println();
            out.println("       };");
            out.println("   }");
            out.println();
        }

        out.println("}");
    }

    private static StringBuffer sb = new StringBuffer(2048);
    private static List bigStrings = new LinkedList();

    private static void partialQuotient(int i, int j, BigInteger partialQuotient) {
        if (i >= 0 || j % 3 == 1) {
            Integer indexAsInteger = (Integer) bigToIndex.get(partialQuotient);

            if (indexAsInteger == null) {
                indexAsInteger = new Integer(bigToIndex.size());
                bigToIndex.put(partialQuotient, indexAsInteger);
                bigints.add(partialQuotient);

                sb.append(partialQuotient.toString(Character.MAX_RADIX));
                sb.append('/');

                if (sb.length() >= 2048) {
                    bigStrings.add(sb.toString());
                    sb.setLength(0);
                }
            }

            v[i - twoPowerStart][i >= 0 ? j : (j - 1) / 3] = indexAsInteger.intValue();
        }
    }

    private static void start() {
        out.print(
                "package precisefloating.continuedfractions;\n" +
                "\n" +
                "import java.util.List;\n" +
                "import java.util.logging.Logger;\n" +
                "import java.math.BigInteger;\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.Collections;\n" +
                "\n" +
                "import precisefloating.Formulas;\n" +
                "\n" +
                "/**\n" +
                " * java -ea -cp classes:lib/junit.jar:lib/JAMon.jar:lib/commons-collections.jar precisefloating.continuedfractions.ERadicalPowersGenerator " + twoPowerStart + " " + twoPowerCount + " " + bufferSize + " src/com/deployedprimes/precisefloating/continuedfractions/EGeneratedTable.java\n" +
                " *\n" +
                " * @author Daniel Aioanei (aioaneid@go.ro)\n" +
                " */\n" +
                "public class EGeneratedTable implements ETable {\n" +
                "\n" +
                "    private static EGeneratedTable instance = new EGeneratedTable();\n" +
                "    private static final Logger log = Logger.getLogger(EGeneratedTable.class.getName());\n" +
                "\n" +
                "    public List getBuffer(int twoPower) {\n" +
                "        List list = null;\n" +
                "\n" +
                "        int directionIndex = twoPower - twoPowerStart;\n" +
                "\n" +
                "        if (Formulas.inOpenInterval(directionIndex, -1, table.length)) {\n" +
                "                list = table[directionIndex];\n" +
                "        }\n" +
                "\n" +
                "        if (list == null) {\n" +
                "            log.info(twoPower + \" not found in cache\");\n" +
                "        }\n" +
                "\n" +
                "        return list;\n" +
                "    }\n" +
                "\n" +
                "    public static EGeneratedTable getInstance() {\n" +
                "        if (instance == null) {\n" +
                "            synchronized (EGeneratedTable.class) {\n" +
                "                if (instance == null) {\n" +
                "                    instance = new EGeneratedTable();\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        return instance;\n" +
                "    }" +
                "\n" +
                "    private final List[] table;\n" +
                "    private final int twoPowerStart;\n" +
                "\n" +


                "    private EGeneratedTable() {\n" +
                "        twoPowerStart = " + twoPowerStart + ";\n" +
                "\n" +
                "       BigInteger[] bigints = GUtils.parse(new String[] {\n" +
                "           ");
    }


    private static void endTwoPowerIndex(int i) {
    }

    private static void startTwoPowerIndex(int i) {
        Arrays.fill(v[i - twoPowerStart], -1);
    }

    public static PartialQuotients eTwoPower(int i) {
        if (i == 0) {
            return ContinuedFractionConstants.E.partialQuotients();
        } else {
            if (i < 0) {
                return new ERadicalPartialQuotients(BigInteger.ONE.shiftLeft(-i));
            } else {
                assert i > 0;
                return PowerPartialQuotients.repeatedSquaring(
                        ContinuedFractionConstants.E.partialQuotients(), i);
            }
        }
    }

}
