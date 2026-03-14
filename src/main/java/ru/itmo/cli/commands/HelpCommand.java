package ru.itmo.cli.commands;

import ru.itmo.cli.CommandRegistry;

public class HelpCommand implements Command {
    private final CommandRegistry registry;//Поле registry хранит ссылку на реестр команд

    public HelpCommand(CommandRegistry registry) {
        this.registry = registry;//команда получает реестр извне.
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Доступные команды:");
        for (String name : registry.getAllCommands().keySet()) {
            System.out.println("  " + name);
        }// stream API не нужен поскольку данные никак не фильтруются и тд. Просто вывод так более читаем и удобен

    }
}