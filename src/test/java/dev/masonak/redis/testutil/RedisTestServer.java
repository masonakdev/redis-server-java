package dev.masonak.redis.testutil;

import dev.masonak.redis.testutil.misc.Logging;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class RedisTestServer implements AutoCloseable {

    private final Process process;

    public RedisTestServer(String scriptPath) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(scriptPath);
        pb.redirectErrorStream(true);
        pb.directory(new File("."));
        process = pb.start();

        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while((line = reader.readLine()) != null) {
                    System.out.println(Logging.PREFIX + line);
                }
            } catch (IOException e) {
                System.out.println(Logging.ERROR_PREFIX + e.getMessage());
            }
        }).start();

        waitForServerReady(6379, 20000);
    }

    private static void waitForServerReady(int port, int timeoutMs) throws Exception {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMs) {
            try (Socket socket = new Socket("127.0.0.1", port)) {
                return;
            } catch (IOException e) {
                Thread.sleep(100);
            }
        }
        throw new IOException("Server did not start listening on port " + port);
    }

    @Override
    public void close() throws Exception {
        if (process != null && process.isAlive()) process.destroy();
    }

}