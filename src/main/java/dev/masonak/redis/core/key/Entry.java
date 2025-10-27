package dev.masonak.redis.core.key;

import dev.masonak.redis.core.value.Value;

public final class Entry {

    private static final long NO_EXPIRY = Long.MIN_VALUE;

    public final Value value;
    public final long expireAtNanos; // NO_EXPIRY means no TTL

    public Entry(Value value, long expireAtNanos) {
        this.value = value;
        this.expireAtNanos = expireAtNanos;
    }

    public boolean isExpired(long now) {
        return expireAtNanos != NO_EXPIRY && now >= expireAtNanos;
    }

}