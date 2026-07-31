package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        private T item;
        Node prev;
        Node next;
    }
    private Node sentinel = new Node();
    private int size;

    public LinkedListDeque() {
        size = 0;
        sentinel.item = null;
        sentinel.prev = sentinel;
        sentinel.next = sentinel;

    }

    @Override
    public void addFirst(T item) {
        Node temp = new Node();
        temp.prev = sentinel;
        temp.item = item;
        temp.next = sentinel.next;
        sentinel.next.prev = temp;
        sentinel.next = temp;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        sentinel.prev.next = new Node();
        sentinel.prev.next.prev = sentinel.prev;
        sentinel.prev.next.item = item;
        sentinel.prev.next.next = sentinel;
        sentinel.prev = sentinel.prev.next;
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node p = new Node();
        for (p = sentinel; p.next != sentinel; p = p.next) {
            System.out.print(p.item);
            System.out.print(" ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        T item = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        if (item != null) {
            size--;
        }
        return item;
    }

    @Override
    public T removeLast() {
        T item = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        if (item != null) {
            size--;
        }
        return item;
    }

    @Override
    public T get(int index) {
        Node p = sentinel;
        boolean itemExist = true;
        for (int i = 0; i <= index; i++) {
            p = p.next;
            if (p.equals(sentinel)) {
                itemExist = false;
            }
        }
        if (itemExist) {
            return p.item;
        }
        return null;
    }

    private T recursive(int index, Node p) {
        if (index == 0 || p.equals(sentinel)) {
            return p.item;
        }
        return recursive(index - 1, p.next);
    }

    public T getRecursive(int index) {
        Node p = sentinel.next;
        return recursive(index, p);
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDeque.LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private Node pos;
        LinkedListDequeIterator() {
            pos = sentinel;
        }

        public boolean hasNext() {
            return pos.next != sentinel;
        }

        public T next() {
            T returnItem = pos.item;
            pos = pos.next;
            return returnItem;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LinkedListDeque)) {
            return false;
        }
        LinkedListDeque<T> other = (LinkedListDeque<T>) o;
        if (other.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (other.get(i) != this.get(i)) {
                return false;
            }
        }
        return true;
    }
}

