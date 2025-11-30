package dev.masonak.redis.commandtest;

import dev.masonak.redis.testutil.BaseRedisTest;
import dev.masonak.redis.testutil.RedisClient;
import dev.masonak.redis.testutil.resp.RESPEncoder;
import dev.masonak.redis.testutil.resp.SimpleError;
import dev.masonak.redis.testutil.resp.RArray;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XRangeCommandTest extends BaseRedisTest {

    @Test
    void testXRangeCommand() throws Exception {
        try (RedisClient client = new RedisClient(port)) {
            String response = client.sendCommand("XRANGE");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("XRANGE")), response);

            response = client.sendCommand("XRANGE", "key", "start");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("XRANGE")), response);

            response = client.sendCommand("XRANGE", "key", "start", "end", "count");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("XRANGE")), response);

            response = client.sendCommand("XRANGE", "nonexistent", "-", "+");
            assertEquals(RESPEncoder.encode(new RArray()), response);

            client.sendCommand("XADD", "mystream", "1000-1", "field1", "value1");
            client.sendCommand("XADD", "mystream", "1000-2", "field2", "value2");
            client.sendCommand("XADD", "mystream", "1001-1", "field3", "value3");

            response = client.sendCommand("XRANGE", "mystream", "-", "+");
            assertTrue(response.contains("1000-1"));
            assertTrue(response.contains("1000-2"));
            assertTrue(response.contains("1001-1"));
            assertTrue(response.contains("field1"));
            assertTrue(response.contains("value1"));

            response = client.sendCommand("XRANGE", "mystream", "1000-1", "1000-2");
            assertTrue(response.contains("1000-1"));
            assertTrue(response.contains("1000-2"));

            String[] parts = response.split("\r\n");
            boolean contains1001 = response.contains("1001-1");
            assertEquals(false, contains1001);

            client.sendCommand("SET", "string_key", "value");
            response = client.sendCommand("XRANGE", "string_key", "-", "+");
            assertEquals(
                    RESPEncoder.encode(
                            new SimpleError("the specified key does not exist or is not associated with a stream")),
                    response);
        }
    }

}
