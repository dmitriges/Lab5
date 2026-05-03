package ru.itmo.cli.commands;

import ru.itmo.cli.Cli;
import ru.itmo.storage.UserStorage;

public class LoginCommand extends BaseCommand {
    private final Cli cli;
    private final UserStorage userStorage;

    public LoginCommand(Cli cli, UserStorage userStorage) {
        this.cli = cli;
        this.userStorage = userStorage;
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
            userStorage.authenticate(login, password);
            cli.setCurrentUser(login);
            System.out.println("Добро пожаловать, '" + login + "'!");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка входа: " + e.getMessage());
        }
    }
}