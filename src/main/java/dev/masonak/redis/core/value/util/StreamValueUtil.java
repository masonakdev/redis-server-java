package dev.masonak.redis.core.value.util;

import dev.masonak.redis.core.value.StreamValue;

import java.util.List;

public class StreamValueUtil {

    public static void buildStreamValue(StreamValue streamValue, List<String> args, String streamId) {
        int l = 0;
        int r = 1;
        while (r < args.size()) {
            streamValue.add(streamId, args.get(l), args.get(r));
            l += 2;
            r += 2;
        }
    }

}