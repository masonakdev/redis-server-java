package dev.masonak.redis.commandtest;

import dev.masonak.redis.testutil.BaseRedisTest;
import dev.masonak.redis.testutil.RedisClient;
import dev.masonak.redis.testutil.resp.RESPEncoder;
import dev.masonak.redis.testutil.resp.SimpleError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XAddCommandTest extends BaseRedisTest {

    @Test
    void testXAddCommand() throws Exception {
        try (RedisClient client = new RedisClient(port)) {
            String response = client.sendCommand("XADD");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("XADD")), response);

            response = client.sendCommand("XADD", "key", "*");
            assertEquals(RESPEncoder.encode(SimpleError.wrongArity("XADD")), response);

            response = client.sendCommand("XADD", "key", "*", "field");
            assertEquals(
                    RESPEncoder.encode(SimpleError.syntax(
                            "syntax is XADD <id> <stream_key> <value> (multiple key -> value mappings permitted)")),
                    response);

            response = client.sendCommand("XADD", "mystream", "*", "field1", "value1");
            String[] parts1 = response.split("\r\n");
            String id1 = parts1[1];
            assertTrue(id1.matches("\\d+-\\d+"));

            response = client.sendCommand("XADD", "mystream", "*", "field2", "value2");
            String[] parts2 = response.split("\r\n");
            String id2 = parts2[1];
            assertTrue(id2.compareTo(id1) > 0);

            response = client.sendCommand("XADD", "mystream", "0-0", "field", "value");
            assertEquals(RESPEncoder.encode(new SimpleError("The ID specified in XADD must be greater than 0-0")),
                    response);

            response = client.sendCommand("XADD", "mystream", "1-1", "field", "value");
            response = client.sendCommand("XADD", "mystream", "0-1", "field", "value");
            assertEquals(
                    RESPEncoder.encode(new SimpleError(
                            "The ID specified in XADD is equal or smaller than the target stream top item")),
                    response);

            client.sendCommand("SET", "string_key", "value");
            response = client.sendCommand("XADD", "string_key", "*", "field", "value");
            assertEquals(
                    RESPEncoder
                            .encode(new SimpleError("<stream_key> is already associated with a different value type")),
                    response);
        }
    }

}
