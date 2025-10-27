package dev.masonak.redis.testutil.resp;

public final class NoResponse implements RESPValue {

    private final DataType dataType = DataType.NoResponse;

    @Override
    public byte[] bytes() {
        return null;
    }

    @Override
    public String string() { return null; }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    @Override
    public String getUnencodedPayload() {
        return null;
    }
}