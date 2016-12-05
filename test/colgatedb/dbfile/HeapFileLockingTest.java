package colgatedb.dbfile;

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

import colgatedb.AccessManager;
import colgatedb.Database;
import colgatedb.TestUtility;
import colgatedb.page.Page;
import colgatedb.page.SimplePageId;
import colgatedb.page.SlottedPage;
import colgatedb.transactions.Permissions;
import colgatedb.transactions.TransactionAbortedException;
import colgatedb.transactions.TransactionId;
import colgatedb.tuple.Tuple;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static colgatedb.dbfile.HeapFileMoreTest.initializeHeapFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * These tests check that locks are appropriately acquired in HeapFile.
 */
public class HeapFileLockingTest {

    protected TransactionId tid0 = new TransactionId();
    private SimplePageId pid0;
    private SimplePageId pid1;
    private HeapFile hf;
    private AccessManager am;

    @Before
    public void setUp() throws IOException {
        Database.reset();
        List<Tuple> tups = new LinkedList<Tuple>();
        hf = initializeHeapFile(new int[]{-1,1}, tups); // first page full, second page one tuple
        assertEquals(2, hf.numPages());
        int tableid = hf.getId();
        pid0 = new SimplePageId(tableid, 0);
        pid1 = new SimplePageId(tableid, 1);
        am = Database.getAccessManager();
    }

    @Test
    public void testLockAcquiredOnDelete() throws TransactionAbortedException {

        // shouldn't have any locks acquired
        checkNoLocksAcquired();

        // get first tuple on second page
        SlottedPage page = (SlottedPage) Database.getBufferManager().pinPage(pid1, HeapFileMoreTest.pm);
        Tuple tuple = page.getTuple(0);

        // only needs to acquire lock on second page (pid1)
        hf.deleteTuple(tid0, tuple);

        // should not have lock on pid0 because tuple is located on pid1
        assertFalse(am.holdsLock(tid0, pid0, Permissions.READ_ONLY));
        // should have acquired lock on pid1 (and not yet released it because strict 2PL)
        assertTrue(am.holdsLock(tid0, pid1, Permissions.READ_WRITE));

    }

    @Test
    public void testLockAcquiredOnIterator() throws TransactionAbortedException {

        // shouldn't have any locks acquired
        checkNoLocksAcquired();

        // only needs to acquire lock on second page (pid1)
        DbFileIterator iterator = hf.iterator(tid0);
        iterator.open();
        while (iterator.hasNext()) {
            iterator.next();
        }
        iterator.close();

        // should still have locks on all pages (strict 2PL)
        assertTrue(am.holdsLock(tid0, pid0, Permissions.READ_ONLY));
        assertTrue(am.holdsLock(tid0, pid1, Permissions.READ_ONLY));
    }

    @Test
    public void testLockAcquiredOnInsert() throws TransactionAbortedException {

        // shouldn't have any locks acquired
        checkNoLocksAcquired();

        insertTuple();

        // should have acquired lock on pid1 (and not yet released it because strict 2PL)
        assertTrue(am.holdsLock(tid0, pid1, Permissions.READ_WRITE));
    }

    /**
     * This test is more advanced than {@link #testLockAcquiredOnInsert()}
     * @throws TransactionAbortedException
     */
    @Test
    public void testEarlyLockReleaseOnFullPages() throws TransactionAbortedException {

        // shouldn't have any locks acquired
        checkNoLocksAcquired();

        insertTuple();

        // should not have lock on pid0 because this page is full and so it should have done an
        // early lock release on this page (see lab implementation details)
        assertFalse(am.holdsLock(tid0, pid0, Permissions.READ_ONLY));
        // should have acquired lock on pid1 (and not yet released it because strict 2PL)
        assertTrue(am.holdsLock(tid0, pid1, Permissions.READ_WRITE));
    }

    /**
     * This test is more advanced than {@link #testEarlyLockReleaseOnFullPages()}
     * @throws TransactionAbortedException
     */
    @Test
    public void testOnlyReleaseIfJustAcquired() throws TransactionAbortedException {

        // run iterator to acquire readonly lock on all pages
        DbFileIterator iterator = hf.iterator(tid0);
        iterator.open();
        while (iterator.hasNext()) {
            iterator.next();
        }
        iterator.close();

        // check that both pages are locked
        assertTrue(am.holdsLock(tid0, pid0, Permissions.READ_ONLY));
        assertTrue(am.holdsLock(tid0, pid1, Permissions.READ_ONLY));

        // now do insert:
        insertTuple();

        // subtle point: it should STILL have lock on pid0 because it had lock prior to calling insert
        assertTrue(am.holdsLock(tid0, pid0, Permissions.READ_ONLY));
        // should have acquired lock upgrade on pid1 (and not yet released it because strict 2PL)
        assertTrue(am.holdsLock(tid0, pid1, Permissions.READ_WRITE));
    }

    private void insertTuple() throws TransactionAbortedException {
        // should insert onto pid1
        Tuple t = TestUtility.getIntTuple(new int[]{-1, -1});
        hf.insertTuple(tid0, t);
        assertEquals(1, t.getRecordId().getPageId().pageNumber()); // tuple inserted on pid1
    }

    private void checkNoLocksAcquired() {
        assertFalse(am.holdsLock(tid0, pid0, Permissions.READ_ONLY));
        assertFalse(am.holdsLock(tid0, pid1, Permissions.READ_ONLY));
    }

}
