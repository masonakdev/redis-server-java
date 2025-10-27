package dev.masonak.redis.core.value;

import java.util.HashMap;
import java.util.Map;

public final class StreamValue implements Value {
    /**
     * <Stream ID, <Value One, Value Two>> ALL STRINGS
     */
    private Map<String, Map<String, String>> streamValueMap = new HashMap<>();

    @Override
    public byte[] bytes() {
        return null;
    }

    @Override
    public ValueType type() {
        return ValueType.Stream;
    }

    public boolean containsKey(String key) {
        return streamValueMap.containsKey(key);
    }

    public void add(String key, String valueOne, String valueTwo) {
        streamValueMap.computeIfAbsent(key, k -> new HashMap<>()).put(valueOne, valueTwo);
    }

    public Map<String, Map<String, String>> getStreamValueMap() {
        return streamValueMap;
    }

    public Map<String, String> getNestedMap(String key) { return streamValueMap.get(key); }

    @Override
    public String toString() {
        return streamValueMap.toString();
    }

}