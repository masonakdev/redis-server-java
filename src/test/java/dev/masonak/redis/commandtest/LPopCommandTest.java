package dev.masonak.redis.commandtest;

import dev.masonak.redis.testutil.BaseRedisTest;
import dev.masonak.redis.testutil.RedisClient;
import dev.masonak.redis.testutil.resp.RESPEncoder;
import dev.masonak.redis.testutil.resp.SimpleError;
import dev.masonak.redis.testutil.resp.BulkString;
import dev.masonak.redis.testutil.resp.NullBulkString;
import dev.masonak.redis.testutil.resp.RArray;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LPopCommandTest extends BaseRedisTest {

    @Test
    void testLPopCommand() throws Exception {
        try (RedisClient client = new RedisClient(port)) {
            String response = client.sendCommand("LPOP");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("LPOP")), response);

            response = client.sendCommand("LPOP", "key1", "key2", "key3");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("LPOP")), response);

            response = client.sendCommand("LPOP", "nonexistent");
            assertEquals(RESPEncoder.encode(new NullBulkString()), response);

            client.sendCommand("LPUSH", "mylist", "item1");
            client.sendCommand("LPUSH", "mylist", "item2");

            response = client.sendCommand("LPOP", "mylist");
            assertEquals(RESPEncoder.encode(new BulkString("item2")), response);

            response = client.sendCommand("LPOP", "mylist");
            assertEquals(RESPEncoder.encode(new BulkString("item1")), response);

            client.sendCommand("LPUSH", "mylist2", "a", "b", "c");
            response = client.sendCommand("LPOP", "mylist2", "2");
            RArray expected = new RArray();
            expected.add(new BulkString("c"));
            expected.add(new BulkString("b"));
            assertEquals(RESPEncoder.encode(expected), response);

            client.sendCommand("SET", "mystring", "value");
            response = client.sendCommand("LPOP", "mystring");
            assertEquals(
                    RESPEncoder.encode(
                            SimpleError.customError("the value associated with the key `mystring` is not a list")),
                    response);
        }
    }

}