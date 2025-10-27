package dev.masonak.redis.resp.datatype;

import dev.masonak.redis.resp.protocol.RESPEncoder;

import java.nio.charset.StandardCharsets;

public sealed interface RESPValue permits BulkString, NoResponse, NullBulkString, RArray, RInteger, SimpleError, SimpleString {

    default byte[] bytes() {
        return RESPEncoder.encode(this).getBytes(StandardCharsets.UTF_8);
    }
    DataType getDataType();
    String getUnencodedPayload();

}