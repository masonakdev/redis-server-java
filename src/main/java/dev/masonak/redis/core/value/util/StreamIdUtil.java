package dev.masonak.redis.core.value.util;

import java.util.*;

public class StreamIdUtil {

    public static Long autoGenerateTimePart() {
        return System.currentTimeMillis();
    }

    public static Long autoGenerateSequenceNum(String streamKey, String inputTimePart) {
        Long timePartKey = toLong(inputTimePart);
        if (StreamIdDb.containsStreamKeyAndTimePartKey(streamKey, timePartKey)) {
            Long prevSequenceNum = StreamIdDb.getListOfSequenceNums(streamKey, timePartKey).getLast();
            return prevSequenceNum + 1;
        }
        if (timePartKey == 0) return 1L;
        return 0L;
    }

    public static Long autoGenerateSequenceNum(String streamKey, Long timePartKey) {
        if (StreamIdDb.getStreamKeyMap(streamKey).containsKey(timePartKey)) {
            Long prevSequenceNum = StreamIdDb.getListOfSequenceNums(streamKey, timePartKey).getLast();
            return prevSequenceNum + 1;
        } else {
            if (timePartKey == 0) return 1L;
            return 0L;
        }
    }

    public static String parseSequenceNum(String streamId) {
        String[] temp = streamId.split("-");
        return temp[1];
    }

    public static String parseTimePart(String streamId) {
        String[] temp = streamId.split("-");
        return temp[0];
    }

    /**
     * If result is valid, IT WILL BE ADDED TO STREAMIDDB
     *
     * @param streamId The already parsed stream ID
     * @return Returns "VALID" if valid, otherwise returns SimpleError payload
     */
    public static String checkValidXAdd(String streamKey, String streamId) {
        long currTimePart = Long.parseLong(parseTimePart(streamId));
        long currSequenceNum = Long.parseLong(parseSequenceNum(streamId));
        if (StreamIdDb.getLastStreamId() != null) {
            String lastId = StreamIdDb.getLastStreamId();
            long lastTimePart = Long.parseLong(parseTimePart(lastId));
            // Get the last sequence num associated with the time part rather than the sequence num associated with the last entry
            long lastSequenceNum = StreamIdDb.getListOfSequenceNums(streamKey, currTimePart) == null ? -1L : StreamIdDb.getListOfSequenceNums(streamKey, currTimePart).getLast();
            if (currTimePart > lastTimePart || (currTimePart == lastTimePart && currSequenceNum > lastSequenceNum)) {
                StreamIdDb.addToStreamIdDb(streamKey, streamId);
                return "VALID";
            } else if (currTimePart == 0 && currSequenceNum == 0) { // This may be unnecessary, but I'm too scared to remove it
                return "The ID specified in XADD must be greater than 0-0";
            } else {
                return "The ID specified in XADD is equal or smaller than the target stream top item";
            }
        } else if (currTimePart >= 0 && currSequenceNum >= 0){
            StreamIdDb.addToStreamIdDb(streamKey, streamId);
            return "VALID";
        } else {
            return "The ID specified in XADD must be greater than 0-0";
        }
    }

    public static String combineId(String timePart, String sequenceNum) {
        return timePart + "-" + sequenceNum;
    }

    public static String combineId(Long timePart, Long sequenceNum) { return toString(timePart) + "-" + toString(sequenceNum); }

    /**
     * Handles auto-generation of IDs ('*-*' ONLY) and converts time parts to fully formed IDs.
     *
     * @param input
     * @return Newly generated ID, same ID, or: if input = timePart, <timepart>-0
     */
    public static String sanitizeStreamID(String streamKey, String input) {
        if (input.contains("-")) {
            String timePart = parseTimePart(input);
            String sequenceNum = parseSequenceNum(input);
            if (timePart.equals("*")) timePart = toString(autoGenerateTimePart());
            if (sequenceNum.equals("*")) sequenceNum = toString(autoGenerateSequenceNum(streamKey, timePart));
            return combineId(timePart, sequenceNum);
        } else {
            return combineId(input, "0");
        }
    }

