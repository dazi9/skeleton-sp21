package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer> Ns = new AList<Integer>();
        AList<Double> times = new AList<Double>();
        AList<Integer> opCounts = new AList<Integer>();
        for (int i = 1000; i <= 128000; i *= 2) {
            SLList<Integer> list = new SLList<Integer>();
            for (int n = 1; n <= i; n++) {
                list.addLast(n);
            }
            Stopwatch sw = new Stopwatch();
            int m = 10000;
            for (int j = 0; j < m; j++) {
                list.getLast();
            }
            times.addLast(sw.elapsedTime());
            Ns.addLast(i);
            opCounts.addLast(m);
        }
        TimeSLList.printTimingTable(Ns , times, opCounts);
    }

}
