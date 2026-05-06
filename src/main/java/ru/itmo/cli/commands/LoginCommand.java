package ru.itmo.cli.commands;

import ru.itmo.cli.Cli;
import ru.itmo.services.UserManager;

public class LoginCommand extends BaseCommand {
    private final Cli cli;
    private final UserManager userManager;

    public LoginCommand(Cli cli, UserManager userStorage) {
        this.cli = cli;
        this.userManager = userStorage;
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 0) {
            System.out.println("Ошибка: команда login не принимает аргументы.");
            return;
        }
        if (cli.getCurrentUser() != null) {
            System.out.println("Вы уже вошли как '" + cli.getCurrentUser() + "'.");
            return;
        }
        System.out.println("Вход в систему.");
        String login = input.promptNonEmpty("Логин: ");
        String password = input.prompt("Пароль: ");
        try {
            userManager.authenticate(login, password);
            cli.setCurrentUser(login);
            System.out.println("Добро пожаловать, '" + login + "'!");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка входа: " + e.getMessage());
        }
    }
}