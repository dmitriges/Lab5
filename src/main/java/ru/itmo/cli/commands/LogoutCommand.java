package ru.itmo.cli.commands;

import ru.itmo.cli.Cli;

public class LogoutCommand extends BaseCommand {
    private final Cli cli;

    public LogoutCommand(Cli cli) {
        this.cli = cli;
    }

    @Override
    public void execute(String[] args) {
        if (cli.getCurrentUser() == null) {
            System.out.println("Вы не авторизованы.");
            return;
        }
        System.out.println("Пользователь '" + cli.getCurrentUser() + "' вышел из системы.");
        cli.setCurrentUser(null);
    }
}