package deque;

import antlr.Utils;

public class ArrayDeque<T> {
    private T[] items;
    private int size;
    private int front;
    private int back;

    public ArrayDeque() {
        size = 0;
        items = (T[]) new Object[8];
        front = 0;
        back = items.length - 1;
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
        items[front] = item;
        front--;
        front = (front + size) % size;
    }

    public void addLast(T item) {
        size++;
        if (size > items.length) {
            resize(2 * size);
        }
        items[back] = item;
        back++;
        back = (size - back) % size;
    }

    public T removeFirst() {
        T temp = items[front];
        items[front] = null;
        front++;
        front = (front + size) % size;
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
        back = (size - back) % size;
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
            front = (front + size) % size;
            System.out.print(" ");
        }
    }

    public T get(int index) {
        return items[(front + index) % size];
    }
}
