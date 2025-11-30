package dev.masonak.redis.commandtest;

import dev.masonak.redis.testutil.BaseRedisTest;
import dev.masonak.redis.testutil.RedisClient;
import dev.masonak.redis.testutil.resp.RESPEncoder;
import dev.masonak.redis.testutil.resp.SimpleError;
import dev.masonak.redis.testutil.resp.SimpleString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeCommandTest extends BaseRedisTest {

    @Test
    void testTypeCommand() throws Exception {
        try (RedisClient client = new RedisClient(port)) {
            String response = client.sendCommand("TYPE");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("TYPE")), response);

            response = client.sendCommand("TYPE", "key", "extra");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("TYPE")), response);

            response = client.sendCommand("TYPE", "nonexistent");
            assertEquals(RESPEncoder.encode(new SimpleString("none")), response);

            client.sendCommand("SET", "string_key", "value");
            response = client.sendCommand("TYPE", "string_key");
            assertEquals(RESPEncoder.encode(new SimpleString("string")), response);

            client.sendCommand("LPUSH", "list_key", "item");
            response = client.sendCommand("TYPE", "list_key");
            assertEquals(RESPEncoder.encode(new SimpleString("list")), response);
        }
    }

}
