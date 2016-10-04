package btree;

/**
 * B+Tree Lab
 * @author Michael Hay mhay@colgate.edu
 * <p>
 * A DataEntry object represents an alternative 1 data entry.  Data entries
 * are stored in the leaves of B+Trees.
 */
public class DataEntry extends Entry {

    private final int key;
    private final Object record;

    public DataEntry(int key, Object record) {
        this.key = key;
        this.record = record;
    }

    @Override
    public String toString() {
        return key + "*";
    }

    public Object getRecord() {
        return record;
    }
}
