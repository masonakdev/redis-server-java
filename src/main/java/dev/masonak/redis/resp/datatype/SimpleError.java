package dev.masonak.redis.resp.datatype;

public final class SimpleError implements RESPValue {

    private final String unencodedPayload;
    private String prefix = "-ERR";

    public SimpleError() {
        this.unencodedPayload = "";
    }

    public SimpleError(String unencodedPayload) {
        this.unencodedPayload = unencodedPayload;
    }

    public SimpleError(String prefix, String unencodedPayload) {
        this.prefix = prefix;
        this.unencodedPayload = unencodedPayload;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public static SimpleError customError(String message) {
        return new SimpleError(message);
    }

    public static SimpleError syntax(String message) {
        return new SimpleError(message);
    }

    public static SimpleError unknownCommand(String commandName) {
        return new SimpleError("unkown command `" + commandName + "`");
    }

    public static SimpleError wrongArity(String commandName) {
        return new SimpleError("wrong number of arguments for `" + commandName + "`");
    }

    public SimpleError literallyHow() {
        return new SimpleError("I literally have no idea how you got to this error. Well done. Email me@masonak.dev or submit an issue.");
    }

    public DataType getDataType() {
        return DataType.SimpleError;
    }

    public String getUnencodedPayload() {
        return unencodedPayload;
    }

}