package dev.masonak.redis.command;

import dev.masonak.redis.command.commands.*;

import java.util.Set;

public class DefaultCommands {

    public static void registerAll(CommandRegistry reg) {
        reg.register(new CommandMetadata("PING", 1, -1, Set.of(Flag.FAST, Flag.READ)), new PingCommand());
        reg.register(new CommandMetadata("ECHO", 2, -1, Set.of(Flag.FAST, Flag.READ)), new EchoCommand());
        reg.register(new CommandMetadata("SET", 3, 5, Set.of(Flag.WRITE)), new SetCommand());
        reg.register(new CommandMetadata("GET", 2, 2, Set.of(Flag.READ)), new GetCommand());
        reg.register(new CommandMetadata("RPUSH", 3, -1, Set.of(Flag.WRITE)), new RPushCommand());
        reg.register(new CommandMetadata("LPUSH", 3, -1, Set.of(Flag.WRITE)), new LPushCommand());
        reg.register(new CommandMetadata("LRANGE", 4, 4, Set.of(Flag.READ)), new LRangeCommand());
        reg.register(new CommandMetadata("LLEN", 2, 2, Set.of(Flag.READ)), new LLenCommand());
        reg.register(new CommandMetadata("LPOP", 2, 3, Set.of(Flag.WRITE)), new LPopCommand());
        reg.register(new CommandMetadata("BLPOP", 3, 3, Set.of(Flag.WRITE)), new BLPopCommand());
        reg.register(new CommandMetadata("TYPE", 2, 2, Set.of(Flag.READ)), new TypeCommand());
        reg.register(new CommandMetadata("XADD", 4, -1, Set.of(Flag.WRITE)), new XAddCommand());
        reg.register(new CommandMetadata("XRANGE", 4,  4, Set.of(Flag.READ, Flag.SLOW)), new XRangeCommand()); // O(wtf) time & space
        reg.register(new CommandMetadata("XREAD", 4, -1, Set.of(Flag.READ, Flag.SLOW)), new XReadCommand());
    }

}