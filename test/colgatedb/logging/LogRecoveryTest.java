package colgatedb.logging;

import colgatedb.page.Page;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
public class LogRecoveryTest extends LogTestUtility {

    /**
     * Transaction modifies a page, commits, then crash.  Recovery should redo change.
     * @throws IOException
     */
    @Test
    public void testRedo() throws IOException {

        MockPage before = new MockPage(pid0, 1);
        MockPage after = new MockPage(pid0, 2);

        // Write log records
        lf.logXactionBegin(tid0);
        lf.logWrite(tid0, before, after);
        dm.writePage(after);
        lf.logCommit(tid0);

        // now go in and mess with page0 and set to something else...
        dm.writePage(new MockPage(pid0, -1));

        // crash!
        crash();

        // Check that pid0 has been redone
        assertEquals(after, (MockPage) dm.readPage(pid0, pm));
    }


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

        // crash!
        crash();

        // Check that pid0 has been undone (the page on disk should be equal to before)
        assertEquals(before, (MockPage) dm.readPage(pid0, pm));

        // Check abort record written to log for this transaction
        assertTrue(lf.abortedTxns.contains(tid0.getId()));

        // Check that a compensating log record was written
        List<Page> pages = lf.getCLRs(tid0);
        assertTrue(pages.contains(before));
    }


    /**
     * Two transactions write pages to disk, one commits and one never commits.  Make sure
     * recovery only undoes modifications by the loser transaction.
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

        crash();

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

        crash();

        // Check that p0 was rolled back all the to initial value p0Before
        MockPage pageOnDisk = (MockPage) dm.readPage(pid0, pm);
        assertEquals(p0Before, pageOnDisk);
    }


    /**
     * Test that recovery skips over CLRs
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

        crash();

        // Check that pid0 has been undone (the page on disk should be equal to before)
        MockPage pageOnDisk = (MockPage) dm.readPage(pid0, pm);
        assertEquals(before, pageOnDisk);
    }


    /**
     * Test that recovery starts at checkpoint
     * @throws IOException
     */
    @Test
    public void testStartAtCheckpoint() throws IOException {

        MockPage before = new MockPage(pid0, 1);
        MockPage after = new MockPage(pid0, 2);

        // Write log records
        lf.logXactionBegin(tid0);
        lf.logWrite(tid0, before, after);
        dm.writePage(after);
        lf.logCheckpoint();
        lf.logCommit(tid0);

        // go in and mess around with page
        MockPage hackedPage = new MockPage(pid0, -2);
        dm.writePage(hackedPage);

        crash();

        // Check that pid0 was not re-written (b/c recovery should start at checkpoint)
        assertEquals(hackedPage, dm.readPage(pid0, pm));
    }


    /**
     * Test that recovery starts at checkpoint but undo phase can go past checkpoint if necessary
     * @throws IOException
     */
    @Test
    public void testUndoPastCheckpoint() throws IOException {

        MockPage before = new MockPage(pid0, 1);
        MockPage afterT0 = new MockPage(pid0, 2);
        MockPage afterT1 = new MockPage(pid0, 3);

        // Write log records
        lf.logXactionBegin(tid0);
        lf.logXactionBegin(tid1);
        lf.logWrite(tid0, before, afterT0);
        dm.writePage(afterT0);
        lf.logWrite(tid1, afterT0, afterT1);
        dm.writePage(afterT0);
        lf.logCheckpoint();
        lf.logCommit(tid0);
        // tid1 never commits: it should be a loser

        // go in and mess around with page: this should get undone during recovery
        MockPage hackedPage = new MockPage(pid0, -2);
        dm.writePage(hackedPage);

        crash();

        // Check that pid0 was re-written (b/c recovery should undo tid1's modifications)
        assertEquals(afterT0, dm.readPage(pid0, pm));
    }
}
