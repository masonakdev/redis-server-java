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

public class BLPopCommandTest extends BaseRedisTest {

    @Test
    void testBLPOPConcurrency() throws Exception {
        String key = "queue";
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(1);

        AtomicReference<String> firstClientResponse = new AtomicReference<>();
        AtomicReference<String> secondClientResponse = new AtomicReference<>();

        Future<?> client1 = executor.submit(() -> {
            try (RedisClient c1 = new RedisClient(port)) {
                System.out.println(Logging.PREFIX + ANSIColor.GREEN + "[Client1][" + Instant.now() + "]"
                        + ANSIColor.RESET + " Sending BLPOP");
                latch.countDown();
                String resp = c1.sendCommand("BLPOP", key, "0");
                System.out.println(Logging.PREFIX + ANSIColor.GREEN + "[Client1][" + Instant.now() + "]"
                        + ANSIColor.RESET + " Received: " + resp);
                firstClientResponse.set(resp);
            } catch (Exception e) {
                System.out.println(Logging.ERROR_PREFIX + "Exception for client one.");
            }
        });

        Future<?> client2 = executor.submit(() -> {
            try {
                latch.await();
                Thread.sleep(50);
                try (RedisClient c2 = new RedisClient(port)) {
                    System.out.println(Logging.PREFIX + ANSIColor.GREEN + "[Client2][" + Instant.now() + "]"
                            + ANSIColor.RESET + " Sending BLPOP");
                    String resp = c2.sendCommand("BLPOP", key, "0");
                    System.out.println(Logging.PREFIX + ANSIColor.GREEN + "[Client2][" + Instant.now() + "]"
                            + ANSIColor.RESET + " Received: " + resp);
                    secondClientResponse.set(resp);
                }
            } catch (Exception e) {
                System.out.println(Logging.ERROR_PREFIX + "Exception for client two.");
            }
        });

        Future<?> pusher = executor.submit(() -> {
            try (RedisClient c3 = new RedisClient(port)) {
                Thread.sleep(500);
                System.out.println(Logging.PREFIX + ANSIColor.GREEN + "[Client3][" + Instant.now() + "]"
                        + ANSIColor.RESET + " LPUSHing value");
                c3.sendCommand("LPUSH", key, "hello");
            } catch (Exception e) {
                System.out.println(Logging.ERROR_PREFIX + "Exception for client three during `LPUSH`.");
            }
        });

        pusher.get(5, TimeUnit.SECONDS);
        client1.get(5, TimeUnit.SECONDS);

        assertThat(firstClientResponse.get()).as("First client should have a BLPOP response").isNotNull();
        assertThat(firstClientResponse.get()).contains("hello");

        assertThat(secondClientResponse.get()).as("Second client should still be blocking").isNull();

        executor.shutdownNow();
    }

}