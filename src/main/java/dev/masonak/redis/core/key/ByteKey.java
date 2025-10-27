package dev.masonak.redis.core.key;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class ByteKey {

    private final byte[] bytes;

    public ByteKey(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override public boolean equals(Object o) {
        return o instanceof ByteKey k && Arrays.equals(bytes, k.bytes);
    }
    @Override public int hashCode() { return Arrays.hashCode(bytes); }

    public static ByteKey fromUtf8(String input) {
        return new ByteKey(input.getBytes(StandardCharsets.UTF_8));
    }

}