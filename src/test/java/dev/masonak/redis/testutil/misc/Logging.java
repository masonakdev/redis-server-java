package dev.masonak.redis.testutil.misc;

public class Logging {

    public static final String PREFIX = ANSIColor.CYAN + "[RedisTestServer]: " + ANSIColor.RESET;
    public static final String ERROR_PREFIX = PREFIX + ANSIColor.RED + "(ERROR) " + ANSIColor.RESET;
    public static final String WARN_PREFIX = PREFIX + ANSIColor.YELLOW + "(WARN) " + ANSIColor.RESET;
    public static final String DEBUG_PREFIX = PREFIX + ANSIColor.WHITE + "(DEBUG) " + ANSIColor.RESET;

}