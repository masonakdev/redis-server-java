package dev.masonak.redis.commandtest;

import dev.masonak.redis.testutil.BaseRedisTest;
import dev.masonak.redis.testutil.RedisClient;
import dev.masonak.redis.testutil.resp.RESPEncoder;
import dev.masonak.redis.testutil.resp.SimpleString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PingCommandTest extends BaseRedisTest {

    @Test
    void testPingCommand() throws Exception {
        try (RedisClient client = new RedisClient(port)) {
            String response = client.sendCommand("PING");
            assertEquals(RESPEncoder.encode(new SimpleString("PONG")), response);
        }
    }

}