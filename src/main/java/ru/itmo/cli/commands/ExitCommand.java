package ru.itmo.cli.commands;

import ru.itmo.cli.Cli;

public class ExitCommand implements Command {
    private final Cli cli;//Объявляет приватное неизменяемое поле cli типа Cli.
    // Оно будет хранить ссылку на главный объект приложения, чтобы команда могла его остановить.

    public ExitCommand(Cli cli) {
        this.cli = cli;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Завершение работы.");
        cli.stop();
    }
}