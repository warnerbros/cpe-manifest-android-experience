package com.wb.nextgenlibrary.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Generic SoftHashMap based on com.dbxml.util.SoftHashMap with values wrapped in SoftReference
 */
public final class SoftHashMap<K, V> extends AbstractMap<K, V> {
    private final Map<K, SoftValue<K, V>> hashMap = new HashMap<K, SoftValue<K, V>>();
    private final ReferenceQueue<V> refQueue = new ReferenceQueue<V>();

    public SoftHashMap() {
    }

    public V get(Object key) {
        V value = null;
        SoftValue<K, V> sr = hashMap.get(key);
        if (sr != null) {
            value = sr.get();
            if (value == null) {
                hashMap.remove(key);
            }
        }
        return value;
    }

    public V put(K key, V value) {
        processQueue();
        SoftValue<K, V> oldValue = hashMap.put(key, new SoftValue<K, V>(key, value, refQueue));
        return oldValue != null ? oldValue.get() : null;
    }

    public V remove(Object key) {
        processQueue();
        SoftValue<K, V> oldValue = hashMap.remove(key);
        return oldValue != null ? oldValue.get() : null;
    }

    public void clear() {
        processQueue();
        hashMap.clear();
    }

    public int size() {
        processQueue();
        return hashMap.size();
    }

    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    private void processQueue() {
        for (;;) {
            @SuppressWarnings("unchecked")
            SoftValue<K, V> softValue = (SoftValue<K, V>) refQueue.poll();
            if (softValue != null) {
                hashMap.remove(softValue.key);
            } else {
                return;
            }
        }
    }

    private static class SoftValue<K, T> extends SoftReference<T> {
        private final K key;

        private SoftValue(K key, T value, ReferenceQueue<T> queue) {
            super(value, queue);
            this.key = key;
        }
    }
}