package dev.masonak.redis.core;

import dev.masonak.redis.concurrency.Waiter;
import dev.masonak.redis.core.key.ByteKey;
import dev.masonak.redis.core.key.Entry;
import dev.masonak.redis.core.value.ValueType;
import dev.masonak.redis.core.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RedisDatabase {

    private static final long NO_EXPIRY = Long.MIN_VALUE;

    private final Map<ByteKey, Entry> database = new ConcurrentHashMap<>();
    public final Map<String, List<Waiter>> blockedListKeys = new ConcurrentHashMap<>();

    /***
     * Put's a key value mapping into the REDIS db instance
     *
     * @param key String
     * @param value Value
     * @param ttlMillis {@code null} is no expiry
     */
    public void put(String key, Value value, Long ttlMillis) {
        long expireAt = (ttlMillis == null) ? NO_EXPIRY
                : System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(Math.max(0L, ttlMillis));
        database.put(ByteKey.fromUtf8(key), new Entry(value, expireAt));
        List<Waiter> monitorList = blockedListKeys.get(key);
        checkBlockedListKey(key);
    }

    private void checkBlockedListKey(String key) {
        List<Waiter> monitorList = blockedListKeys.get(key);
        if (monitorList != null && !monitorList.isEmpty()) {
            Waiter first = monitorList.remove(0);
            synchronized (first) {
                first.doNotify();
            }
            if (monitorList.isEmpty()) {
                blockedListKeys.remove(key);
            }
        }
    }

    public Value get(String key) {
        ByteKey k = ByteKey.fromUtf8(key);
        Entry e = database.get(k);
        if (e == null) return null;
        long now = System.nanoTime();
        if (e.isExpired(now)) {
            database.remove(k, e);
            return null;
        }
        return e.value;
    }

    public boolean containsKey(String key) {
        ByteKey k = ByteKey.fromUtf8(key);
        Entry e = database.get(k);
        if (e == null) return false;
        long now = System.nanoTime();
        if (e.isExpired(now)) {
            database.remove(k, e);
        }
        return true;
    }

    public boolean del(String key) {
        return database.remove(ByteKey.fromUtf8(key)) != null;
    }

    public void blockListKey(String key, Waiter waiter) {
        blockedListKeys.computeIfAbsent(key, k -> new ArrayList<>()).addLast(waiter);
    }

    public void unblockListKey(String key) {
        blockedListKeys.remove(key);
    }

    public boolean isStreamValue(String key) {
        if (get(key) != null) {
            return get(key).type() == ValueType.Stream;
        }
        return false;
    }

}