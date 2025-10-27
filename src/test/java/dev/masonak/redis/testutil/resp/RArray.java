package dev.masonak.redis.testutil.resp;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class RArray implements RESPValue {

    private List<RESPValue> respValueArray;
    private boolean isNull = false;

    public RArray() {
        this.respValueArray = new ArrayList<>();
    }

    public RArray(boolean isNull) {
        this.respValueArray = new ArrayList<>();
        this.isNull = true;
    }

    @Override
    public byte[] bytes() {
        return RESPEncoder.encodeRArray(respValueArray, isNull).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String string() { return RESPEncoder.encodeRArray(respValueArray, isNull); }

    @Override
    public DataType getDataType() {
        return DataType.RArray;
    }

    @Deprecated
    @Override
    public String getUnencodedPayload() {
        return "";
    }

    public void add(RESPValue respValue) {
        respValueArray.add(respValue);
    }

    public int size() {
        return respValueArray.size();
    }

    public boolean isNull() { return isNull; }

    public List<RESPValue> getList() {
        return respValueArray;
    }

    @Override
    public String toString() {
        return RESPEncoder.encodeRArray(respValueArray, isNull);
    }

    public boolean containsDataType(DataType dt) {
        for (RESPValue rV : respValueArray) {
            if (rV.getDataType() == dt) {
                return true;
            }
        }
        return false;
    }

}