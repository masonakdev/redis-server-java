package dev.masonak.redis.commandtest;

import dev.masonak.redis.testutil.BaseRedisTest;
import dev.masonak.redis.testutil.RedisClient;
import dev.masonak.redis.testutil.resp.RESPEncoder;
import dev.masonak.redis.testutil.resp.SimpleError;
import dev.masonak.redis.testutil.resp.RInteger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LPushCommandTest extends BaseRedisTest {

    @Test
    void testLPushCommand() throws Exception {
        try (RedisClient client = new RedisClient(port)) {
            String response = client.sendCommand("LPUSH");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("LPUSH")), response);

            response = client.sendCommand("LPUSH", "key1");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("LPUSH")), response);

            response = client.sendCommand("LPUSH", "mylist", "item1");
            assertEquals(RESPEncoder.encode(new RInteger(1)), response);

            response = client.sendCommand("LPUSH", "mylist", "item2");
            assertEquals(RESPEncoder.encode(new RInteger(2)), response);

            response = client.sendCommand("LPUSH", "mylist", "item3", "item4");
            assertEquals(RESPEncoder.encode(new RInteger(4)), response);

            client.sendCommand("SET", "mystring", "value");
            response = client.sendCommand("LPUSH", "mystring", "item");
            assertEquals(
                    RESPEncoder.encode(
                            SimpleError.customError("the value associated with the key `mystring` is not a list")),
                    response);
        }
    }

}