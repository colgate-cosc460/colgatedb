package colgatedb;

import colgatedb.page.Page;
import colgatedb.transactions.Permissions;
import colgatedb.transactions.TransactionAbortedException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * ColgateDB
 * @author Michael Hay mhay@colgate.edu
 * <p>
 * ColgateDB was developed by Michael Hay but borrows considerably from past
 * efforts including SimpleDB (developed by Sam Madden at MIT) and its predecessor
 * Minibase (developed at U. of Wisconsin by Raghu Ramakrishnan).
 * <p>
 * The contents of this file are either wholly the creation of Michael Hay or are
 * a significant adaptation of code from the SimpleDB project.  A number of
 * substantive changes have been made to meet the pedagogical goals of the cosc460
 * course at Colgate.  If this file contains remnants from SimpleDB, we are
 * grateful for Sam's permission to use and adapt his materials.
 */
public class AccessManagerTest2 extends AccessManagerTestBase {


    /**
     * This test can be passed only once Lab11 is complete.
     * @throws TransactionAbortedException
     */
    @Test
    public void testDirtyPagesNotFlushedOnCommit() throws TransactionAbortedException {
        am.setForce(false);
        am.acquireLock(tid0, pid0, Permissions.READ_WRITE);

        MockPage page0 = (MockPage) am.pinPage(tid0, pid0, pm);

        page0.datum = 42;

        am.unpinPage(tid0, page0, true);

        am.transactionComplete(tid0);

        assertFalse(bm.wasFlushed(pid0));  // force is off
        page0 = (MockPage) bm.getPage(pid0);
        MockPage beforeImage = (MockPage) page0.getBeforeImage();
        assertEquals(42, beforeImage.datum);
    }


}
