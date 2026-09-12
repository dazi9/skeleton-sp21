package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K , V> {

    private int size = 0;

    private class BSTNode {
        private K key;
        private V val;
        private BSTNode left, right;
        public BSTNode(K key, V val) {
            this.key = key;
            this.val = val;
        }
    }

    private BSTNode root = null;

    public void printInOrder() {
        print(root);
    }

    private void print(BSTNode node) {
        if (node.left != null) {
            print(node.left);
        }
        System.out.println(node.key + " " + node.val);
        if (node.right != null) {
            print(node.right);
        }
    }

    /** Removes all of the mappings from this map. */
    public void clear() {
        clear(root);
        size = 0;
    }

    private void clear(BSTNode node) {
        if (node != null) {
            clear(node.left);
            clear(node.right);
            node = null;
        }
    }

    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        return get(root, key);
    }

    private V get(BSTNode node, K key) {
        if (key == null) {
            return null;
        }
        if (node == null){
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            return node.val;
        } else if (cmp > 0) {
            return get(node.right, key);
        } else {
            return get(node.left, key);
        }
    }

    /* Returns the number of key-value mappings in this map. */
    public int size() {
        return size;
    }

    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value) {
        if (get(key) != null) {
            size += 1;
        }
        put(root, key, value);
    }

    private void put(BSTNode node, K key, V value) {
        if (node == null) {
            node = new BSTNode(key, value);
            return;
        }
        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            node.key = key;
            node.val = value;
        } else if (cmp > 0) {
            put(node.right, key, value);
        } else {
            put (node.left, key, value);
        }
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("KeySet() not required");
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException("remove(key) not required");
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("remove(key, value) not required");
    }

    @Override
    public Iterator<K> iterator() {
        return null;
    }
}
