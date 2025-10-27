package dev.masonak.redis.command.commands;

import dev.masonak.redis.command.Command;
import dev.masonak.redis.command.CommandContext;
import dev.masonak.redis.core.RedisDatabase;
import dev.masonak.redis.core.value.util.StreamIdUtil;
import dev.masonak.redis.core.value.StreamValue;
import dev.masonak.redis.core.value.util.StreamValueUtil;
import dev.masonak.redis.resp.datatype.BulkString;
import dev.masonak.redis.resp.datatype.RESPValue;
import dev.masonak.redis.resp.datatype.SimpleError;

import java.util.List;

public class XAddCommand implements Command {

    @Override
    public RESPValue execute(RedisDatabase db, List<String> args, CommandContext context) {
        if (args.size() % 2 == 0) {
            String streamKey = args.getFirst();
            String streamId;
            if (args.get(1).equals("*")){
                streamId = StreamIdUtil.sanitizeStreamID(streamKey,"*-*");
            } else {
                streamId = StreamIdUtil.sanitizeStreamID(streamKey, args.get(1));
            }
            String validity = StreamIdUtil.checkValidXAdd(streamKey, streamId);
            if (!db.containsKey(streamKey) || db.isStreamValue(streamKey)) {
                if (validity.equalsIgnoreCase("VALID")) {
                    if (db.containsKey(streamKey)) {
                        StreamValue streamValue = (StreamValue) db.get(streamKey);
                        StreamValueUtil.buildStreamValue(streamValue, args.subList(2, args.size()), streamId);
                        return new BulkString(streamId);
                    } else {
                        StreamValue addToDb = new StreamValue();
                        StreamValueUtil.buildStreamValue(addToDb, args.subList(2, args.size()), streamId);
                        db.put(streamKey, addToDb, null);
                        return new BulkString(streamId);
                    }
                } else {
                    return new SimpleError(validity);
                }
            } else {
                return new SimpleError("<stream_key> is already associated with a different value type");
            }
        }
        return SimpleError.syntax("syntax is XADD <id> <stream_key> <value> (multiple key -> value mappings permitted)");
    }

}