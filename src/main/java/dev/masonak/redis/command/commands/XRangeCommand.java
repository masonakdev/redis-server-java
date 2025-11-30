package dev.masonak.redis.command.commands;

import dev.masonak.redis.command.Command;
import dev.masonak.redis.command.CommandContext;
import dev.masonak.redis.core.RedisDatabase;
import dev.masonak.redis.core.value.ValueType;
import dev.masonak.redis.core.value.util.StreamIdDb;
import dev.masonak.redis.core.value.util.StreamIdUtil;
import dev.masonak.redis.core.value.StreamValue;
import dev.masonak.redis.resp.datatype.BulkString;
import dev.masonak.redis.resp.datatype.RArray;
import dev.masonak.redis.resp.datatype.RESPValue;
import dev.masonak.redis.resp.datatype.SimpleError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XRangeCommand implements Command {

    /**
     * When I wrote this, only God and I understood it. Now only God does.
     *
     * @param db
     * @param args
     * @param context
     * @return
     */
    @Override
    public RESPValue execute(RedisDatabase db, List<String> args, CommandContext context) {
        if (args.size() == 3) {
            String streamKey = args.getFirst();
            if (db.containsKey(streamKey) && db.get(streamKey).type() != ValueType.Stream) {
                return new SimpleError("the specified key does not exist or is not associated with a stream");
            }
            if (!StreamIdDb.containsStreamKey(streamKey)) {
                return new RArray();
            }
            String floorId = args.get(1).equals("-")
                    ? StreamIdUtil.sanitizeStreamID(streamKey,
                            Long.toString(StreamIdDb.getStreamKeyMap(streamKey).firstEntry().getKey()))
                    : StreamIdUtil.sanitizeStreamID(streamKey, args.get(1));
            String ceilingId = args.get(2).equals("+")
                    ? StreamIdUtil.sanitizeStreamID(streamKey,
                            StreamIdDb.getStreamKeyMap(streamKey).lastEntry().getKey() + "-"
                                    + StreamIdDb.getStreamKeyMap(streamKey).lastEntry().getValue().getLast())
                    : StreamIdUtil.sanitizeStreamID(streamKey, args.get(2));
            List<String> idsInRange = StreamIdUtil.getStreamIdsInRange(streamKey, floorId, ceilingId);
            RArray rArray = new RArray();
            for (String id : idsInRange) {
                RArray idRArray = new RArray();
                idRArray.add(new BulkString(id));
                String redisDbKey = args.getFirst();
                StreamValue streamValue;
                if (db.get(redisDbKey) != null && db.get(redisDbKey).type() == ValueType.Stream) {
                    streamValue = (StreamValue) db.get(redisDbKey);
                    Map<String, String> idKeyValuePairs = new HashMap<>(streamValue.getNestedMap(id));
                    for (Map.Entry<String, String> entry : idKeyValuePairs.entrySet()) {
                        RArray idKeyValRArray = new RArray();
                        idKeyValRArray.add(new BulkString(entry.getKey()));
                        idKeyValRArray.add(new BulkString(entry.getValue()));
                        idRArray.add(idKeyValRArray);
                    }
                    rArray.add(idRArray);
                } else {
                    return new SimpleError("the specified key does not exist or is not associated with a stream");
                }
            }
            return rArray;
        }
        return new SimpleError("syntax is XRANGE <key> <from_id> <to_id> (inclusive)");
    }

}