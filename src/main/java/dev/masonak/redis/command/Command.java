package dev.masonak.redis.command;

import dev.masonak.redis.core.RedisDatabase;
import dev.masonak.redis.resp.datatype.RESPValue;

import java.util.List;

@FunctionalInterface
public interface Command {
    RESPValue execute(RedisDatabase db, List<String> args, CommandContext context);
}