package deque;

public class LinkedListDeque<T> {
    private class node {
        public T item;
        node prev;
        node next;
    }
    private node sentinel = new node();
    private int size;

    public LinkedListDeque() {
        size = 0;
        sentinel.item = null;
        sentinel.prev = sentinel;
        sentinel.next = sentinel;

    }

    public void addFirst(T item) {
        node temp = new node();
        temp.prev = sentinel;
        temp.item = item;
        temp.next = sentinel.next;
        sentinel.next.prev = temp;
        sentinel.next = temp;
        size += 1;
    }

    public void addLast(T item) {
        sentinel.prev.next = new node();
        sentinel.prev.next.prev = sentinel.prev;
        sentinel.prev.next.item = item;
        sentinel.prev.next.next = sentinel;
        sentinel.prev = sentinel.prev.next;
        size += 1;
    }

    public boolean isEmpty() {
        return sentinel.next == sentinel;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        node p = new node();
        for (p = sentinel; p.next != sentinel; p = p.next) {
            System.out.print(p.item);
            System.out.print(" ");
        }
        System.out.println();
    }

    public T removeFirst() {
        T item = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        if (item != null) {
            size--;
        }
        return item;
    }

    public T removeLast() {
        T item = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        if (item != null) {
            size--;
        }
        return item;
    }

    public T get(int index) {
        node p = sentinel;
        boolean ItemExist = true;
        for (int i = 0; i <= index; i++) {
            p = p.next;
            if (p.equals(sentinel)) {
                ItemExist = false;
            }
        }
        if (ItemExist) {
            return p.item;
        }
        return null;
    }

    public T getRecursive(int index) {
        node p = sentinel.next;
        if (index > 0 && p.equals(sentinel)) {
            return null;
        }
        if (index == 0) {
            return p.item;
        }
        return getRecursive(index - 1);
    }

}

