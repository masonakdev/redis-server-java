package dev.masonak.redis.command;

import java.util.Set;

public record CommandMetadata(
        String name,
        int minArity, // includes command (e.g. SET key value = 3)
        int maxArity, // -1 is unlimited
        Set<Flag> flags
){}