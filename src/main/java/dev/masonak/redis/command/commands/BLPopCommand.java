package dev.masonak.redis.command.commands;

import dev.masonak.redis.command.Command;
import dev.masonak.redis.command.CommandContext;
import dev.masonak.redis.concurrency.Waiter;
import dev.masonak.redis.core.RedisDatabase;
import dev.masonak.redis.core.value.ListValue;
import dev.masonak.redis.core.value.Value;
import dev.masonak.redis.resp.datatype.*;

import java.util.List;

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

            try {
                long endTime = System.currentTimeMillis() + (long) (timeout * 1000L);

                while (true) {
                    Value value = db.get(args.getFirst());
                    if (value instanceof ListValue listValue && !listValue.getDeque().isEmpty()) {
                        RArray response = new RArray();
                        response.add(new BulkString(args.getFirst()));
                        String polledVal = listValue.pollFirst();
                        System.out.println(
                                "[DEBUG] Unblocking client for key=" + args.getFirst() + " with value=" + polledVal);
                        if (polledVal.isEmpty()) {
                            // Should not happen if !isEmpty() check passed, but handle it
                            System.out.println("[ERROR] Polled value is empty but list was not empty!");
                            return SimpleError.customError("Internal error: list empty after check");
                        }
                        response.add(new BulkString(polledVal));
                        return response;
                    }

                    long waitTime = timeout == 0 ? 0 : endTime - System.currentTimeMillis();
                    if (waitTime <= 0 && timeout != 0) {
                        return new RArray(true); // null array for timeout
                    }

                    waiter.doWait(waitTime);
                }
            } finally {
                // Ensure waiter is removed to prevent memory leaks and phantom notifications
                db.unblockListKey(args.getFirst(), waiter);
            }
        }
        return SimpleError.syntax("syntax is BLPOP <list_key> <timeout(seconds)>");
    }

}