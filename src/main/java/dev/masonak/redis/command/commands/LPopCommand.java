package dev.masonak.redis.command.commands;

import dev.masonak.redis.command.Command;
import dev.masonak.redis.command.CommandContext;
import dev.masonak.redis.core.RedisDatabase;
import dev.masonak.redis.core.value.ListValue;
import dev.masonak.redis.core.value.Value;
import dev.masonak.redis.resp.datatype.*;

import java.util.List;

public class LPopCommand implements Command {

    @Override
    public RESPValue execute(RedisDatabase db, List<String> args, CommandContext context) {
        if (args.isEmpty()) {
            return SimpleError.wrongArity("LPOP");
        } else if (args.size() == 1) {
            if (db.containsKey(args.getFirst())) {
                Value value = db.get(args.getFirst());
                ListValue listValue = value instanceof ListValue ? (ListValue) value : null;
                if (listValue == null) return SimpleError.customError("the value associated with the key `" + args.getFirst() + "` is not a list");
                return new BulkString(listValue.pollFirst());
            } else {
                return new NullBulkString();
            }
        } else if (args.size() == 2) {
            int count;
            try {
                count = Integer.parseInt(args.getLast());
            } catch (NumberFormatException e) {
                return SimpleError.syntax("syntax is LPOP <list_key> <num_elements>");
            }
            Value value = db.get(args.getFirst());
            ListValue listValue = value instanceof ListValue ? (ListValue) value : null;
            if (listValue == null) return SimpleError.customError("the value associated with the key `" + args.getFirst() + "` is not a list");
            RArray rArray = new RArray();
            for (int i = 0; i < count; i++) {
                rArray.add(new BulkString(listValue.pollFirst()));
            }
            return rArray;
        }
        return SimpleError.syntax("syntax is LPOP <list_key> <num_elements>");
    }

}