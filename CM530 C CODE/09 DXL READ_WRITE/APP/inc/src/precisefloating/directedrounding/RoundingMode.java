package precisefloating.directedrounding;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public final class RoundingMode implements Serializable {

    private final transient String name;
    private final int no;

    private RoundingMode(String name, int no) {
        this.name = name;
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    /**
     * Rounding mode to round towards positive infinity.  If the
     * BigDecimal is positive, behaves as for <tt>ROUND_UP</tt>; if negative,
     * behaves as for <tt>ROUND_DOWN</tt>.  Note that this rounding mode never
     * decreases the calculated value.
     */
    public static final RoundingMode ROUND_CEILING =
            new RoundingMode("round towards positive infinity", BigDecimal.ROUND_CEILING);

    /**
     * Rounding mode to round towards negative infinity.  If the
     * BigDecimal is positive, behave as for <tt>ROUND_DOWN</tt>; if negative,
     * behave as for <tt>ROUND_UP</tt>.  Note that this rounding mode never
     * increases the calculated value.
     */
    public static final RoundingMode ROUND_FLOOR =
            new RoundingMode("round towards negative infinity", BigDecimal.ROUND_FLOOR);

    /**
     * Rounding mode to round towards the "nearest neighbor" unless both
     * neighbors are equidistant, in which case, round towards the even
     * neighbor.
     * Note that this is the rounding mode that minimizes cumulative error
     * when applied repeatedly over a sequence of calculations.
     */
    public static final RoundingMode ROUND_HALF_EVEN =
            new RoundingMode("round towards nearest", BigDecimal.ROUND_HALF_EVEN);

    public Object readResolve() throws ObjectStreamException {
        switch (no) {
            case BigDecimal.ROUND_CEILING:
                return ROUND_CEILING;
            case BigDecimal.ROUND_FLOOR:
                return ROUND_FLOOR;
            case BigDecimal.ROUND_HALF_EVEN:
                return ROUND_HALF_EVEN;
            default:
                throw new InvalidObjectException("no = " + no);
        }
    }

}
