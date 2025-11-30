package dev.masonak.redis.commandtest;

import dev.masonak.redis.testutil.BaseRedisTest;
import dev.masonak.redis.testutil.RedisClient;
import dev.masonak.redis.testutil.resp.RESPEncoder;
import dev.masonak.redis.testutil.resp.SimpleError;
import dev.masonak.redis.testutil.resp.BulkString;
import dev.masonak.redis.testutil.resp.RArray;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LRangeCommandTest extends BaseRedisTest {

    @Test
    void testLRangeCommand() throws Exception {
        try (RedisClient client = new RedisClient(port)) {
            String response = client.sendCommand("LRANGE");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("LRANGE")), response);

            response = client.sendCommand("LRANGE", "key");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("LRANGE")), response);

            response = client.sendCommand("LRANGE", "key", "0");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("LRANGE")), response);

            response = client.sendCommand("LRANGE", "nonexistent", "a", "b");
            assertEquals(RESPEncoder.encode(new RArray()), response);

            client.sendCommand("LPUSH", "syntax_test", "1");
            response = client.sendCommand("LRANGE", "syntax_test", "a", "b");
            assertEquals(
                    RESPEncoder.encode(SimpleError.syntax("syntax is LRANGE <list_key> <start_index> <stop_index>")),
                    response);

            response = client.sendCommand("LRANGE", "nonexistent", "0", "-1");
            assertEquals(RESPEncoder.encode(new RArray()), response);

            client.sendCommand("LPUSH", "mylist", "c");
            client.sendCommand("LPUSH", "mylist", "b");
            client.sendCommand("LPUSH", "mylist", "a");

            response = client.sendCommand("LRANGE", "mylist", "0", "-1");
            RArray expected = new RArray();
            expected.add(new BulkString("a"));
            expected.add(new BulkString("b"));
            expected.add(new BulkString("c"));
            assertEquals(RESPEncoder.encode(expected), response);

            response = client.sendCommand("LRANGE", "mylist", "0", "1");
            expected = new RArray();
            expected.add(new BulkString("a"));
            expected.add(new BulkString("b"));
            assertEquals(RESPEncoder.encode(expected), response);

            response = client.sendCommand("LRANGE", "mylist", "10", "20");
            assertEquals(RESPEncoder.encode(new RArray()), response);

            client.sendCommand("SET", "mystring", "value");
            response = client.sendCommand("LRANGE", "mystring", "0", "-1");
            assertEquals(
                    RESPEncoder.encode(
                            SimpleError.customError("the value associated with the key `mystring` is not a list")),
                    response);
        }
    }

}