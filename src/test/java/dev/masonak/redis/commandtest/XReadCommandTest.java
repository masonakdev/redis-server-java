package dev.masonak.redis.commandtest;

import dev.masonak.redis.testutil.BaseRedisTest;
import dev.masonak.redis.testutil.RedisClient;
import dev.masonak.redis.testutil.resp.RESPEncoder;
import dev.masonak.redis.testutil.resp.SimpleError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XReadCommandTest extends BaseRedisTest {

    @Test
    void testXReadCommand() throws Exception {
        try (RedisClient client = new RedisClient(port)) {
            String response = client.sendCommand("XREAD");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("XREAD")), response);

            response = client.sendCommand("XREAD", "STREAMS", "key");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("XREAD")), response);

            response = client.sendCommand("XREAD", "WRONG", "key", "0-0");
            assertEquals(RESPEncoder.encode(SimpleError.syntax("syntax is XREAD <type> <keys> <start_ids>(exclusive)")),
                    response);

            response = client.sendCommand("XREAD", "STREAMS", "nonexistent", "0-0");
            assertEquals(RESPEncoder.encode(SimpleError.syntax("syntax is XREAD <type> <keys> <start_ids>(exclusive)")),
                    response);

            client.sendCommand("XADD", "mystream", "1000-1", "field1", "value1");
            client.sendCommand("XADD", "mystream", "1000-2", "field2", "value2");

            response = client.sendCommand("XREAD", "STREAMS", "mystream", "0-0");
            assertTrue(response.contains("mystream"));
            assertTrue(response.contains("1000-1"));
            assertTrue(response.contains("1000-2"));
            assertTrue(response.contains("field1"));
            assertTrue(response.contains("value1"));

            response = client.sendCommand("XREAD", "STREAMS", "mystream", "1000-1");
            assertTrue(response.contains("mystream"));
            assertTrue(response.contains("1000-2"));
            assertTrue(response.contains("1000-1"));

            client.sendCommand("XADD", "stream2", "2000-1", "f", "v");
            response = client.sendCommand("XREAD", "STREAMS", "mystream", "stream2", "0-0", "0-0");
            assertTrue(response.contains("mystream"));
            assertTrue(response.contains("stream2"));
            assertTrue(response.contains("1000-1"));
            assertTrue(response.contains("2000-1"));

            client.sendCommand("SET", "string_key", "value");
            response = client.sendCommand("XREAD", "STREAMS", "string_key", "0-0");
            assertEquals(RESPEncoder.encode(SimpleError.syntax("syntax is XREAD <type> <keys> <start_ids>(exclusive)")),
                    response);
        }
    }

}
