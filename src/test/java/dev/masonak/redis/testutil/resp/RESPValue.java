package dev.masonak.redis.testutil.resp;

import java.nio.charset.StandardCharsets;

public sealed interface RESPValue permits BulkString, NoResponse, NullBulkString, RArray, RInteger, SimpleError, SimpleString {

    default byte[] bytes() {
        return RESPEncoder.encode(this).getBytes(StandardCharsets.UTF_8);
    }
    default String string() { return RESPEncoder.encode(this); }
    DataType getDataType();
    String getUnencodedPayload();

}