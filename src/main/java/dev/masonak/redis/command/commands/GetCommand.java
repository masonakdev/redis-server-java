package dev.masonak.redis.command.commands;

import dev.masonak.redis.command.Command;
import dev.masonak.redis.command.CommandContext;
import dev.masonak.redis.core.RedisDatabase;
import dev.masonak.redis.core.value.StringValue;
import dev.masonak.redis.core.value.Value;
import dev.masonak.redis.resp.datatype.BulkString;
import dev.masonak.redis.resp.datatype.NullBulkString;
import dev.masonak.redis.resp.datatype.RESPValue;
import dev.masonak.redis.resp.datatype.SimpleError;

import java.util.List;

public class GetCommand implements Command {

    @Override
    public RESPValue execute(RedisDatabase db, List<String> args, CommandContext context) {
        if (args.size() != 1) {
            return SimpleError.wrongArity("GET");
        }
        String key = args.get(0);

        Value v = db.get(key);              // <-- single get (may lazily expire and delete)
        if (v == null) return new NullBulkString();

        if (v instanceof StringValue sv) {  // <-- unwrap string payload
            return new BulkString(sv.value());  // adjust accessor name if different
        }
        return SimpleError.customError("WRONGTYPE Operation against a key holding the wrong kind of value");
    }

}