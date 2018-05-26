package precisefloating.continuedfractions;

import java.util.List;

/**
 * Not a singleton.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class EEmptyTable implements ETable {

    private static final EEmptyTable instance = new EEmptyTable();

    public List getBuffer(int twoPower) {
        return null;
    }

    public static EEmptyTable getInstance() {
        return instance;
    }

}
