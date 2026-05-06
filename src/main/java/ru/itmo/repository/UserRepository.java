package ru.itmo.repository;

import ru.itmo.config.DatabaseConfig;
import ru.itmo.model.User;
import ru.itmo.config.DatabaseConfig;
import ru.itmo.model.User;
import ru.itmo.ui.util.PasswordHasher;

import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserRepository {

    // Создаёт пользователя (регистрация)
    public void save(User user) throws SQLException {
        String sql = "INSERT INTO users (login, password_hash) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPasswordHash());
            ps.executeUpdate();
        }
    }

    // Находит пользователя по логину
    public User findByLogin(String login) throws SQLException {
        String sql = "SELECT * FROM users WHERE login = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String loginDb = rs.getString("login");
                    String passwordHash = rs.getString("password_hash");
                    return new User(loginDb, passwordHash);
                }
            }
        }
        return null;
    }

    // Проверка существования пользователя
    public boolean exists(String login) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE login = ?";
        // вместо того чтобы возвращать какие-то ненужные нам поля, мы вернем единицу если нашли пользователя
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
