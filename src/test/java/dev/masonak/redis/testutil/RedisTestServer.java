package dev.masonak.redis.testutil;

import dev.masonak.redis.testutil.misc.Logging;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;

public class RedisTestServer implements AutoCloseable {

    private final Process scriptProcess;
    private final Process cmdProcess;

    public RedisTestServer(String scriptPath, int port) throws Exception {
        ProcessBuilder scriptProcessBuilder = new ProcessBuilder(scriptPath);
        scriptProcessBuilder.redirectErrorStream(true);
        scriptProcessBuilder.directory(new File("."));
        scriptProcess = scriptProcessBuilder.start();

        String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = "dev.masonak.redis.RedisServer";

        String[] cmdArgs = new String[] { javaBin, "-cp", classpath, className, Integer.toString(port) };
        System.out.println(Logging.DEBUG_PREFIX + "Executing: " + Arrays.toString(cmdArgs));
        cmdProcess = new ProcessBuilder(cmdArgs).start();

        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(Logging.PREFIX + line);
                }
            } catch (IOException e) {
                System.out.println(Logging.ERROR_PREFIX + e.getMessage());
            }
        }).start();

        waitForServerReady(port, 20000);
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
        if (cmdProcess != null && cmdProcess.isAlive())
            cmdProcess.destroy();
    }

}