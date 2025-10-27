package dev.masonak.redis.command.commands;

import dev.masonak.redis.command.Command;
import dev.masonak.redis.command.CommandContext;
import dev.masonak.redis.core.RedisDatabase;
import dev.masonak.redis.resp.datatype.BulkString;
import dev.masonak.redis.resp.datatype.RESPValue;
import dev.masonak.redis.resp.datatype.SimpleString;

import java.util.List;

public class PingCommand implements Command {

    @Override
    public RESPValue execute(RedisDatabase db, List<String> args, CommandContext context) {
        if (args.isEmpty()) return new SimpleString("PONG");
        // TODO is this right?
        return new BulkString(String.join(" ", args));
    }

}