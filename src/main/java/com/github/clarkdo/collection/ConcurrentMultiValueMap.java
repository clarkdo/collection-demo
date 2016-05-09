package com.github.clarkdo.collection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A concurrent implementation of MultivaluedMap
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author clarkdo
 * @since 1.7
 */
public class ConcurrentMultiValueMap<K, V> extends AbstractMultiValueMap<K, V> {

    private static final int DEFAULT_CAPACITY = 20;
    private final Map<K, List<V>> replica;

    public ConcurrentMultiValueMap() {
        this(DEFAULT_CAPACITY);
    }

    public ConcurrentMultiValueMap(int initialCapacity) {
        this(new ConcurrentHashMap<>(initialCapacity));
    }

    public ConcurrentMultiValueMap(ConcurrentMap<K, List<V>> replica) {
        if (replica == null) {
            throw new IllegalArgumentException("Replication map should be initialized.");
        }
        this.replica = replica;
    }

    /**
     * @see javax.ws.rs.core.MultivaluedMap#putSingle
     */
    @Override
    public void putSingle(K key, V value) {
        List<V> item = Collections.singletonList(value);
        replica.put(key, item);
    }

    /**
     * @see javax.ws.rs.core.MultivaluedMap#add
     */
    @Override
    public void add(K key, V value) {
        try {
            this.getAndPut(key).add(value);
        } catch (UnsupportedOperationException ex) {
            throw ex;
        } catch (Exception ex) {
            List<V> item = this.get(key);
            if(item != null){
                item.add(value);
            }
        }
    }

    /**
     * @see javax.ws.rs.core.MultivaluedMap#getFirst
     */
    @Override
    public V getFirst(K key) {
        V result = null;
        List<V> item = replica.get(key);
        if (replica.containsKey(key) && !item.isEmpty()) {
            result = item.get(0);
        }
        return result;
    }

    /**
     * @see java.util.Map#size
     */
    @Override
    public int size() {
        return replica.size();
    }

    /**
     * @see java.util.Map#isEmpty
     */
    @Override
    public boolean isEmpty() {
        return replica.isEmpty();
    }

    /**
     * @see java.util.Map#containsKey
     */
    @Override
    public boolean containsKey(Object key) {
        return replica.containsKey(key);
    }

    /**
     * @see java.util.Map#containsValue
     */
    @Override
    public boolean containsValue(Object value) {
        return replica.containsValue(value);
    }

    /**
     * @see java.util.Map#get
     */
    @Override
    public List<V> get(Object key) {
        return replica.get(key);
    }

    /**
     * @see java.util.Map#put
     */
    @Override
    public List<V> put(K key, List<V> value) {
        return replica.put(key, Collections.synchronizedList(new ArrayList<>(value)));
    }

    /**
     * @see java.util.Map#remove
     */
    @Override
    public List<V> remove(Object key) {
        return replica.remove(key);
    }

    /**
     * @see AbstractMultiValueMap#removeValue
     */
    @Override
    public List<V> removeValue(K key, V value) {
        List<V> item = replica.get(key);
        if (item != null) {
            synchronized (this) {
                item = replica.get(key);
                if (item != null) {
                    if (item.contains(value)) {
                        item.remove(value);
                    }
                    if (item.isEmpty()) {
                        this.remove(key);
                    }
                }
            }
        }
        return item;
    }

    /**
     * @see java.util.Map#putAll
     */
    @Override
    public void putAll(Map<? extends K, ? extends List<V>> m) {
        replica.putAll(m);
    }

    /**
     * @see java.util.Map#clear
     */
    @Override
    public void clear() {
        replica.clear();
    }

    /**
     * @see java.util.Map#keySet
     */
    @Override
    public Set<K> keySet() {
        return replica.keySet();
    }

    /**
     * @see java.util.Map#values
     */
    @Override
    public Collection<List<V>> values() {
        return replica.values();
    }

    /**
     * @see java.util.Map#entrySet
     */
    @Override
    public Set<Map.Entry<K, List<V>>> entrySet() {
        return replica.entrySet();
    }

    /**
     * Get the list for a key from this map if it is present.
     * If it is not, put a new list corresponding the key into the map.
     *
     * @param key the key
     */
    private List<V> getAndPut(K key) {
        List<V> item = replica.get(key);
        if (item == null) {
            synchronized (this) {
                item = replica.get(key);
                if (item == null) {
                    item = Collections.synchronizedList(new ArrayList<>());
                    replica.put(key, item);
                }
            }
        }
        return item;
    }
}