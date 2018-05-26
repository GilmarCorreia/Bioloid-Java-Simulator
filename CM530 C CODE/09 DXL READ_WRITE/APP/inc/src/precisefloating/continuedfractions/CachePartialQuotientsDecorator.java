package precisefloating.continuedfractions;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daniel Aioanei (aioaneid@go.ro)
 */
public class CachePartialQuotientsDecorator extends PartialQuotientsDecorator
        implements Cloneable, Serializable {

    // todo add original when assertion enabled and ! iterator instanceof ScrollableIterator
    private LinkedList list;
    private final int listSize;
    private static final Logger log = Logger.getLogger(CachePartialQuotientsDecorator.class.getName());

    public CachePartialQuotientsDecorator(BigInteger[] list, PartialQuotients iterator) {
        this(toLinkedList(list), iterator, false);
    }

    public Object clone() throws CloneNotSupportedException {
        CachePartialQuotientsDecorator clone = (CachePartialQuotientsDecorator) super.clone();
        clone.iterator = (PartialQuotients) ((PartialQuotients) iterator).clone();
        clone.list = (LinkedList) list.clone();
        return clone;
    }

    public CachePartialQuotientsDecorator(List list, PartialQuotients iterator) {
        this(toLinkedList(list), iterator, false);
    }

    private static LinkedList toLinkedList(List list) {
        if (list instanceof LinkedList) {
            return (LinkedList) ((LinkedList) list).clone();
        } else {
            LinkedList linkedList = new LinkedList();
            linkedList.addAll(list);
            return linkedList;
        }
    }

    private static LinkedList toLinkedList(BigInteger[] list) {
        LinkedList linkedList = new LinkedList();
        for (int i = 0; i < list.length; i++) {
            linkedList.add(list[i]);
        }
        return linkedList;
    }

    public CachePartialQuotientsDecorator(LinkedList list, PartialQuotients iterator) {
        this(list, iterator, true);
    }

    public CachePartialQuotientsDecorator(LinkedList list, PartialQuotients iterator,
            boolean toClone) {
        super(iterator);

        if (list.isEmpty()) {
            throw new IllegalArgumentException("list must not be empty");
        }

        if (toClone) {
            this.list = (LinkedList) list.clone();
        } else {
            this.list = list;
        }

        listSize = list.size();
        assert listSize >= 1;
    }

    public boolean hasNext() {
        return list != null || super.hasNext();
    }

    protected BigInteger computeNext() {
        BigInteger next;

        if (list == null) {
            next = super.computeNext();
        } else {
            assert !list.isEmpty();

            next = (BigInteger) list.removeFirst();

            if (list.isEmpty()) {
                list = null;

                if (log.isLoggable(Level.INFO)) {
                    log.info(this + " cache will miss at directionIndex " + (index + 1));
                }

                if (iterator instanceof ScrollableIterator) {
                    ScrollableIterator scrollable = (ScrollableIterator) iterator;
                    scrollable.setIndex(listSize - 1);
                } else {
                    for (int i = 0; i < listSize; i++) {
                        iterator.next();
                    }
                }
            }
        }

        if (log.isLoggable(Level.FINEST)) {
            log.finest(this + " " + next);
        }

        return next;
    }

}
