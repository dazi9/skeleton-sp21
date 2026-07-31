package deque;

import java.util.Comparator;

public class MaxArrayDeque <T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.comparator = c;
    }

    public T max() {
        T max = null;
        if (this.size() != 0) {
            max = this.get(0);
            for (int i = 0; i < this.size(); i++) {
                if (comparator.compare(max, this.get(i)) > 0) {
                    max = this.get(i);
                }
            }
        }
        return max;
    }

    public T max(Comparator<T> c) {
        this.comparator = c;
        return max();
    }

}
