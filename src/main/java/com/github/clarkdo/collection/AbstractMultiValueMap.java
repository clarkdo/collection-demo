package com.github.clarkdo.collection;

import javax.ws.rs.core.MultivaluedMap;
import java.util.*;

public abstract class AbstractMultiValueMap<K, V> implements MultivaluedMap<K, V> {

    /**
     * Removes the specific value inside list for a key from this map if it is present.
     * If this map is empty after removing, the key will also be removed from this map.
     *
     * @param key the key
     * @param value value in list
     */
    public abstract List<V> removeValue(K key, V value);
}