package ru.itmo.server;

import ru.itmo.server.commands.Command;

import java.util.HashMap;
import java.util.Map;

public class ServerCommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();

    public void register(String name, Command command) {
        commands.put(name.toLowerCase(), command);
    }

    public Command getCommand(String name) {
        if (name == null) {
            return null;
        }
        return commands.get(name.toLowerCase());
    }
}
