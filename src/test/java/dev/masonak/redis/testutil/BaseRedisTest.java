package dev.masonak.redis.testutil;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseRedisTest {

    protected static RedisTestServer server;

    @BeforeAll
    static void startServer() throws Exception {
        if (server == null) {
            server = new RedisTestServer("./redis-server-java.sh");
        }
    }

    @AfterAll
    static void stopServer() throws Exception {
        if (server != null) {
            server.close();
        }
    }

}