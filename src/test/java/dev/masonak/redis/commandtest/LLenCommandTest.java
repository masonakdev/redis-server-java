package dev.masonak.redis.commandtest;

import dev.masonak.redis.testutil.BaseRedisTest;
import dev.masonak.redis.testutil.RedisClient;
import dev.masonak.redis.testutil.resp.RESPEncoder;
import dev.masonak.redis.testutil.resp.SimpleError;
import dev.masonak.redis.testutil.resp.RInteger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LLenCommandTest extends BaseRedisTest {

    @Test
    void testLLenCommand() throws Exception {
        try (RedisClient client = new RedisClient(port)) {
            String response = client.sendCommand("LLEN");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("LLEN")), response);

            response = client.sendCommand("LLEN", "key1", "key2");
            assertEquals(RESPEncoder.encode(SimpleError.syntax("syntax is LLEN <list_key>")), response);

            response = client.sendCommand("LLEN", "nonexistent");
            assertEquals(RESPEncoder.encode(new RInteger(0)), response);

            client.sendCommand("LPUSH", "mylist", "item1");
            client.sendCommand("LPUSH", "mylist", "item2");
            response = client.sendCommand("LLEN", "mylist");
            assertEquals(RESPEncoder.encode(new RInteger(2)), response);

            client.sendCommand("SET", "mystring", "value");
            response = client.sendCommand("LLEN", "mystring");
            assertEquals(
                    RESPEncoder.encode(
                            SimpleError.customError("the value associated with the key `mystring` is not a list")),
                    response);
        }
    }

}