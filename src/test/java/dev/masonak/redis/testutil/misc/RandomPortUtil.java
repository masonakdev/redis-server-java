package dev.masonak.redis.testutil.misc;

import java.net.ServerSocket;

public class RandomPortUtil {

    public Integer getRandomPort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            System.out.println(Logging.DEBUG_PREFIX + "Random port " + socket.getLocalPort() + " generated.");
            return socket.getLocalPort();
        } catch (Exception e) {
            System.out.println(Logging.ERROR_PREFIX + "Exception while attempting to generate a random port");
            System.out.println(Logging.ERROR_PREFIX + e.getMessage());
        }
        return null;
    }

}