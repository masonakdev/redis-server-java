package dev.masonak.redis.core.value;

public interface Value  {
    byte[] bytes();
    ValueType type();
}