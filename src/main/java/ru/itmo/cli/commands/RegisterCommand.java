package ru.itmo.cli.commands;

import ru.itmo.services.UserManager;

// новая команда для этапа 5
public class RegisterCommand extends BaseCommand {
    private final UserManager userStorage;

    public RegisterCommand(UserManager userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 0) {
            System.out.println("Ошибка: команда register не принимает аргументы.");
            return;
        }
        System.out.println("Регистрация нового пользователя.");
        String login = input.promptNonEmpty("Логин: ");
        String password = input.prompt("Пароль: ");
        if (password.isBlank()) {
            System.out.println("Ошибка: пароль не может быть пустым.");
            return;
        }
        try {
            userStorage.register(login, password);
            System.out.println("Пользователь '" + login + "' успешно зарегистрирован.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка регистрации: " + e.getMessage());
        }
    }
}