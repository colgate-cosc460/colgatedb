package colgatedb.operators;

import colgatedb.DbException;
import colgatedb.TestUtility;
import colgatedb.page.PageTestUtility;
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
 * The contents of this file are taken almost verbatim from the SimpleDB project.
 * We are grateful for Sam's permission to use and adapt his materials.
 */
public class JoinTest {

    int width1 = 2;
    int width2 = 3;
    DbIterator scan1;
    DbIterator scan2;
    DbIterator eqJoin;

    /**
     * Initialize each unit test
     */
    @Before public void createTupleLists() throws Exception {
        this.scan1 = OperatorTestUtility.createTupleList(width1,
                new int[]{1, 2,
                        3, 4,
                        5, 6,
                        7, 8});
        this.scan2 = OperatorTestUtility.createTupleList(width2,
                new int[]{1, 2, 3,
                        2, 3, 4,
                        3, 4, 5,
                        4, 5, 6,
                        5, 6, 7});
        this.eqJoin = OperatorTestUtility.createTupleList(width1 + width2,
                new int[]{1, 2, 1, 2, 3,
                        3, 4, 3, 4, 5,
                        5, 6, 5, 6, 7});
    }

    @Test
    public void getJoinPredicate() {
        JoinPredicate pred = new JoinPredicate(0, Op.EQUALS, 0);
        Join op = new Join(pred, scan1, scan2);
        assertEquals(pred, op.getJoinPredicate());
    }

    @Test
    public void getTupleDesc() {
        JoinPredicate pred = new JoinPredicate(0, Op.EQUALS, 0);
        Join op = new Join(pred, scan1, scan2);
        TupleDesc expected = TestUtility.getTupleDesc(width1 + width2);
        TupleDesc actual = op.getTupleDesc();
        assertEquals(expected, actual);
    }

    @Test
    public void setChildrenIncorrectly() {
        JoinPredicate pred = new JoinPredicate(0, Op.EQUALS, 0);
        Join op = new Join(pred, scan1, scan2);
        DbIterator[] children = {scan1};
        try {
            op.setChildren(children);
            fail("should have raised an exception!");
        } catch (DbException e) {
            // expected
        }
    }


    /**
     * Unit test that uses an = predicate.  Joins scan1 and scan2 based on
     * the first attribute in each relation.  Each tuple in scan1 has at most
     * one match in scan2 (and vice versa).  Some tuples have zero matches.
     */
    @Test
    public void eqJoin() throws Exception {
        JoinPredicate pred = new JoinPredicate(0, Op.EQUALS, 0);
        Join op = new Join(pred, scan1, scan2);
        op.open();
        eqJoin.open();
        OperatorTestUtility.matchAllTuples(eqJoin, op);
    }

    @Test
    public void rewind() throws Exception {
        JoinPredicate pred = new JoinPredicate(0, Op.EQUALS, 0);
        Join op = new Join(pred, scan1, scan2);
        op.open();
        while (op.hasNext()) {
            assertNotNull(op.next());
        }
        assertTrue(OperatorTestUtility.checkExhausted(op));
        op.rewind();

        eqJoin.open();
        Tuple expected = eqJoin.next();
        Tuple actual = op.next();
        assertTrue(PageTestUtility.compareTuples(expected, actual));
    }
}

