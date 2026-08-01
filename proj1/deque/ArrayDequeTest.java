package deque;

import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void easyResizeTest() {
        ArrayDeque<Integer> a = new ArrayDeque<Integer>();
        int m = 50000;
        for (int i = 0; i < m; i++) {
            a.addLast(i);
            assertEquals("should return the i-th item", (long) i, (long) a.get(i));
        }
    }

    @Test
    public void midResizeTest() {
        ArrayDeque<Integer> a = new ArrayDeque<Integer>();
        int m = 50000;
        for (int i = 0; i < m; i++) {
            a.addLast(i);
        }
        for (int i = 0; i < m; i++) {
            assertEquals("should return the i-th item", (long) m - i - 1, (long) a.removeLast());
        }
    }

    @Test
    public void hardResizeTest() {
        ArrayDeque<Integer> a = new ArrayDeque<Integer>();
        int m = 50000;
        for (int i = 0; i < m; i++) {
            a.addLast(i);
        }
        int n = 45000;
        for (int i = 0; i < n; i++) {
            a.removeLast();
        }
        assertEquals("size should be 5000", (long) m - n, (long) a.size());
        for (int i = 0; i < m - n; i++) {
            assertEquals("should return the i-th item", (long) i, (long) a.get(i));
        }
    }
}
