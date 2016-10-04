package btree;

/**
 * B+Tree Lab
 * @author Michael Hay mhay@colgate.edu
 * <p>
 * An IndexEntry object represents an entry in a non-leaf node of a B+Tree.
 */
public class IndexEntry extends Entry {

    private final int key;
    private final BTree.Node subtree;

    public IndexEntry(int key, BTree.Node subtree) {
        this.key = key;
        this.subtree = subtree;
    }

    public BTree.Node getNode() {
        return subtree;
    }

    @Override
    public String toString() {
        return "(" + subtree + "," + key + ")";
    }

}
