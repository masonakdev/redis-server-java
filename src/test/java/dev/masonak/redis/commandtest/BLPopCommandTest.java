package dev.masonak.redis.commandtest;

import dev.masonak.redis.testutil.BaseRedisTest;
import dev.masonak.redis.testutil.RedisClient;
import dev.masonak.redis.testutil.misc.ANSIColor;
import dev.masonak.redis.testutil.misc.Logging;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Concurrency test for BLPOP behavior.
 *
 * Scenario:
 *  - Two clients issue BLPOP on the same key (blocking indefinitely).
 *  - A third client LPUSHes a value into the key.
 *  - The first blocked client should receive a response.
 *  - The second should remain blocked.
 */
public class BLPopCommandTest extends BaseRedisTest {

    @Test
    void testBLPOPConcurrency() throws Exception {
        String key = "queue";
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(1);

        AtomicReference<String> firstClientResponse = new AtomicReference<>();
        AtomicReference<String> secondClientResponse = new AtomicReference<>();

        // Client 1: blocks first
        Future<?> client1 = executor.submit(() -> {
            try (RedisClient c1 = new RedisClient()) {
                System.out.println(Logging.PREFIX + ANSIColor.GREEN + "[Client1][" + Instant.now() + "]" + ANSIColor.RESET + " Sending BLPOP");
                latch.countDown(); // signal client2 can start
                String resp = c1.sendCommand("BLPOP", key, "0"); // block indefinitely
                System.out.println(Logging.PREFIX + ANSIColor.GREEN + "[Client1][" + Instant.now() + "]" + ANSIColor.RESET + " Received: " + resp);
                firstClientResponse.set(resp);
            } catch (Exception e) {
                System.out.println(Logging.ERROR_PREFIX + "Exception for client one.");
            }
        });

        // Client 2: blocks slightly later
        Future<?> client2 = executor.submit(() -> {
            try {
                latch.await(); // wait until client1 sent BLPOP
                Thread.sleep(50); // ensure client1 is first
                try (RedisClient c2 = new RedisClient()) {
                    System.out.println(Logging.PREFIX + ANSIColor.GREEN + "[Client2][" + Instant.now() + "]" + ANSIColor.RESET + " Sending BLPOP");
                    String resp = c2.sendCommand("BLPOP", key, "0");
                    System.out.println(Logging.PREFIX + ANSIColor.GREEN + "[Client2][" + Instant.now() + "]" + ANSIColor.RESET + " Received: " + resp);
                    secondClientResponse.set(resp);
                }
            } catch (Exception e) {
                System.out.println(Logging.ERROR_PREFIX + "Exception for client two.");
            }
        });

        // Client 3: pushes value after small delay
        Future<?> pusher = executor.submit(() -> {
            try (RedisClient c3 = new RedisClient()) {
                Thread.sleep(500);
                System.out.println(Logging.PREFIX + ANSIColor.GREEN + "[Client3][" + Instant.now() + "]" + ANSIColor.RESET + " LPUSHing value");
                c3.sendCommand("LPUSH", key, "hello");
            } catch (Exception e) {
                System.out.println(Logging.ERROR_PREFIX + "Exception for client three during `LPUSH`.");
            }
        });

        // Wait a bit to allow LPUSH to trigger one response
        pusher.get(5, TimeUnit.SECONDS);
        client1.get(5, TimeUnit.SECONDS);

        // Now verify only client 1 received something
        assertThat(firstClientResponse.get()).as("First client should have a BLPOP response").isNotNull();
        assertThat(firstClientResponse.get()).contains("hello");

        // Client 2 should still be blocked
        assertThat(secondClientResponse.get()).as("Second client should still be blocking").isNull();

        executor.shutdownNow();
    }
}
