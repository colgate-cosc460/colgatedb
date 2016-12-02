package colgatedb.logging;

import colgatedb.page.Page;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

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
public class LogRollbackTest extends LogTestUtility {

    /**
     * Transaction modifies a page then aborts.  Modification should be undone and abort
     * message should be written to log.
     * @throws IOException
     */
    @Test
    public void testSimpleUndo() throws IOException {

        MockPage before = new MockPage(pid0, 1);
        MockPage after = new MockPage(pid0, 2);

        // Write log records
        lf.logXactionBegin(tid0);
        lf.logWrite(tid0, before, after);
        dm.writePage(after);

        // Abort tid0
        lf.logAbort(tid0);

        // Check that pid0 has been undone (the page on disk should be equal to before)
        MockPage pageOnDisk = (MockPage) dm.readPage(pid0, pm);
        assertEquals(before, pageOnDisk);

        // Check that rollback wrote an abort record to log for this transaction
        assertTrue(lf.abortedTxns.contains(tid0.getId()));

        // Check that a compensating log record was written
        List<Page> pages = lf.getCLRs(tid0);
        assertTrue(pages.contains(before));
    }

    /**
     * Two transactions write pages to disk, one commits and one aborts.  Make sure
     * rollback only undoes modifications by the aborting transaction.
     * @throws IOException
     */
    @Test
    public void testOnlyUndoAbortingTransaction() throws IOException {

        MockPage p0Before = new MockPage(pid0, 1);
        MockPage p0After = new MockPage(pid0, 2);
        MockPage p1Before = new MockPage(pid1, 1);
        MockPage p1After = new MockPage(pid1, 2);


        // Write log records
        lf.logXactionBegin(tid0);
        lf.logXactionBegin(tid1);
        lf.logWrite(tid0, p0Before, p0After);
        dm.writePage(p0After);
        lf.logWrite(tid1, p1Before, p1After);
        dm.writePage(p1After);
        lf.logCommit(tid1);

        // Abort tid0
        lf.logAbort(tid0);

        // Check that pid0 has been undone (the page on disk should be equal to before)
        MockPage pageOnDisk = (MockPage) dm.readPage(pid0, pm);
        assertEquals(p0Before, pageOnDisk);

        // Check that pid1 has NOT been undone (it was modified by T1 who committed)
        pageOnDisk = (MockPage) dm.readPage(pid1, pm);
        assertEquals(p1After, pageOnDisk);

        // Check that rollback wrote an abort record to log for this transaction
        assertTrue(this.lf.abortedTxns.contains(tid0.getId()));
    }


    /**
     * Transaction modifies more than one page and aborts.
     * @throws IOException
     */
    @Test
    public void testUndoMultiple() throws IOException {

        MockPage p0Before = new MockPage(pid0, 1);
        MockPage p0After = new MockPage(pid0, 2);
        MockPage p1Before = new MockPage(pid1, 10);
        MockPage p1After = new MockPage(pid1, 20);


        // Write log records
        lf.logXactionBegin(tid0);
        lf.logWrite(tid0, p0Before, p0After);
        dm.writePage(p0After);
        lf.logWrite(tid0, p1Before, p1After);
        dm.writePage(p1After);

        // Abort tid0
        lf.logAbort(tid0);

        // Check pages have been undone
        MockPage pageOnDisk = (MockPage) dm.readPage(pid0, pm);
        assertEquals(p0Before, pageOnDisk);
        pageOnDisk = (MockPage) dm.readPage(pid1, pm);
        assertEquals(p1Before, pageOnDisk);
    }

    /**
     * Transaction modifies the same object multiple times and then aborts.  Test that
     * modifications are done in the appropriate order leaving the page in its initial state.
     * @throws IOException
     */
    @Test
    public void testUndoOrder() throws IOException {

        MockPage p0Before = new MockPage(pid0, 1);
        MockPage p0After = new MockPage(pid0, 2);
        MockPage p0After2  = new MockPage(pid0, 3);
        MockPage p0After3 = new MockPage(pid0, 4);


        // Write log records
        lf.logXactionBegin(tid0);
        lf.logWrite(tid0, p0Before, p0After);
        dm.writePage(p0After);
        lf.logWrite(tid0, p0After, p0After2);
        dm.writePage(p0After2);
        lf.logWrite(tid0, p0After2, p0After3);
        dm.writePage(p0After3);

        // Abort tid0
        lf.logAbort(tid0);

        // Check that p0 was rolled back all the to initial value p0Before
        MockPage pageOnDisk = (MockPage) dm.readPage(pid0, pm);
        assertEquals(p0Before, pageOnDisk);
    }

    /**
     * Test that rollback skips over CLRs
     * @throws IOException
     */
    @Test
    public void testWithCLRs() throws IOException {

        MockPage before = new MockPage(pid0, 1);
        MockPage afterT0 = new MockPage(pid0, 2);
        MockPage afterT1 = new MockPage(pid0, 20);

        // Write log records
        lf.logXactionBegin(tid0);
        lf.logXactionBegin(tid1);
        lf.logWrite(tid0, before, afterT0);
        dm.writePage(afterT0);
        lf.logCLR(tid0, before);
        lf.logAbort(tid0.getId());   // tid0 aborted
        lf.logWrite(tid1, before, afterT1);
        dm.writePage(afterT1);

        // Abort tid0
        lf.logAbort(tid1);

        // Check that pid0 has been undone (the page on disk should be equal to before)
        MockPage pageOnDisk = (MockPage) dm.readPage(pid0, pm);
        assertEquals(before, pageOnDisk);
    }


    /**
     * Should throw exception when attempting to abort an already committed transaction.
     * @throws IOException
     */
    @Test
    public void testExceptionOnCommittedTransaction() throws IOException {

        // Write log records
        lf.logXactionBegin(tid0);
        lf.logCommit(tid0);

        try {
            // Abort tid0
            lf.logAbort(tid0);
            fail("Should raise an IOException");
        } catch (IOException e) {
            // expected
        }
    }

    /**
     * Transaction modifies a page then aborts.  Modified page should be discarded
     * from buffer pool.
     * @throws IOException
     */
    @Test
    public void testPageDiscardedFromBufferPool() throws IOException {

        MockPage before = new MockPage(pid0, 11);
        dm.writePage(before);

        // Start a transaction, allocate a page, modify it, and then abort
        lf.logXactionBegin(tid0);

        MockPage page = (MockPage) bm.pinPage(pid0, pm);
        page.datum = 12;
        lf.logWrite(tid0, page.getBeforeImage(), page);
        bm.unpinPage(pid0, true);

        // Abort tid0
        lf.logAbort(tid0);

        // Check that pid0 is no longer in buffer pool and that modified value not written to disk
        assertFalse(bm.inBufferPool(pid0));
        assertEquals(before, (MockPage) dm.readPage(pid0, pm));
    }


}
