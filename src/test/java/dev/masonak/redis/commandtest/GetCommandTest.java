package dev.masonak.redis.commandtest;

import dev.masonak.redis.testutil.BaseRedisTest;
import dev.masonak.redis.testutil.RedisClient;
import dev.masonak.redis.testutil.resp.RESPEncoder;
import dev.masonak.redis.testutil.resp.SimpleError;
import dev.masonak.redis.testutil.resp.BulkString;
import dev.masonak.redis.testutil.resp.NullBulkString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetCommandTest extends BaseRedisTest {

    @Test
    void testGetCommand() throws Exception {
        try (RedisClient client = new RedisClient(port)) {
            String response = client.sendCommand("GET");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("GET")), response);

            response = client.sendCommand("GET", "key1", "key2");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("GET")), response);

            response = client.sendCommand("GET", "nonexistent");
            assertEquals(RESPEncoder.encode(new NullBulkString()), response);

            client.sendCommand("SET", "mykey", "myvalue");
            response = client.sendCommand("GET", "mykey");
            assertEquals(RESPEncoder.encode(new BulkString("myvalue")), response);

            client.sendCommand("LPUSH", "mylist", "item");
            response = client.sendCommand("GET", "mylist");
            assertEquals(
                    RESPEncoder.encode(SimpleError
                            .customError("WRONGTYPE Operation against a key holding the wrong kind of value")),
                    response);
        }
    }

}