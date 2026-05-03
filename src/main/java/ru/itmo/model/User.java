package ru.itmo.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


//Пользователь системы. Хранит логин и хеш пароля.
// Логин уникален. Пароль в открытом виде никогда не сохраняется.

public final class User {
    private final String login;
    private final String passwordHash;  // SHA-256 hex
    // Создаёт пользователя с уже вычисленным хешем.
    // Используется при загрузке из файла.
    @JsonCreator
    public User(@JsonProperty("login") String login,
                @JsonProperty("passwordHash") String passwordHash) {
        this.login = login;
        this.passwordHash = passwordHash;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    // Два пользователя равны, если совпадают логины (логин уникален)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return login.equals(user.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }

    @Override
    public String toString() {
        return "User{login='" + login + "'}";
    }
}