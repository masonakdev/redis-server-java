package dev.masonak.redis.testutil;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RedisClient implements Closeable {

    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;

    public RedisClient() throws IOException {
        this.socket = new Socket("127.0.0.1", 6379);
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
    }

    public String sendCommand(String... parts) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("*").append(parts.length).append("\r\n");
        for (String part : parts) {
            sb.append("$").append(part.length()).append("\r\n").append(part).append("\r\n");
        }
        out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        out.flush();

        ByteArrayOutputStream resp = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int read = in.read(buf);
        if (read > 0) resp.write(buf, 0, read);
        return resp.toString(StandardCharsets.UTF_8);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

}