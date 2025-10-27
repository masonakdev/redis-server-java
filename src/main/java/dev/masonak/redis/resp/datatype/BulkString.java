package dev.masonak.redis.resp.datatype;

import dev.masonak.redis.resp.protocol.RESPEncoder;

public final class BulkString implements RESPValue {

    private final String unencodedPayload;

    public BulkString() {
        this.unencodedPayload = "";
    }

    public BulkString(String unencodedPayload) {
        this.unencodedPayload = unencodedPayload;
    }

    public DataType getDataType() {
        return DataType.BulkString;
    }

    public String getUnencodedPayload() {
        return unencodedPayload;
    }

}