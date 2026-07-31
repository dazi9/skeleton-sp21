package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
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
        System.arraycopy(items, 0, a, 0, (back + 1) % items.length);
        System.arraycopy(items, front, a, front + items.length, items.length - front);
        front = front + items.length;
        items = a;
    }

    @Override
    public void addFirst(T item) {
        size++;
        if (size > items.length) {
            resize(2 * items.length);
        }
        front--;
        front = (front + items.length) % items.length;
        items[front] = item;
    }

    @Override
    public void addLast(T item) {
        size++;
        if (size > items.length) {
            resize(2 * items.length);
        }
        back++;
        back = (items.length + back) % items.length;
        items[back] = item;
    }


    @Override
    public T removeFirst() {
        T temp = items[front];
        items[front] = null;
        front++;
        front = (front + items.length) % items.length;
        if (size > 0) {
            size--;
        }
        if (size < items.length / 4 && size > 8) {
            resize(items.length / 4);
        }
        return temp;
    }

    @Override
    public T removeLast() {
        T temp = items[back];
        items[back] = null;
        back--;
        back = (items.length + back) % items.length;
        if (size > 0) {
            size--;
        }
        if (size < items.length / 4 && size > 8) {
            resize(items.length / 4);
        }
        return temp;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(get(front));
            front++;
            front = (front + items.length) % items.length;
            System.out.print(" ");
        }
    }

    @Override
    public T get(int index) {
        return items[(front + index) % items.length];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int pos;
        public ArrayDequeIterator() {
            pos = front;
        }

        public boolean hasNext() {
            return (pos != back);
        }

        public T next() {
            T returnItem = items[pos];
            pos++;
            pos = (pos + items.length) % items.length;
            return returnItem;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof ArrayDeque)) {
            return false;
        }
        ArrayDeque<T> other = (ArrayDeque<T>) o;
        if (other.size() != this.size()) { return false; }
        for (int i = 0; i < size; i++) {
            if (other.get(i) != this.get(i)) {
                return false;
            }
        }
        return true;
    }
}
