package dev.masonak.redis.commandtest;

import dev.masonak.redis.testutil.BaseRedisTest;
import dev.masonak.redis.testutil.RedisClient;
import dev.masonak.redis.testutil.resp.RESPEncoder;
import dev.masonak.redis.testutil.resp.SimpleString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EchoCommandTest extends BaseRedisTest {

    @Test
    void testEchoCommand() throws Exception {
        try (RedisClient client = new RedisClient(port)) {
            String response = client.sendCommand("ECHO", "hello");
            assertEquals(RESPEncoder.encode(new SimpleString("hello")), response);
        }
    }

}
