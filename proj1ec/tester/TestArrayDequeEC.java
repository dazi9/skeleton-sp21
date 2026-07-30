package tester;

import static org.junit.Assert.*;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {

    @Test
    public void test {
        int m = 10000;
        StudentArrayDeque<Integer> s = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> a = new ArrayDequeSolution<>();
        for (int i = 0; i < m; i++) {
            s.addFirst(i);
            a.addfirst(i);
        }
    }
}
