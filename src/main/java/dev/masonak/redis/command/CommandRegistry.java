package dev.masonak.redis.command;

import dev.masonak.redis.resp.datatype.RESPValue;
import dev.masonak.redis.resp.datatype.SimpleError;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class CommandRegistry {
    private static final class Entry {
        final CommandMetadata metadata;
        final Command impl;
        Entry(CommandMetadata metadata, Command impl) {
            this.metadata = metadata;
            this.impl = impl;
        }
    }

    private final Map<String, Entry> commands = new ConcurrentHashMap<>();

    public void register(CommandMetadata metadata, Command impl) {
        Objects.requireNonNull(metadata);
        Objects.requireNonNull(impl);
        String key = metadata.name();
        if(commands.putIfAbsent(key, new Entry(metadata, impl)) != null) {
            throw new IllegalStateException("Command already registered: " + key);
        }
    }

    public Optional<Command> lookup(String name) {
        Entry e = commands.get(name.toUpperCase(Locale.ROOT));
        return Optional.ofNullable(e).map(en -> en.impl);
    }

    public Optional<CommandMetadata> describe(String name) {
        Entry e = commands.get(name.toUpperCase(Locale.ROOT));
        return Optional.ofNullable(e).map(en -> en.metadata);
    }

    public Optional<RESPValue> validateArity(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return Optional.of(new SimpleError("empty command"));
        }
        String name = tokens.get(0);
        Entry e = commands.get(name.toUpperCase(Locale.ROOT));
        if (e == null) {
            return Optional.of(SimpleError.unknownCommand(name));
        }
        int arity = tokens.size();
        int min = e.metadata.minArity();
        int max = e.metadata.maxArity();
        if (arity < min || (max != -1 && arity > max)) {
            return Optional.of(SimpleError.wrongArity(name));
        }
        return Optional.empty();
    }

    public Set<String> names() {
        return Collections.unmodifiableSet(commands.keySet());
    }

}