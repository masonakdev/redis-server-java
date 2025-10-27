package dev.masonak.redis.command.commands;

import dev.masonak.redis.command.Command;
import dev.masonak.redis.command.CommandContext;
import dev.masonak.redis.core.RedisDatabase;
import dev.masonak.redis.core.value.StreamValue;
import dev.masonak.redis.core.value.util.StreamIdDb;
import dev.masonak.redis.core.value.util.StreamIdUtil;
import dev.masonak.redis.resp.datatype.*;

import java.util.*;

public class XReadCommand implements Command {

    private static final String SYNTAX_ERROR = "syntax is XREAD <type> <keys> <start_ids>(exclusive)";

    @Override
    public RESPValue execute(RedisDatabase db, List<String> args, CommandContext context) {
        if (args.size() % 2 == 1) {
            String argument = args.getFirst().toUpperCase(Locale.ROOT);
            switch(argument) {
                case "STREAMS" -> {
                    RArray rArray = new RArray();
                    List<String> streamKeys = parseStreamKeys(args);
                    List<String> streamIds = parseStreamIds(args);
                    if (streamIds.size() == streamKeys.size()) {
                        for (int i = 0; i < streamIds.size(); i++) {
                            System.out.print("XREAD(execute:STREAMS): calling streams with " + streamKeys.get(i) + " and " + streamIds.get(i) + "\n");
                            rArray.add(streams(db, List.of(streamKeys.get(i), streamIds.get(i))));
                        }
                        if (rArray.containsDataType(DataType.SimpleError)) {
                            return new SimpleError(SYNTAX_ERROR);
                        }
                        return rArray;
                    }
                    return new SimpleError(SYNTAX_ERROR);
                }
                default -> {
                    return new SimpleError(SYNTAX_ERROR);
                }
            }
        }
        return new SimpleError(SYNTAX_ERROR);
    }

    private RESPValue streams(RedisDatabase db, List<String> args) {
        String streamKey = args.getFirst();
        String startId = args.getLast().equals("0-0") ? "0-0" : StreamIdUtil.sanitizeStreamID(streamKey, args.getLast()); // edge case
        if (db.containsKey(streamKey)) {
            if (db.isStreamValue(streamKey)) {
                System.out.println("XREAD(streams): value of StreamValue in rdb for " + streamKey + " is {" + db.get(streamKey).toString() + "}");
                System.out.println("XREAD(streams): value of map in streamiddb for " + streamKey + " is {" + StreamIdDb.getStreamKeyMap(streamKey) + "}");
                List<String> idsInRange = StreamIdUtil.getStreamIdsFromStart(streamKey, startId, false);
                System.out.println("getstreamidsfromstart returned " + idsInRange);
                RArray rArray = new RArray();
                rArray.add(new BulkString(streamKey));
                for (String id : idsInRange) {
                    RArray idkApparentlyThisIsCorrect = new RArray();
                    RArray idRArray = new RArray();
                    idRArray.add(new BulkString(id));
                    StreamValue streamValue = (StreamValue) db.get(streamKey);
                    Map<String, String> idKeyValuePairs = new HashMap<>(streamValue.getNestedMap(id));
                    System.out.println("idkeyvalpairs are " + idKeyValuePairs);
                    for (Map.Entry<String, String> entry : idKeyValuePairs.entrySet()) {
                        RArray idKeyValRArray = new RArray();
                        idKeyValRArray.add(new BulkString(entry.getKey()));
                        idKeyValRArray.add(new BulkString(entry.getValue()));
                        idRArray.add(idKeyValRArray);
                    }
                    idkApparentlyThisIsCorrect.add(idRArray);
                    rArray.add(idkApparentlyThisIsCorrect);
                }
                return rArray;
            } else {
                return new SimpleError("input key value is associated with a " + db.get(streamKey).type() + " value");
            }
        } else {
            return new SimpleError("<stream_key> not recognized");
        }
    }

    private List<String> parseStreamKeys(List<String> args) {
        int counter = 1;
        while (!args.get(counter).contains("-")) {
            counter++;
        }
        System.out.println("parsekeys: returning " + args.subList(1, counter));
        return args.subList(1, counter);
    }

    private List<String> parseStreamIds(List<String> args) {
        List<String> result = new ArrayList<>();
        for (String s : args) {
            if (s.contains("-")) {
                result.add(s);
            }
        }
        System.out.println("parseIds: returning " + result);
        return result;
    }

}