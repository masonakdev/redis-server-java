package dev.masonak.redis.core.value;

import java.nio.charset.StandardCharsets;

public record StringValue(
        String value
        ) implements Value {

    @Override
    public byte[] bytes() {
        return value().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public ValueType type() {
        return ValueType.String;
    }

    @Override
    public String toString() {
        return value;
    }

}