package dev.masonak.redis.command;

import dev.masonak.redis.core.RedisDatabase;

import java.util.List;

public class Dispatcher {

    private final CommandRegistry registry;
    private final RedisDatabase db;

    public Dispatcher(CommandRegistry registry, RedisDatabase db) {
        this.registry = registry;
        this.db = db;
    }

    public byte[] dispatch(List<String> tokens, CommandContext context) {
        var maybeErr = registry.validateArity(tokens);
        if (maybeErr.isPresent()) return maybeErr.get().bytes();

        String name = tokens.get(0);
        var args = tokens.subList(1, tokens.size());
        var cmd = registry.lookup(name).orElseThrow();
        return cmd.execute(db, args, context).bytes();
    }

}