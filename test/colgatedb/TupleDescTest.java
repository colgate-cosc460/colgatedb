package colgatedb;

import colgatedb.tuple.TupleDesc;
import colgatedb.tuple.Type;
import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

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

public class TupleDescTest {

    @Test
    public void constructorNamed() {
        TupleDesc td = TestUtility.getTupleDesc(2, "myFieldName");
        assertEquals(Type.INT_TYPE, td.getFieldType(0));
        assertEquals(Type.INT_TYPE, td.getFieldType(1));
        assertEquals("myFieldName0", td.getFieldName(0));
        assertEquals("myFieldName1", td.getFieldName(1));
    }

    @Test
    public void constructorUnNamed() {
        TupleDesc td = TestUtility.getTupleDesc(1);
        assertEquals(Type.INT_TYPE, td.getFieldType(0));
        assertEquals("", td.getFieldName(0));
    }

    @Test
    public void numFields() {
        int[] lengths = new int[]{1, 2, 1000};

        for (int len : lengths) {
            TupleDesc td = TestUtility.getTupleDesc(len);
            assertEquals(len, td.numFields());
        }
    }

    @Test
    public void getFieldType() {
        int[] lengths = new int[]{1, 2, 1000};

        for (int len : lengths) {
            TupleDesc td = TestUtility.getTupleDesc(len);
            for (int i = 0; i < len; ++i)
                assertEquals(Type.INT_TYPE, td.getFieldType(i));
        }

        TupleDesc td = TestUtility.getTupleDesc(10);
        try {
            td.getFieldType(11);
            fail("Invalid index.  Should throw NoSuchElementException.");
        } catch (IndexOutOfBoundsException e) {
            fail("Threw IndexOutOfBoundsException on invalid index.  Should throw NoSuchElementException instead.");
        } catch (NoSuchElementException e) {
            // expected to get here
        } catch (Exception e) {
            fail("Threw some other kind of exception.  Should throw NoSuchElementException.");
        }
    }

    @Test
    public void getFieldName() {
        TupleDesc td = TestUtility.getTupleDesc(3, "td");
        assertEquals("td0", td.getFieldName(0));
        assertEquals("td2", td.getFieldName(2));
        try {
            td.getFieldName(3);
            fail("Invalid index.  Should throw NoSuchElementException.");
        } catch (IndexOutOfBoundsException e) {
            fail("Threw IndexOutOfBoundsException on invalid index.  Should throw NoSuchElementException instead.");
        } catch (NoSuchElementException e) {
            // expected to get here
        } catch (Exception e) {
            fail("Threw some other kind of exception.  Should throw NoSuchElementException.");
        }
    }


    @Test
    public void fieldNameToIndex() {
        int[] lengths = new int[]{1, 2, 1000};
        String prefix = "test";

        for (int len : lengths) {
            // Make sure you retrieve well-named fields
            TupleDesc td = TestUtility.getTupleDesc(len, prefix);
            for (int i = 0; i < len; ++i) {
                assertEquals(i, td.fieldNameToIndex(prefix + i));
            }

            // Make sure you throw exception for non-existent fields
            try {
                td.fieldNameToIndex("foo");
                fail("foo is not a valid field name");
            } catch (NoSuchElementException e) {
                // expected to get here
            } catch (Exception e) {
                fail("Threw some other kind of exception.  Should throw NoSuchElementException.");
            }

            // Make sure you throw exception for null searches
            try {
                td.fieldNameToIndex(null);
                fail("null is not a valid field name");
            } catch (NoSuchElementException e) {
                // expected to get here
            } catch (Exception e) {
                fail("Threw some other kind of exception.  Should throw NoSuchElementException.");
            }

            // Make sure you throw exception when all field names are null
            td = new TupleDesc(new Type[]{Type.INT_TYPE, Type.INT_TYPE}, new String[2]);
            try {
                td.fieldNameToIndex(prefix);
                fail("no fields are named, so you can't find it");
            } catch (NoSuchElementException e) {
                // expected to get here
            }
        }
    }


    /**
     * Unit test for TupleDesc.getSize()
     */
    @Test
    public void getSize() {
        TupleDesc td;

        // create a tupledesc with string types
        td = new TupleDesc(new Type[] {
                Type.STRING_TYPE, Type.STRING_TYPE,
                Type.STRING_TYPE, Type.STRING_TYPE});
        assertEquals(4 * Type.STRING_TYPE.getLen(), td.getSize());

        int[] lengths = new int[]{1, 2, 1000};

        for (int len : lengths) {
            td = TestUtility.getTupleDesc(len);
            assertEquals(len * Type.INT_TYPE.getLen(), td.getSize());
        }

        // create a tupledesc of mixed type
        td = new TupleDesc(new Type[] { Type.INT_TYPE, Type.STRING_TYPE, Type.INT_TYPE});
        assertEquals(2 * Type.INT_TYPE.getLen() + Type.STRING_TYPE.getLen(), td.getSize());
    }


