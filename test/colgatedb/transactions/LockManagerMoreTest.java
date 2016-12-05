package colgatedb.transactions;

import colgatedb.page.PageId;
import colgatedb.page.SimplePageId;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

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

/**
 * Tests basic functionality of the LockManager w/o any concurrency.
 */
public class LockManagerMoreTest {
    private TransactionId tid1 = new TransactionId();
    private TransactionId tid2 = new TransactionId();
    private TransactionId tid3 = new TransactionId();
    private SimplePageId pid1 = new SimplePageId(0, 1);
    private SimplePageId pid2 = new SimplePageId(0, 2);
    private LockManager lm;

    @Before
    public void setUp() {
        lm = new LockManagerImpl();
    }

    @Test
    public void holdsLockExclusiveAfterUpgrade() throws TransactionAbortedException {
        assertFalse(lm.holdsLock(tid1, pid1, Permissions.READ_ONLY));
        assertFalse(lm.holdsLock(tid1, pid1, Permissions.READ_WRITE));
        lm.acquireLock(tid1, pid1, Permissions.READ_ONLY);
        assertTrue(lm.holdsLock(tid1, pid1, Permissions.READ_ONLY));

        lm.acquireLock(tid1, pid1, Permissions.READ_WRITE);
        assertTrue(lm.holdsLock(tid1, pid1, Permissions.READ_ONLY));
        assertTrue(lm.holdsLock(tid1, pid1, Permissions.READ_WRITE));
    }

}