    /**
     * Only accepts fully formed IDs.
     *
     * @param floorId
     * @param ceilingId
     * @return
     */
    public static List<String> getStreamIdsInRange(String streamKey, String floorId, String ceilingId) {
        long floorTimePart = toLong(parseTimePart(floorId));
        long ceilingTimePart = toLong(parseTimePart(ceilingId));
        List<String> result = new ArrayList<>();
        NavigableMap<Long, List<Long>> streamKeyMap = new TreeMap<>(StreamIdDb.getStreamKeyMap(streamKey));
        SortedMap<Long, List<Long>> subMap = streamKeyMap.subMap(floorTimePart, true, ceilingTimePart, true);
        long startingSeqNum = toLong(parseSequenceNum(floorId));
        long endingSeqNum = toLong(parseSequenceNum(ceilingId));
        int counter = subMap.size();
        for (Map.Entry<Long, List<Long>> entry : subMap.entrySet()) {
            long key = entry.getKey();
            List<Long> value = new ArrayList<>(entry.getValue());
            for (Long seqNum : value) {
                if (seqNum >= startingSeqNum && (counter > 1 || seqNum <= endingSeqNum)) {
                    result.add(combineId(key, seqNum));
                }
            }
            counter--;
            startingSeqNum = 0;
        }
        return result;
    }

    public static List<String> getStreamIdsFromStart(String streamKey, String floorId, boolean fromInclusive) {
        System.out.println("getfromstart(received): " + streamKey + " ; " + floorId + " ; " + fromInclusive);
        long floorTimePart = toLong(parseTimePart(floorId));
        long floorSeqNum = toLong(parseSequenceNum(floorId));
        List<String> result = new ArrayList<>();
        NavigableMap<Long, List<Long>> fullMap = new TreeMap<>(StreamIdDb.getStreamKeyMap(streamKey));
        SortedMap<Long, List<Long>> subMap;
        System.out.println("getfromstart(vars):\n\tfloorTimePart={" + floorTimePart + "}" + "\n\tfloorSeqNum={" + floorSeqNum + "}" + "\n\tfloorId={" + floorId + "}" + "\n\tstreamKey={" + streamKey + "}" +
                "\ngetfromstart(vars)[fullMap]:" + " fullMap={" + fullMap + "}" + "\n\tfullMapFirstKey={" + fullMap.firstKey() + "}" + "\n\tfullMapLastKey={" + fullMap.lastKey() + "}");
        if (floorId.equals("0-0")) {
            subMap = fullMap.subMap(fullMap.firstKey(), true, fullMap.lastKey(), true);
            System.out.println("getfromstart(submap:floorId=0-0):\n\t" + subMap);
        } else {
            if (fullMap.firstKey() == floorTimePart && fullMap.get(floorTimePart).getFirst() >= floorSeqNum) {
                subMap = fullMap.subMap(fullMap.firstKey(), true, fullMap.lastKey(), true);
                System.out.println("getfromstart(submap:firstkey==ftp&&firstSeqNum>=floorSeqNum):\n\t" + subMap);
            } else {
                subMap = fullMap.subMap(floorTimePart, fromInclusive, fullMap.lastKey(), true);
                System.out.println("getfromstart(submap:else:else):\n\t" + subMap);
            }
        }
        for (Map.Entry<Long, List<Long>> entry : subMap.entrySet()) {
            long key = entry.getKey();
            List<Long> value = new ArrayList<>(entry.getValue());
            for (Long seqNum : value) {
                result.add(combineId(key, seqNum));
            }
        }
        return result;
    }

    public static Long toLong(String input) {
        return Long.parseLong(input);
    }

    public static String toString(Long input) {
        return Long.toString(input);
    }

}