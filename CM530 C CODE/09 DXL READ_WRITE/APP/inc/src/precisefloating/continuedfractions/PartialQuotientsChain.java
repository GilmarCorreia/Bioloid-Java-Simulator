package precisefloating.continuedfractions;

import org.apache.commons.collections.iterators.IteratorChain;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

/**
 * Description adapted from <code>org.apache.commons.collections.iterators.IteratorChain</code>.
 * <p/>
 * A PartialQuotientsChain is an PartialQuotients that wraps one or more
 * PartialQuotients. When any method from the PartialQuotients class is called,
 * the IteratorChain will proxy to a single underlying PartialQuotients.
 * The PartialQuotientsChain will invoke the PartialQuotientsIterators in sequence
 * until all PartialQuotientsIterators are exhausted completely.
 * Under many circumstances, linking PartialQuotientsIterators together in this manner is
 * more efficient (and convenient) than reading out the contents of each Iterator into a List
 * and creating a new Iterator. Calling a method that adds new PartialQuotients
 * after a method in the PartialQuotients interface has been called will result in
 * an UnsupportedOperationException. Subclasses should take care to not alter the underlying
 * List of Iterators.
 * 
 * @author Daniel Aioanei (aioaneid@go.ro)
 * @see IteratorChain
 */
public class PartialQuotientsChain extends PartialQuotients {

    protected IteratorChain chain;

    public PartialQuotientsChain() {
        chain = new IteratorChain();
    }

    public PartialQuotientsChain(PartialQuotients iterator) {
        chain = new IteratorChain(iterator);
    }

    public boolean hasNext() {
        return chain.hasNext();
    }

    public PartialQuotientsChain(PartialQuotients x,
            PartialQuotients y) {
        chain = new IteratorChain(x, y);
    }

    public PartialQuotientsChain(PartialQuotients[] iterators) {
        chain = new IteratorChain(iterators);
    }

    public PartialQuotientsChain(List list) {
        chain = new IteratorChain(checkedList(list));
    }

    protected static List checkedList(List list) {
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            Iterator it = (Iterator) iterator.next();
            if (it != null && !(it instanceof PartialQuotients)) {
                throw new ClassCastException("Could not convert " + it.getClass()
                        + " to " + PartialQuotients.class);
            }
        }

        return list;
    }

    public void addIterator(PartialQuotients iterator) {
        chain.addIterator(iterator);
    }

    public void setIterator(int i, PartialQuotients iterator)
            throws IndexOutOfBoundsException {
        chain.setIterator(i, iterator);
    }

    protected BigInteger computeNext() {
        return (BigInteger) chain.next();
    }

    public List getIterators() {
        return chain.getIterators();
    }

    public boolean isLocked() {
        return chain.isLocked();
    }

}
