package colgatedb.logging;

import colgatedb.*;
import colgatedb.page.Page;
import colgatedb.page.PageId;
import colgatedb.page.PageMaker;
import colgatedb.page.SimplePageId;
import colgatedb.transactions.TransactionId;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
public class LogTestUtility {

    private final int tableId = 0;
    protected SimplePageId pid0 = new SimplePageId(tableId, 0);
    protected SimplePageId pid1 = new SimplePageId(tableId, 1);
    protected DiskManagerImpl dm;
    protected MockPageMaker pm = new MockPageMaker();
    protected TransactionId tid0 = new TransactionId();
    protected TransactionId tid1 = new TransactionId();
    protected WrappedLogFileImpl lf;
    protected BufferManager bm;
    private File dbFile;
    private File logFile;

    @Before
    public void setUp() throws IOException {

        // set up the log file to be a wrapped log file
        logFile = File.createTempFile("testLog", ".dat");
        dbFile = File.createTempFile("testFile", ".dat");

        resetDatabase();

        dm.allocatePage(pid0);
        dm.allocatePage(pid1);
    }

    private void resetDatabase() throws IOException {
        this.lf = new WrappedLogFileImpl(logFile);

        // set up the disk manager: add an entry for my fake table
        dm = new DiskManagerImpl(MockPage.PAGESIZE);
        dm.addFileEntry(tableId, dbFile.getAbsolutePath());

        bm = new BufferManagerImpl(10, dm);
        AccessManagerImpl am = new AccessManagerImpl(bm);

        // update the global database state
        Database.setDiskManager(dm);
        Database.setBufferManager(bm);
        Database.setAccessManager(am);
        Database.setLogFile(this.lf);
    }

    /**
     * Destroys in-memory state of database and initiate recovery protocol
     * @throws IOException
     */
    protected void crash() throws IOException {
        resetDatabase();
        lf.recover();
    }



    /**
     * Simple Page object that just stores a single (byte-sized) integer
     */
    public static class MockPage implements Page {

        private final PageId pid;
        byte datum;
        byte beforeImage;
        public final static int PAGESIZE = 1;  // 1 byte

        public MockPage(PageId pid, int datum) {
            this(pid, new byte[]{(byte) datum});
        }

        public MockPage(PageId pid, byte[] pageData) {
            this.pid = pid;
            if (pageData.length != 1) {
                throw new RuntimeException("Invalid input!");
            }
            this.datum = pageData[0];
            beforeImage = datum;
        }

        @Override
        public boolean equals(Object other) {
            return (other instanceof MockPage) &&
                    ((((MockPage) other).datum == datum) &&
                            ((MockPage) other).getId().equals(this.getId()));
        }

        @Override
        public PageId getId() {
            return pid;
        }

        @Override
        public byte[] getPageData() {
            return new byte[]{datum};
        }

        @Override
        public Page getBeforeImage() {
            return new MockPage(pid, beforeImage);
        }

        @Override
        public void setBeforeImage() {
            beforeImage = datum;
        }

        public String toString() {
            return "MockPage(id=" + pid + ", datum=" + datum + ")";
        }
    }

    /**
     * Simple page maker for MockPage objects
     */
    public static class MockPageMaker implements PageMaker {
        @Override
        public Page makePage(PageId pid, byte[] bytes) {
            return new MockPage(pid, bytes);
        }

        @Override
        public Page makePage(PageId pid) {
            return new MockPage(pid, -1);
        }

    }

    /**
     * Extension of LogFile: no new functionality, just tracks calls for some methods.
     *
     * I should use a decorator pattern here rather than subclassing, but subclassing
     * is much more concise.
     */
    public class WrappedLogFileImpl extends LogFileImpl {
        List<Long> abortedTxns = new LinkedList<>();
        Map<Long, List<Page>> clrs = new HashMap<>();

        public WrappedLogFileImpl(File f) throws IOException {
            super(f);
        }

        public void logAbort(Long tid) throws IOException {
            super.logAbort(tid);
            abortedTxns.add(tid);
        }

        public void logCLR(TransactionId tid, Page after) throws IOException {
            super.logCLR(tid, after);
            recordCLR(after, tid.getId());
        }


        public void logCLR(Long tid, Page after) throws IOException {
            super.logCLR(tid, after);
            recordCLR(after, tid);
        }

        private void recordCLR(Page after, long tid) {
            clrs.putIfAbsent(tid, new LinkedList<>());
            clrs.get(tid).add(after);
        }

        public List<Page> getCLRs(TransactionId tid) {
            clrs.putIfAbsent(tid.getId(), new LinkedList<>());
            return clrs.get(tid.getId());
        }
    }

}
