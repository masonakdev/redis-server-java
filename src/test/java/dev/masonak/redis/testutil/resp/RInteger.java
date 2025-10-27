package dev.masonak.redis.testutil.resp;

public final class RInteger implements RESPValue {

    private static final String DELIMITER = "\r\n";
    private static final String PREFIX = ":";

    private final String unencodedPayload;

    public RInteger(Integer unencodedPayload) {
        this.unencodedPayload = Integer.toString(unencodedPayload);
    }

    public DataType getDataType() {
        return DataType.RInteger;
    }

    public String getUnencodedPayload() {
        return unencodedPayload;
    }

}