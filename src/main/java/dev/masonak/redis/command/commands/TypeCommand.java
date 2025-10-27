package dev.masonak.redis.command.commands;

import dev.masonak.redis.command.Command;
import dev.masonak.redis.command.CommandContext;
import dev.masonak.redis.core.RedisDatabase;
import dev.masonak.redis.resp.datatype.RESPValue;
import dev.masonak.redis.resp.datatype.SimpleError;
import dev.masonak.redis.resp.datatype.SimpleString;

import java.util.List;
import java.util.Locale;

public class TypeCommand implements Command {

    @Override
    public RESPValue execute(RedisDatabase db, List<String> args, CommandContext context) {
        if(args.size() == 1) {
            if (db.containsKey(args.getFirst())) {
                String valueType = db.get(args.getFirst()).type().toString().toLowerCase(Locale.ROOT);
                return new SimpleString(valueType);
            } else {
                return new SimpleString("none");
            }
        }
        return SimpleError.syntax("syntax is TYPE <key>");
    }

}