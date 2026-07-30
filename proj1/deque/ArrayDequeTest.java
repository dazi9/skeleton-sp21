package deque;

import org.junit.Test;
import static org.junit.Assert.*;


/** Performs some basic array deque tests. */
public class ArrayDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     *  finally printing the results. */
    public void addIsEmptySizeTest() {

        ArrayDeque<String> ad1 = new ArrayDeque<String>();

        assertTrue("A newly initialized ArrayDeque should be empty", ad1.isEmpty());
        ad1.addFirst("front");

        assertEquals(1, ad1.size());
        assertFalse("ad1 should now contain 1 item", ad1.isEmpty());

        ad1.addLast("middle");
        assertEquals(2, ad1.size());

        ad1.addLast("back");
        assertEquals(3, ad1.size());

        System.out.println("Printing out deque: ");
        ad1.printDeque();
    }

    @Test
    /** Adds an item, then removes an item, and ensures that deque is empty afterwards. */
    public void addRemoveTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        // should be empty
        assertTrue("ad1 should be empty upon initialization", ad1.isEmpty());

        ad1.addFirst(10);
        // should not be empty
        assertFalse("ad1 should contain 1 item", ad1.isEmpty());

        ad1.removeFirst();
        // should be empty
        assertTrue("ad1 should be empty after removal", ad1.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(3);

        ad1.removeLast();
        ad1.removeFirst();
        ad1.removeLast();
        ad1.removeFirst();

        int size = ad1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }



    @Test
    /* check if null is returned when removing from an empty ArrayDeque. */
    public void emptyNullReturnTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();

        assertEquals("Should return null when removeFirst is called on an empty Deque,",
                null, ad1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,",
                null, ad1.removeLast());
    }



    // ==================== ArrayDeque 专项测试 ====================

    @Test
    /* Test get(index) with addLast only */
    public void getTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addLast(0);
        ad1.addLast(1);
        ad1.addLast(2);
        ad1.addLast(3);

        assertEquals("get(0) should return first element", Integer.valueOf(0), ad1.get(0));
        assertEquals("get(1) should return second element", Integer.valueOf(1), ad1.get(1));
        assertEquals("get(2) should return third element", Integer.valueOf(2), ad1.get(2));
        assertEquals("get(3) should return fourth element", Integer.valueOf(3), ad1.get(3));
    }

    @Test
    /* Test get(index) with addFirst only */
    public void getAddFirstTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(0);
        ad1.addFirst(1);
        ad1.addFirst(2);
        ad1.addFirst(3);

        // addFirst 依次插入: 3, 2, 1, 0 — 顺序应为 [3, 2, 1, 0]
        assertEquals("get(0) should return first element", Integer.valueOf(3), ad1.get(0));
        assertEquals("get(1) should return second element", Integer.valueOf(2), ad1.get(1));
        assertEquals("get(2) should return third element", Integer.valueOf(1), ad1.get(2));
        assertEquals("get(3) should return fourth element", Integer.valueOf(0), ad1.get(3));
    }

    @Test
    /* Test interleaved addFirst / addLast */
    public void interleavedAddTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(1);          // [1]
        ad1.addLast(2);           // [1, 2]
        ad1.addFirst(0);          // [0, 1, 2]
        ad1.addLast(3);           // [0, 1, 2, 3]

        assertEquals("size should be 4", 4, ad1.size());
        assertEquals("get(0) should be 0", Integer.valueOf(0), ad1.get(0));
        assertEquals("get(1) should be 1", Integer.valueOf(1), ad1.get(1));
        assertEquals("get(2) should be 2", Integer.valueOf(2), ad1.get(2));
        assertEquals("get(3) should be 3", Integer.valueOf(3), ad1.get(3));
    }

    @Test
    /* Test removeFirst returns elements in FIFO order */
    public void removeFirstOrderTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addLast(1);
        ad1.addLast(2);
        ad1.addLast(3);

        assertEquals("First removeFirst should return 1", Integer.valueOf(1), ad1.removeFirst());
        assertEquals("Second removeFirst should return 2", Integer.valueOf(2), ad1.removeFirst());
        assertEquals("Third removeFirst should return 3", Integer.valueOf(3), ad1.removeFirst());
        assertTrue("Deque should be empty", ad1.isEmpty());
    }

    @Test
    /* Test removeLast returns elements in LIFO order */
    public void removeLastOrderTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(1);
        ad1.addFirst(2);
        ad1.addFirst(3);

        assertEquals("First removeLast should return 1", Integer.valueOf(1), ad1.removeLast());
        assertEquals("Second removeLast should return 2", Integer.valueOf(2), ad1.removeLast());
        assertEquals("Third removeLast should return 3", Integer.valueOf(3), ad1.removeLast());
        assertTrue("Deque should be empty", ad1.isEmpty());
    }



    @Test
    /* Test add/remove alternating pattern */
    public void alternatingAddRemoveTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addLast(1);
        ad1.addLast(2);
        assertEquals(Integer.valueOf(1), ad1.removeFirst());
        ad1.addLast(3);
        assertEquals(Integer.valueOf(2), ad1.removeFirst());
        ad1.addLast(4);
        assertEquals(Integer.valueOf(3), ad1.removeFirst());
        assertEquals(Integer.valueOf(4), ad1.removeFirst());
        assertTrue(ad1.isEmpty());
    }

    @Test
    /* Test size after mixed add/remove operations */
    public void sizeAfterMixedOpsTest() {

        ArrayDeque<String> ad1 = new ArrayDeque<>();
        ad1.addFirst("a");
        ad1.addLast("b");
        ad1.addFirst("c");
        assertEquals(3, ad1.size());

        ad1.removeFirst();
        assertEquals(2, ad1.size());

        ad1.removeLast();
        assertEquals(1, ad1.size());

        ad1.removeFirst();
        assertEquals(0, ad1.size());
        assertTrue(ad1.isEmpty());
    }

    @Test
    public void bigArrayDequeTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }


    }

}
