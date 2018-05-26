package precisefloating.continuedfractions;

import java.util.List;

/**
 * Classes implementing this interface are expected to be threadsafe.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public interface ETable {

    /**
     * Null if not found in tables.
     */
    List getBuffer(int twoPower);

}
