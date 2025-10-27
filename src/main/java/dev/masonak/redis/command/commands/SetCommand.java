package dev.masonak.redis.command.commands;

import dev.masonak.redis.command.Command;
import dev.masonak.redis.command.CommandContext;
import dev.masonak.redis.core.RedisDatabase;
import dev.masonak.redis.core.value.StringValue;
import dev.masonak.redis.resp.datatype.RESPValue;
import dev.masonak.redis.resp.datatype.SimpleError;
import dev.masonak.redis.resp.datatype.SimpleString;

import java.util.List;

public class SetCommand implements Command {

    @Override
    public RESPValue execute(RedisDatabase db, List<String> args, CommandContext context) {
        if (args.size() < 2) {
            return SimpleError.wrongArity("SET");
        }

        final String key = args.get(0);
        final String value = args.get(1);

        Long ttlMillis = null;
        if (args.size() > 2) {
            if (args.size() != 4) {
                return SimpleError.syntax("syntax is `SET <key> <value> [EX seconds|PX milliseconds]`");
            }
            String opt = args.get(2);
            String num = args.get(3);
            try {
                long n = parsePositiveLong(num);
                if ("PX".equalsIgnoreCase(opt)) {
                    ttlMillis = n;
                } else if ("EX".equalsIgnoreCase(opt)) {
                    ttlMillis = n * 1000L;
                } else {
                    return SimpleError.syntax("syntax is `SET <key> <value> [EX seconds|PX milliseconds]`");
                }
            } catch (NumberFormatException e) {
                return SimpleError.syntax("value is not an integer or out of range");
            }
        }

        db.put(key, new StringValue(value), ttlMillis);
        return SimpleString.OK;
    }

    private static long parsePositiveLong(String s) {
        long n = Long.parseLong(s);
        if (n <= 0) throw new NumberFormatException("non-positive");
        return n;
    }

}