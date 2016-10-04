package btree;


/**
 * B+Tree Lab
 * @author Michael Hay mhay@colgate.edu
 */
public class BTree {


    /**
     * Create a B+Tree having the specified order.
     * @param order the order of the tree (the d parameter in lecture and in the book).
     */
    public BTree(int order) {
        throw new UnsupportedOperationException("implement me!");
    }

    /**
     * Inserts a record with the given key.
     * @param key search key value of record
     * @param record record to insert
     * @throws BTreeException if this key already contained in tree
     */
    public void insert(int key, Object record) {
        throw new UnsupportedOperationException("implement me!");
    }

    /**
     * Searches for record with given key value.
     * @param key search key
     * @return Record associated with given search key value or null if no such record exists.
     */
    public Object getRecord(int key) {
        throw new UnsupportedOperationException("implement me!");
    }

    /**
     * Returns a nicely formatted string representation of the entire tree.  The string representation
     * should be clear enough that someone could look at it and simulate search and/or insertion of
     * new entries.
     * @return string representation of the tree
     */
    @Override
    public String toString() {
        throw new UnsupportedOperationException("implement me!");
    }

    /**
     * Internal tree node.
     * @see IndexEntry
     */
    public static class Node {

    }
}
