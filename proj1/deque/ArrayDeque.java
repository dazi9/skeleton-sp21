package deque;

public class ArrayDeque<T> {
    private T[] items;
    private int size;
    private int front;
    private int back;

    public ArrayDeque() {
        size = 0;
        items = (T[]) new Object[8];
        front = 1;
        back = 0;
    }

    private void resize(int length) {
        T[] a = (T[]) new Object[length];
        System.arraycopy(items, 0, a, 0, length);
        items = a;
    }

    public void addFirst(T item) {
        size++;
        if (size > items.length) {
            resize(2 * size);
        }
        front--;
        front = (front + items.length) % items.length;
        items[front] = item;
    }

    public void addLast(T item) {
        size++;
        if (size > items.length) {
            resize(2 * size);
        }
        back++;
        back = (items.length + back) % items.length;
        items[back] = item;
    }

    public T removeFirst() {
        T temp = items[front];
        items[front] = null;
        front++;
        front = (front + items.length) % items.length;
        if (size > 0) { size--; }
        if (size < items.length/4 && size > 8) {
            resize(items.length/4);
        }
        return temp;
    }

    public T removeLast() {
        T temp = items[back];
        items[back] = null;
        back--;
        back = (items.length + back) % items.length;
        if (size > 0) { size--; }
        if (size < items.length/4 && size > 8) {
            resize(items.length/4);
        }
        return temp;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(get(front));
            front++;
            front = (front + items.length) % items.length;
            System.out.print(" ");
        }
    }

    public T get(int index) {
        return items[(front + index) % items.length];
    }
}
