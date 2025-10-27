package dev.masonak.redis.command.commands;

import dev.masonak.redis.command.Command;
import dev.masonak.redis.command.CommandContext;
import dev.masonak.redis.core.RedisDatabase;
import dev.masonak.redis.core.value.ListValue;
import dev.masonak.redis.core.value.Value;
import dev.masonak.redis.resp.datatype.BulkString;
import dev.masonak.redis.resp.datatype.RArray;
import dev.masonak.redis.resp.datatype.RESPValue;
import dev.masonak.redis.resp.datatype.SimpleError;

import java.util.List;

public class LRangeCommand implements Command {

    @Override
    public RESPValue execute(RedisDatabase db, List<String> args, CommandContext context) {
        if (args.isEmpty()) {
            return SimpleError.wrongArity("LRANGE");
        } else if (args.size() == 3) {
            if (!db.containsKey(args.getFirst())) return new RArray();
            int startIndex, stopIndex;
            try {
                startIndex = Integer.parseInt(args.get(1));
                stopIndex = Integer.parseInt(args.get(2));
            } catch (NumberFormatException e) {
                return SimpleError.syntax("syntax is LRANGE <list_key> <start_index> <stop_index>");
            }
            Value value = db.get(args.getFirst());
            ListValue listValue = value instanceof ListValue ? (ListValue) value : null;
            if (listValue == null) return SimpleError.customError("the value associated with the key `" + args.getFirst() + "` is not a list");
            if (startIndex < 0) startIndex = convertNegativeIndex(startIndex, listValue.size());
            if (stopIndex < 0) stopIndex = convertNegativeIndex(stopIndex, listValue.size());
            if (startIndex >= listValue.size() || startIndex > stopIndex) {
                return new RArray();
            }
            stopIndex = stopIndex >= listValue.size() ? listValue.size() - 1 : stopIndex;
            ListValue listValueSublist = listValue.sublist(startIndex, stopIndex);
            List<String> sublist = listValueSublist.toStringList();
            RArray rArray = new RArray();
            for (int i = 0; i < sublist.size(); i++) {
                rArray.add(new BulkString(sublist.get(i)));
            }
            return rArray;
        }
        return SimpleError.syntax("syntax is LRANGE <list_key> <start_index> <stop_index>");
    }

    private int convertNegativeIndex(int index, int arrSize) {
        if (Math.abs(index) >= arrSize) return 0;
        return index + arrSize;
    }

}