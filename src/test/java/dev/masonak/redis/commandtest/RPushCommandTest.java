package dev.masonak.redis.commandtest;

import dev.masonak.redis.testutil.BaseRedisTest;
import dev.masonak.redis.testutil.RedisClient;
import dev.masonak.redis.testutil.resp.RESPEncoder;
import dev.masonak.redis.testutil.resp.SimpleError;
import dev.masonak.redis.testutil.resp.RInteger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RPushCommandTest extends BaseRedisTest {

    @Test
    void testRPushCommand() throws Exception {
        try (RedisClient client = new RedisClient(port)) {
            String response = client.sendCommand("RPUSH");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("RPUSH")), response);

            response = client.sendCommand("RPUSH", "key1");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("RPUSH")), response);

            response = client.sendCommand("RPUSH", "mylist", "item1");
            assertEquals(RESPEncoder.encode(new RInteger(1)), response);

            response = client.sendCommand("RPUSH", "mylist", "item2");
            assertEquals(RESPEncoder.encode(new RInteger(2)), response);

            response = client.sendCommand("RPUSH", "mylist", "item3", "item4");
            assertEquals(RESPEncoder.encode(new RInteger(4)), response);

            client.sendCommand("SET", "mystring", "value");
            response = client.sendCommand("RPUSH", "mystring", "item");
            assertEquals(
                    RESPEncoder.encode(
                            SimpleError.customError("the value associated with the key `mystring` is not a list")),
                    response);
        }
    }

}
