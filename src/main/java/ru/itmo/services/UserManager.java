package ru.itmo.services;

import ru.itmo.model.User;
import ru.itmo.repository.UserRepository;
import ru.itmo.ui.util.PasswordHasher;

import java.sql.SQLException;

public class UserManager {
    private final UserRepository repository;

    public UserManager(UserRepository userRepository) {
        this.repository = userRepository;
    }

    public void register(String login, String password) {
        if (login == null || login.isBlank() || login.contains(" ") || login.length() > 64) {
            throw new IllegalArgumentException("Некорректный логин");
        }
        if (password == null || password.isBlank() || password.length() < 4) {
            throw new IllegalArgumentException("Пароль должен содержать минимум 4 символа");
        }
        try {
            if (repository.exists(login)) {
                throw new IllegalArgumentException("Пользователь с логином '" + login + "' уже существует");
            }
            String hash = PasswordHasher.hash(password);
            User user = new User(login, hash);
            repository.save(user);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка регистрации", e);
        }
    }

    public User authenticate(String login, String password) {
        try {
            User user = repository.findByLogin(login);
            if (user == null) {
                throw new IllegalArgumentException("Пользователь '" + login + "' не найден");
            }
            String hash = PasswordHasher.hash(password);
            if (!hash.equals(user.getPasswordHash())) {
                throw new IllegalArgumentException("Неверный пароль");
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка аутентификации", e);
        }
    }

    public boolean exists(String login) {
        try {
            return repository.exists(login);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка проверки существования пользователя", e);
        }
    }
}