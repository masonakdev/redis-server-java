package dev.masonak.redis.testutil;

import dev.masonak.redis.testutil.misc.RandomPortUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseRedisTest {

    protected RedisTestServer server;
    protected int port;

    @BeforeEach
    void startServer() throws Exception {
        RandomPortUtil randomPortUtil = new RandomPortUtil();
        this.port = randomPortUtil.getRandomPort();
        if (server == null) {
            server = new RedisTestServer("./test-redis-server-java.sh", port);
        }
    }

    @AfterEach
    void stopServer() throws Exception {
        if (server != null) {
            server.close();
        }
        server = null;
    }

}