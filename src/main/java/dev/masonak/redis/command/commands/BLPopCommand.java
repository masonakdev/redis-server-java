package dev.masonak.redis.command.commands;

import dev.masonak.redis.command.Command;
import dev.masonak.redis.command.CommandContext;
import dev.masonak.redis.command.Dispatcher;
import dev.masonak.redis.concurrency.Waiter;
import dev.masonak.redis.core.RedisDatabase;
import dev.masonak.redis.core.value.ListValue;
import dev.masonak.redis.core.value.Value;
import dev.masonak.redis.resp.datatype.*;
import dev.masonak.redis.resp.protocol.RESPParser;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class BLPopCommand implements Command {

    @Override
    public RESPValue execute(RedisDatabase db, List<String> args, CommandContext context) {
        if (args.size() == 2) {
            double timeout;
            try {
                timeout = Double.parseDouble(args.get(1));
            } catch (Exception e) {
                return SimpleError.syntax("syntax is BLPOP <list_key> <timeout(seconds)>");
            }
            Waiter waiter = new Waiter();
            db.blockListKey(args.getFirst(), waiter);
            System.out.println("[DEBUG] Blocking on key: " + args.getFirst() + " for " + timeout + " seconds");

                    long endTime = System.currentTimeMillis() + (long) (timeout * 1000L);

                    while (true) {
                        Value value = db.get(args.getFirst());
                        if (value instanceof ListValue listValue && !listValue.getDeque().isEmpty()) {
                            RArray response = new RArray();
                            response.add(new BulkString(args.getFirst()));
                            String polledVal = listValue.pollFirst();
                            System.out.println("[DEBUG] Unblocking client for key=" + args.getFirst() + " with value=" + polledVal);
                            response.add(new BulkString(polledVal));
                            if (polledVal.isEmpty()) return null;
                            return response;
                        }

                        long waitTime = timeout == 0 ? 0 : endTime - System.currentTimeMillis();
                        if (waitTime <= 0 && timeout != 0) {
                            return new RArray(true); // null array for timeout
                        }

                        waiter.doWait(waitTime);
                    }
        }
        return SimpleError.syntax("syntax is BLPOP <list_key> <timeout(seconds)>");
    }

}