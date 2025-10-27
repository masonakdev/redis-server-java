package dev.masonak.redis.command.commands;

import dev.masonak.redis.command.Command;
import dev.masonak.redis.command.CommandContext;
import dev.masonak.redis.core.RedisDatabase;
import dev.masonak.redis.core.value.ListValue;
import dev.masonak.redis.core.value.Value;
import dev.masonak.redis.resp.datatype.RESPValue;
import dev.masonak.redis.resp.datatype.RInteger;
import dev.masonak.redis.resp.datatype.SimpleError;

import java.util.List;

public class LLenCommand implements Command {

    @Override
    public RESPValue execute(RedisDatabase db, List<String> args, CommandContext context) {
        if (args.isEmpty()) {
            return SimpleError.wrongArity("LLEN");
        } else if (args.size() == 1) {
            if (db.containsKey(args.getFirst())) {
                Value value = db.get(args.getFirst());
                ListValue listValue = value instanceof ListValue ? (ListValue) value : null;
                if (listValue == null) return SimpleError.customError("the value associated with the key `" + args.getFirst() + "` is not a list");
                return new RInteger(listValue.size());
            } else {
                return new RInteger(0);
            }
        }
        return SimpleError.syntax("syntax is LLEN <list_key>");
    }

}