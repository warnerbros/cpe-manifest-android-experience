package com.wb.nextgenlibrary.util.utils;

import java.util.HashMap;
import java.util.Map;

public class ObjectHolder {
    private static final ObjectHolder INSTANCE = new ObjectHolder();

    private final Map<String, Object> map;

    private ObjectHolder() {
        map = new HashMap<String, Object>();
    }

    public static ObjectHolder instance() {
        return INSTANCE;
    }

    public synchronized Object get(String key) {
        return map.get(key);
    }

    public synchronized void put(String key, Object value) {
        map.put(key, value);
    }

    public synchronized Object remove(String key) {
        return map.remove(key);
    }

    public synchronized int size() {
        return map.size();
    }

    public synchronized void clear() {
        map.clear();
    }
}