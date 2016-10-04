package btree;

/**
 * B+Tree Lab
 * @author Michael Hay mhay@colgate.edu
 */
public class BTreeMain {

    /**
     * Write a program that demonstrates your B+Tree implementation.  This program must
     * clearly demonstrate the full functionality of your implementation.  E.g., you will
     * want to insert enough records to trigger splitting of both leaf and non-leaf nodes.
     * @param args
     */
    public static void main(String[] args) {
        BTree bTree = new BTree(2);
        bTree.insert(2, "record r2");
        try {
            bTree.insert(2, "record r2");
            throw new RuntimeException("should not reach here!");
        } catch (BTreeException e) {
            // expected
        }
        System.out.println(bTree.getRecord(2));  // should print 'record r2'
        System.out.println(bTree);               // should print entire tree
    }
}
