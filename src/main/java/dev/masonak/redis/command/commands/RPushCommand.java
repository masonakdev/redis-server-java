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

public class RPushCommand implements Command {

    @Override
    public RESPValue execute(RedisDatabase db, List<String> args, CommandContext context) {
        if (args.isEmpty()) {
            return SimpleError.wrongArity("RPUSH");
        } else if (args.size() >= 2) {
            if (!db.containsKey(args.getFirst())) {
                ListValue listValue = new ListValue();
                for (int i = 1; i < args.size(); i++) {
                    listValue.addLast(args.get(i));
                }
                int size = listValue.size();
                db.put(args.getFirst(), listValue, null);
                return new RInteger(size);
            } else {
                Value value = db.get(args.getFirst());
                ListValue listValue = value instanceof ListValue ? (ListValue) value : null;
                if (listValue == null) return SimpleError.customError("the value associated with the key `" + args.getFirst() + "` is not a list");
                for (int i = 1; i < args.size(); i++) {
                    listValue.addLast(args.get(i));
                }
                int size = listValue.size();
                db.put(args.getFirst(), listValue, null);
                return new RInteger(size);
            }
        }
        return SimpleError.syntax("syntax is RPUSH <list_key> <values>");
    }

}