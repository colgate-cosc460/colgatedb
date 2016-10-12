package colgatedb;


import colgatedb.page.Page;
import colgatedb.page.PageId;
import colgatedb.page.PageMaker;
import colgatedb.page.SimplePageId;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
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

public class BufferManagerMoreTest {
    private MockDiskManager dm;
    private PageMaker pm;
    private BufferManager buffMgr;
    private int tableid = 0;
    private PageId pid0 = new SimplePageId(tableid, 0);
    private PageId pid1 = new SimplePageId(tableid, 1);
    private PageId pid2 = new SimplePageId(tableid, 2);
    private PageId pid3 = new SimplePageId(tableid, 3);  // this one is not allocated initially

    @Before
    public void setUp() throws IOException {
        dm = new MockDiskManager();
        pm = dm;
        for (int i = 0; i < 3; i++) {
            SimplePageId pid = new SimplePageId(tableid, i);
            dm.allocatePage(pid);
            dm.setDatum(pid, pid.pageNumber());
        }
    }

    private void initializeBufferManager(int numPages) {
        buffMgr = new BufferManagerImpl(numPages, dm);
        buffMgr.evictDirty(true);
    }

    @Test
    public void evictPageMultiplePages() {
        initializeBufferManager(2);
        buffMgr.pinPage(pid0, pm);
        buffMgr.pinPage(pid1, pm);
        buffMgr.unpinPage(pid0, false);
        buffMgr.unpinPage(pid1, false);

        // pin a third page, should trigger eviction of first or second
        buffMgr.pinPage(pid2, pm);

        // if page was evicted, then value on disk would change
        assertTrue(!buffMgr.inBufferPool(pid0) || !buffMgr.inBufferPool(pid1));
    }

    /**
     * MockDiskManager is a fake disk manager used for testing purposes.
     *
     * Testers can inspect the disk manager to see how many times a
     * particular page was read or written.
     *
     */
    public class MockDiskManager implements DiskManager, PageMaker {

        // keep track of reads and writes (and allocations?)
        List<PageContainer> pages = new ArrayList<>();

        @Override
        public void allocatePage(PageId pid) {
            assertEquals(tableid, pid.getTableId());
            assertEquals(pages.size(), pid.pageNumber());
            pages.add(new PageContainer());
        }

        @Override
        public Page readPage(PageId pid, PageMaker pageMaker) {
            assertEquals(this, pageMaker);
            PageContainer container = getPageContainer(pid);
            container.reads++;
            return new MockPage(pid, container.pageDatum);
        }

        @Override
        public void writePage(Page page) {
            PageId pid = page.getId();
            PageContainer container = getPageContainer(pid);
            container.writes++;
            container.pageDatum = ((MockPage)page).datum;
        }

        public PageContainer getPageContainer(PageId pid) {
            assertTrue(0 <= pid.pageNumber() && pid.pageNumber() < pages.size());
            return pages.get(pid.pageNumber());
        }

        /**
         * @return number of times page with pid was read
         */
        public int getReadCount(PageId pid) {
            return getPageContainer(pid).reads;
        }

        /**
         * @return number of times page with pid was written
         */
        public int getWriteCount(PageId pid) {
            return getPageContainer(pid).writes;
        }

        /**
         * @return number of times page with pid was written
         */
        public int getDatum(PageId pid) {
            return getPageContainer(pid).pageDatum;
        }

        /**
         * This disk manager does not use the PageMaker because it reads/writes MockPages.
         * @param pid
         * @param bytes
         * @return
         */
        @Override
        public Page makePage(PageId pid, byte[] bytes) {
            throw new UnsupportedOperationException();
        }

        /**
         * This disk manager does not use the PageMaker because it reads/writes MockPages.
         * @param pid
         * @return
         */
        @Override
        public Page makePage(PageId pid) {
            throw new UnsupportedOperationException();
        }

        public MockPage getPage(PageId pid) {
            return new MockPage(pid, getPageContainer(pid).pageDatum);
        }

        public void setPage(Page page) {
            PageId pid = page.getId();
            PageContainer container = getPageContainer(pid);
            container.pageDatum = ((MockPage)page).datum;
        }

        public void setDatum(PageId pid, int datum) {
            PageContainer container = getPageContainer(pid);
            container.pageDatum = datum;
        }

        private class PageContainer {
            int pageDatum;
            int reads;
            int writes;
            public PageContainer() {
                this.pageDatum = -1;
                reads = 0;
                writes = 0;
            }
        }
    }

    public class MockPage implements Page {

        private final PageId pid;
        int datum;

        public MockPage(PageId pid, int datum) {
            this.pid = pid;
            this.datum = datum;
        }

        @Override
        public boolean equals(Object other) {
            return (other instanceof MockPage) &&
                    ((((MockPage)other).datum == datum) &&
                            ((MockPage)other).getId().equals(this.getId()));
        }

        @Override
        public PageId getId() {
            return pid;
        }

        @Override
        public byte[] getPageData() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Page getBeforeImage() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setBeforeImage() {
            throw new UnsupportedOperationException();
        }
    }
}
