package dev.masonak.redis.commandtest;

import dev.masonak.redis.testutil.BaseRedisTest;
import dev.masonak.redis.testutil.RedisClient;
import dev.masonak.redis.testutil.resp.RESPEncoder;
import dev.masonak.redis.testutil.resp.SimpleError;
import dev.masonak.redis.testutil.resp.SimpleString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SetCommandTest extends BaseRedisTest {

    @Test
    void testSetCommand() throws Exception {
        try (RedisClient client = new RedisClient(port)) {
            String response = client.sendCommand("SET");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("SET")), response);

            response = client.sendCommand("SET", "key");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("SET")), response);

            response = client.sendCommand("SET", "key", "value");
            assertEquals(RESPEncoder.encode(SimpleString.OK), response);

            response = client.sendCommand("SET", "key_ex", "value", "EX", "10");
            assertEquals(RESPEncoder.encode(SimpleString.OK), response);

            response = client.sendCommand("SET", "key_px", "value", "PX", "10000");
            assertEquals(RESPEncoder.encode(SimpleString.OK), response);

            response = client.sendCommand("SET", "key", "value", "EX", "invalid");
            assertEquals(RESPEncoder.encode(SimpleError.syntax("value is not an integer or out of range")), response);

            response = client.sendCommand("SET", "key", "value", "EX", "-1");
            assertEquals(RESPEncoder.encode(SimpleError.syntax("value is not an integer or out of range")), response);

            response = client.sendCommand("SET", "key", "value", "XX");
            assertEquals(
                    RESPEncoder
                            .encode(SimpleError.syntax("syntax is `SET <key> <value> [EX seconds|PX milliseconds]`")),
                    response);

            response = client.sendCommand("SET", "key", "value", "EX", "10", "extra");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("SET")), response);
        }
    }

}
