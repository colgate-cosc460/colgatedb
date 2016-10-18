package colgatedb.operators;

import colgatedb.DbException;
import colgatedb.TestUtility;
import colgatedb.page.PageTestUtility;
import colgatedb.transactions.TransactionAbortedException;
import colgatedb.tuple.Op;
import colgatedb.tuple.Tuple;
import colgatedb.tuple.TupleDesc;
import org.junit.Before;
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
public class FilterTest {

    int testWidth = 3;
    DbIterator scan;

    @Before
    public void setUp() {
        this.scan = new OperatorTestUtility.MockScan(-5, 5, testWidth);
    }

    @Test
    public void getTupleDesc() {
        Predicate pred = new Predicate(0, Op.EQUALS, OperatorTestUtility.getField(0));
        Filter op = new Filter(pred, scan);
        TupleDesc expected = TestUtility.getTupleDesc(testWidth);
        TupleDesc actual = op.getTupleDesc();
        assertEquals(expected, actual);
    }

    @Test
    public void getPredicate() {
        Predicate pred = new Predicate(0, Op.EQUALS, OperatorTestUtility.getField(0));
        Filter op = new Filter(pred, scan);
        assertEquals(pred, op.getPredicate());
    }

    @Test
    public void getChildren() {
        Predicate pred = new Predicate(0, Op.EQUALS, OperatorTestUtility.getField(0));
        Filter op = new Filter(pred, scan);
        assertArrayEquals(new DbIterator[]{scan}, op.getChildren());
    }

    @Test
    public void setChildren() {
        Predicate pred = new Predicate(0, Op.EQUALS, OperatorTestUtility.getField(0));
        Filter op = new Filter(pred, scan);
        DbIterator[] children = {new OperatorTestUtility.MockScan(-5, 5, testWidth)};
        op.setChildren(children);
        assertArrayEquals(children, op.getChildren());
    }

    @Test
    public void setChildrenIncorrectly() {
        Predicate pred = new Predicate(0, Op.EQUALS, OperatorTestUtility.getField(0));
        Filter op = new Filter(pred, scan);
        DbIterator[] children = {new OperatorTestUtility.MockScan(-5, 5, testWidth), new OperatorTestUtility.MockScan(-5, 5, testWidth)};

        try {
            op.setChildren(children);
            fail("should have raised an exception!");
        } catch (DbException e) {
            // expected
        }
    }

    @Test
    public void notYetOpen() throws TransactionAbortedException {
        Predicate pred = new Predicate(0, Op.EQUALS, OperatorTestUtility.getField(0));
        Filter op = new Filter(pred, scan);
        assertFalse(op.hasNext());
    }

    @Test
    public void rewind() throws Exception {
        Predicate pred = new Predicate(0, Op.EQUALS, OperatorTestUtility.getField(0));
        Filter op = new Filter(pred, scan);
        op.open();
        assertTrue(op.hasNext());
        assertNotNull(op.next());
        assertTrue(OperatorTestUtility.checkExhausted(op));

        op.rewind();
        Tuple expected = TestUtility.getIntTuple(0, testWidth);
        Tuple actual = op.next();
        assertTrue(PageTestUtility.compareTuples(expected, actual));
        op.close();
    }

    /**
     * Unit test for Filter.getNext() using a &lt; predicate that filters
     * some tuples
     */
    @Test
    public void filterSomeLessThan() throws Exception {
        Predicate pred;
        pred = new Predicate(0, Op.LESS_THAN, OperatorTestUtility.getField(2));
        Filter op = new Filter(pred, scan);
        OperatorTestUtility.MockScan expectedOut = new OperatorTestUtility.MockScan(-5, 2, testWidth);
        op.open();
        OperatorTestUtility.compareDbIterators(op, expectedOut);
        op.close();
    }

    /**
     * Unit test for Filter.getNext() using a &lt; predicate that filters
     * everything
     */
    @Test
    public void filterAllLessThan() throws Exception {
        Predicate pred;
        pred = new Predicate(0, Op.LESS_THAN, OperatorTestUtility.getField(-5));
        Filter op = new Filter(pred, scan);
        op.open();
        assertTrue(OperatorTestUtility.checkExhausted(op));
        op.close();
    }

    /**
     * Unit test for Filter.getNext() using an = predicate
     */
    @Test
    public void filterEqual() throws Exception {
        Predicate pred;
        this.scan = new OperatorTestUtility.MockScan(-5, 5, testWidth);
        pred = new Predicate(0, Op.EQUALS, OperatorTestUtility.getField(-5));
        Filter op = new Filter(pred, scan);
        op.open();
        assertTrue(PageTestUtility.compareTuples(TestUtility.getIntTuple(-5, testWidth),
                op.next()));
        op.close();

        this.scan = new OperatorTestUtility.MockScan(-5, 5, testWidth);
        pred = new Predicate(0, Op.EQUALS, OperatorTestUtility.getField(0));
        op = new Filter(pred, scan);
        op.open();
        assertTrue(PageTestUtility.compareTuples(TestUtility.getIntTuple(0, testWidth),
                op.next()));
        op.close();

        this.scan = new OperatorTestUtility.MockScan(-5, 5, testWidth);
        pred = new Predicate(0, Op.EQUALS, OperatorTestUtility.getField(4));
        op = new Filter(pred, scan);
        op.open();
        assertTrue(PageTestUtility.compareTuples(TestUtility.getIntTuple(4, testWidth),
                op.next()));
        op.close();
    }

    /**
     * Unit test for Filter.getNext() using an = predicate passing no tuples
     */
    @Test
    public void filterEqualNoTuples() throws Exception {
        Predicate pred;
        pred = new Predicate(0, Op.EQUALS, OperatorTestUtility.getField(5));
        Filter op = new Filter(pred, scan);
        op.open();
        OperatorTestUtility.checkExhausted(op);
        op.close();
    }

}

