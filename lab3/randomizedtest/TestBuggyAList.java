package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE

    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> a = new AListNoResizing<>();
        BuggyAList<Integer> b = new BuggyAList<>();
        for (int i = 0; i < 3; i++) {
            a.addLast(i + 4);
            b.addLast(i + 4);
        }
        for (int i = 0; i < 3; i++) {
            assertEquals(a.removeLast(), b.removeLast());
        }
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                assertEquals(L.getLast(), B.getLast());
            } else if (operationNumber == 1 && L.size() > 0 && B.size() > 0) {
                // size
                int val1 = L.removeLast();
                int val2 = B.removeLast();
                assertEquals(val1, val2);
            } else if (operationNumber == 2) {
                // size
                int size1 = L.size();
                int size2 = B.size();
                assertEquals(size1, size2);
            }
        }
    }
}
