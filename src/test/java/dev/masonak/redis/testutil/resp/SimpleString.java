package dev.masonak.redis.testutil.resp;

public final class SimpleString implements RESPValue {

    public static final SimpleString OK = new SimpleString("OK");

    private final String unencodedPayload;

    public SimpleString(String unencodedPayload) {
        this.unencodedPayload = unencodedPayload;
    }

    public DataType getDataType() {
        return DataType.SimpleString;
    }

    public String getUnencodedPayload() {
        return unencodedPayload;
    }

}