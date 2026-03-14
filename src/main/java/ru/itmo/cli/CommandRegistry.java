package ru.itmo.cli;

import ru.itmo.cli.commands.Command;
import java.util.*;


// вместо того чтобы писать огромные switch case
public class CommandRegistry {
    private final TreeMap<String, Command> commands = new TreeMap<>();

    //Регистрирует команды
    public void register(String name, Command command) {
        commands.put(name, command);
    }
//Предоставляет доступ к командам по имени
    public Command getCommand(String name) {
        return commands.get(name);
    }

    // для HelpCommand, чтобы предоставить список всех зарегистрированных команд
    public TreeMap<String, Command> getAllCommands() {
        return commands;
    }
}