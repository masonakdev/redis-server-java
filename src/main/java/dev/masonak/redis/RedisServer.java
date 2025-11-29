package dev.masonak.redis;

import dev.masonak.redis.command.CommandContext;
import dev.masonak.redis.command.CommandRegistry;
import dev.masonak.redis.command.DefaultCommands;
import dev.masonak.redis.command.Dispatcher;
import dev.masonak.redis.core.RedisDatabase;
import dev.masonak.redis.resp.protocol.RESPParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.List;

public class RedisServer {

    private static CommandRegistry commandRegistry;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 6379;

        commandRegistry = new CommandRegistry();
        DefaultCommands.registerAll(commandRegistry);

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            System.exit(-1);
        }

    }

    private static RedisDatabase db = new RedisDatabase();

    private static void handleClient(Socket clientSocket) {
        try (clientSocket;
                OutputStream outputStream = clientSocket.getOutputStream();
                InputStream in = clientSocket.getInputStream();
                RESPParser respParser = new RESPParser(in)) {

            Dispatcher dispatcher = new Dispatcher(commandRegistry, db);
            while (true) {
                List<String> parsedArray = respParser.parseArray();
                var ctx = new CommandContext(clientSocket, clientSocket.getRemoteSocketAddress(), Instant.now());
                byte[] output = dispatcher.dispatch(parsedArray, ctx);
                if (output != null) {
                    outputStream.write(output);
                    outputStream.flush();
                }
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

}