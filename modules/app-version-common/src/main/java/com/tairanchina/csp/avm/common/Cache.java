package com.tairanchina.csp.avm.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author me 2022-11-29 00:11
 */
public class Cache {

    public static final Map<String, Map<String, String>> cache = new ConcurrentHashMap<>();

    public static String get(String key, String hashKey) {
        return cache.computeIfAbsent(key, s -> new HashMap<>()).get(hashKey);
    }

    public static void del(String key, String hashKey) {
        cache.computeIfAbsent(key, s -> new HashMap<>()).remove(hashKey);
    }

    public static void set(String key, String hashKey, String data) {
        cache.computeIfAbsent(key, s -> new HashMap<>()).put(hashKey, data);
    }
}
