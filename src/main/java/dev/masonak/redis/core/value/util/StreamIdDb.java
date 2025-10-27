package dev.masonak.redis.core.value.util;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class StreamIdDb {

    /**
     * key = stream_key val = map of time part -> list of assoc seqnums
     */
    private static final Map<String, NavigableMap<Long, List<Long>>> streamIdDb = new ConcurrentSkipListMap<>();
    private static String lastStreamId = null;

    public static void addToStreamIdDb(String streamKey, String id) {
        try {
            long timePart = Long.parseLong(StreamIdUtil.parseTimePart(id));
            long sequenceNum = Long.parseLong(StreamIdUtil.parseSequenceNum(id));

            streamIdDb
                    .computeIfAbsent(streamKey, k -> new ConcurrentSkipListMap<>())
                    .computeIfAbsent(timePart, t -> new ArrayList<>())
                    .add(sequenceNum);

            setLastStreamId(id);
        } catch (NumberFormatException e) {
            System.err.println("Invalid stream ID: " + id);
        }
    }

    public static void addToStreamIdDb(String streamKey, Long id) {
        try {
            long timePart = Long.parseLong(StreamIdUtil.parseTimePart(Long.toString(id)));
            long sequenceNum = Long.parseLong(StreamIdUtil.parseSequenceNum(Long.toString(id)));

            streamIdDb
                    .computeIfAbsent(streamKey, k -> new ConcurrentSkipListMap<>())
                    .computeIfAbsent(timePart, t -> new ArrayList<>())
                    .add(sequenceNum);

            setLastStreamId(Long.toString(id));
        } catch (NumberFormatException e) {
            System.err.println("Invalid stream ID: " + id);
        }
    }

    public static List<Long> getListOfSequenceNums(String streamKey, Long timePartKey) {
        if (streamIdDb.containsKey(streamKey)) {
            if (streamIdDb.get(streamKey).containsKey(timePartKey)) {
                return streamIdDb.get(streamKey).get(timePartKey);
            }
        }
        return null;
    }

    public static NavigableMap<Long, List<Long>> getStreamKeyMap(String streamKey) {
        return streamIdDb.get(streamKey);
    }

    private static void setLastStreamId(String streamId) {
        lastStreamId = streamId;
    }

    public static String getLastStreamId() {
        return lastStreamId;
    }

    public static boolean containsStreamKey(String streamKey) {
        return streamIdDb.containsKey(streamKey);
    }

    public static boolean containsStreamKeyAndTimePartKey(String streamKey, String timePartKey) {
        return streamIdDb.containsKey(streamKey) && streamIdDb.get(streamKey).containsKey(Long.parseLong(timePartKey));
    }

    public static boolean containsStreamKeyAndTimePartKey(String streamKey, Long timePartKey) {
        return streamIdDb.containsKey(streamKey) && streamIdDb.get(streamKey).containsKey(timePartKey);
    }

    /*
    public static NavigableMap<Long, List<Long>> getStreamIdDb() {
        return streamIdDb;
    }
     */

    /*
    public static boolean containsKey(String key) {
        return streamIdDb.containsKey(Long.parseLong(key));
    }

    public static boolean containsKey(Long key) {
        return streamIdDb.containsKey(key);
    }
     */

}