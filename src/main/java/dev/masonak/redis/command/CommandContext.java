package dev.masonak.redis.command;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.time.Instant;
import java.util.Optional;

public final class CommandContext {

    private final SocketAddress remoteAddress;
    private final Socket socket;
    private final Instant receivedAt;

    public CommandContext(Socket socket, SocketAddress remoteAddress, Instant receivedAt) {
        this.socket = socket;
        this.remoteAddress = remoteAddress;
        this.receivedAt = receivedAt;
    }

    public Optional<SocketAddress> remoteAddress() {
        return Optional.ofNullable(remoteAddress);
    }

    public Instant receivedAt() {
        return receivedAt;
    }

    public Socket getSocket() {
        return socket;
    }

}