    @Test
    public void iterator() {
        TupleDesc td = new TupleDesc(new Type[]{Type.INT_TYPE, Type.STRING_TYPE}, new String[]{"fieldA", "fieldB"});
        Iterator<TupleDesc.TDItem> tdItemIterator = td.iterator();
        assertTrue(tdItemIterator.hasNext());
        TupleDesc.TDItem next = tdItemIterator.next();
        assertEquals(next.fieldType, Type.INT_TYPE);
        assertEquals(next.fieldName, "fieldA");

        assertTrue(tdItemIterator.hasNext());
        next = tdItemIterator.next();
        assertEquals(next.fieldType, Type.STRING_TYPE);
        assertEquals(next.fieldName, "fieldB");

        assertFalse(tdItemIterator.hasNext());
    }

    @Test
    public void equals() {
        TupleDesc singleInt = new TupleDesc(new Type[]{Type.INT_TYPE}, new String[]{"field0"});
        TupleDesc singleInt2 = new TupleDesc(new Type[]{Type.INT_TYPE}, new String[]{"firstField"});
        TupleDesc singleString = new TupleDesc(new Type[]{Type.STRING_TYPE});
        TupleDesc twoInts = new TupleDesc(new Type[]{Type.INT_TYPE, Type.INT_TYPE}, new String[]{"firstField", "secondField"});

        // .equals() with null should return false
        assertFalse(singleInt.equals(null));

        // .equals() with the wrong type should return false
        assertFalse(singleInt.equals(new Object()));

        assertTrue(singleInt.equals(singleInt));
        assertTrue(singleInt.equals(singleInt2));       // can differ on field name
        assertTrue(singleInt2.equals(singleInt));
        assertTrue(singleString.equals(singleString));

        assertFalse(singleInt.equals(singleString));
        assertFalse(singleInt2.equals(singleString));
        assertFalse(singleString.equals(singleInt));
        assertFalse(singleString.equals(singleInt2));

        assertFalse(singleInt.equals(twoInts));
        assertFalse(singleInt2.equals(twoInts));
        assertFalse(singleString.equals(twoInts));
        assertFalse(twoInts.equals(singleInt));
        assertFalse(twoInts.equals(singleInt2));
        assertFalse(twoInts.equals(singleString));
    }

    @Test
    public void testToString() {
        TupleDesc td = new TupleDesc(new Type[]{Type.INT_TYPE, Type.STRING_TYPE}, new String[]{"fieldA", "fieldB"});
        assertEquals("fieldA(INT_TYPE), fieldB(STRING_TYPE)", td.toString());
    }

    @Test
    public void merge() {
        TupleDesc td1, td2, td3;

        td1 = TestUtility.getTupleDesc(1, "td1");
        td2 = TestUtility.getTupleDesc(2, "td2");

        // test td1.merge(td2)
        td3 = TupleDesc.merge(td1, td2);
        assertEquals(3, td3.numFields());
        assertEquals(3 * Type.INT_TYPE.getLen(), td3.getSize());
        for (int i = 0; i < 3; ++i)
            assertEquals(Type.INT_TYPE, td3.getFieldType(i));
        assertEquals(combinedStringArrays(td1, td2, td3), true);

        // test td2.merge(td1)
        td3 = TupleDesc.merge(td2, td1);
        assertEquals(3, td3.numFields());
        assertEquals(3 * Type.INT_TYPE.getLen(), td3.getSize());
        for (int i = 0; i < 3; ++i)
            assertEquals(Type.INT_TYPE, td3.getFieldType(i));
        assertEquals(combinedStringArrays(td2, td1, td3), true);

        // test td2.merge(td2)
        td3 = TupleDesc.merge(td2, td2);
        assertEquals(4, td3.numFields());
        assertEquals(4 * Type.INT_TYPE.getLen(), td3.getSize());
        for (int i = 0; i < 4; ++i)
            assertEquals(Type.INT_TYPE, td3.getFieldType(i));
        assertEquals(combinedStringArrays(td2, td2, td3), true);
    }

    /**
     * Ensures that merged tupledesc's field names = td1's field names + td2's field names
     */
    private boolean combinedStringArrays(TupleDesc td1, TupleDesc td2, TupleDesc combined) {
        for (int i = 0; i < td1.numFields(); i++) {
            if (!(((td1.getFieldName(i) == null) && (combined.getFieldName(i) == null)) ||
                    td1.getFieldName(i).equals(combined.getFieldName(i)))) {
                return false;
            }
        }

        for (int i = td1.numFields(); i < td1.numFields() + td2.numFields(); i++) {
            if (!(((td2.getFieldName(i - td1.numFields()) == null) && (combined.getFieldName(i) == null)) ||
                    td2.getFieldName(i - td1.numFields()).equals(combined.getFieldName(i)))) {
                return false;
            }
        }

        return true;
    }




}

