package dev.masonak.redis.resp.protocol;

import dev.masonak.redis.resp.datatype.DataType;
import dev.masonak.redis.resp.datatype.RArray;
import dev.masonak.redis.resp.datatype.RESPValue;
import dev.masonak.redis.resp.datatype.SimpleError;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RESPEncoder {

    private static final String DELIMITER = "\r\n";

    private RESPEncoder() {
        throw new AssertionError("RESPEncoder can not be instantiated.");
    }

    public static String encode(RESPValue respValue){
        DataType dt = respValue.getDataType();
        switch(dt) {
            case BulkString -> {
                return encodeBulkString(respValue.getUnencodedPayload());
            }
            case NullBulkString -> {
                return encodeNullBulkString();
            }
            case SimpleError -> {
                SimpleError sE = (SimpleError) respValue;
                return encodeSimpleError(sE.getPrefix(), sE.getUnencodedPayload());
            }
            case SimpleString -> {
                return encodeSimpleString(respValue.getUnencodedPayload());
            }
            case RInteger -> {
                return encodeRInteger(respValue.getUnencodedPayload());
            }
            case NoResponse -> {
                return null;
            }
            case RArray -> {
                RArray rArray = (RArray) respValue;
                return encodeRArray(rArray.getList(), rArray.isNull());
            }
            default -> {
                throw new Error("Something bad happened. Create a GitHub issue and say, \"RESPEncoder.java, good luck lmfao.\"");
            }
        }
    }

    private static String encodeBulkString(String input) {
        int length = input.getBytes(StandardCharsets.UTF_8).length;
        return "$" + length + DELIMITER + input + DELIMITER;
    }

    private static String encodeNullBulkString() {
        return "$-1" + DELIMITER;
    }

    private static String encodeSimpleString(String input) {
        return "+" + input + DELIMITER;
    }

    private static String encodeRInteger(String input) {
        return ":" + input + DELIMITER;
    }

    static String encodeSimpleError(String prefix, String input) {
        return prefix + " " + input + DELIMITER;
    }

    public static String encodeRArray(List<RESPValue> respValueArray, boolean isNull) {
        if (isNull) return "*-1\r\n";
        if (respValueArray.isEmpty()) return "*0\r\n";
        StringBuilder sb = new StringBuilder();
        for (RESPValue respValue : respValueArray) {
            DataType dt = respValue.getDataType();
            switch(dt) {
                case BulkString -> {
                    sb.append(encodeBulkString(respValue.getUnencodedPayload()));
                }
                case NullBulkString -> {
                    sb.append(encodeNullBulkString());
                }
                case SimpleError -> {
                    SimpleError sE = (SimpleError) respValue;
                    sb.append(encodeSimpleError(sE.getPrefix(), sE.getUnencodedPayload()));
                }
                case SimpleString -> {
                    sb.append(encodeSimpleString(respValue.getUnencodedPayload()));
                }
                case RInteger -> {
                    sb.append(encodeRInteger(respValue.getUnencodedPayload()));
                }
                case RArray ->  {
                    RArray holyRecursion = (RArray) respValue;
                    sb.append(encodeRArray(holyRecursion.getList(), holyRecursion.isNull()));
                }
                default -> {
                    throw new Error("Something bad happened. Create a GitHub issue and say, \"RESPEncoder.java, good luck lmfao.\"");
                }
            }
        }
        return "*" + respValueArray.size() + DELIMITER + sb;
    }

}