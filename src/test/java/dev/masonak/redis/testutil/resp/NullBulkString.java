package dev.masonak.redis.testutil.resp;

public final class NullBulkString implements RESPValue {

    public NullBulkString() {}

    @Override
    public DataType getDataType() {
        return DataType.NullBulkString;
    }

    @Override
    public String getUnencodedPayload() {
        return "-1";
    }
}