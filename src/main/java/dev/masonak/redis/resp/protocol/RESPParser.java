package dev.masonak.redis.resp.protocol;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RESPParser implements AutoCloseable {

    private final BufferedInputStream input;

    public RESPParser(InputStream input) {
        this.input = new BufferedInputStream(input);
    }

    public List<String> parseArray() throws IOException {
        int marker = input.read(); // Should be '*'
        if (marker == -1) {
            return null;
        } else if (marker != '*') {
            throw new IOException("Expected array marker '*', got: " + (char) marker);
        }

        int count = Integer.parseInt(readLine()); // Number of elements
        List<String> elements = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            elements.add(parseElement());
        }

        return elements;
    }

    private String parseElement() throws IOException {
        int marker = input.read();

        switch (marker) {
            case '$': // Bulk String
                return parseBulkString();
            case '+': // Simple String
                return readLine();
            case ':': // Integer
                return readLine();
            case '-': // Error
                throw new IOException("RESP error: " + readLine());
            default:
                throw new IOException("Unknown RESP type: " + (char) marker);
        }
    }

    private String parseBulkString() throws IOException {
        int length = Integer.parseInt(readLine());

        if (length == -1) return null;

        byte[] bytes = new byte[length];
        int read = input.read(bytes);
        if (read != length) {
            throw new IOException("Incomplete bulk string data.");
        }

        if (input.read() != '\r' || input.read() != '\n') {
            throw new IOException("Bulk string missing CRLF.");
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }

    private String readLine() throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        int b;
        while ((b = input.read()) != -1) {
            if (b == '\r') {
                int next = input.read();
                if (next == '\n') {
                    break;
                } else {
                    throw new IOException("Malformed line ending.");
                }
            }
            line.write(b);
        }
        if (b == -1) {
            throw new IOException("Unexpected end of stream.");
        }
        return line.toString(StandardCharsets.UTF_8);
    }

    @Override
    public void close() throws Exception {

    }
}