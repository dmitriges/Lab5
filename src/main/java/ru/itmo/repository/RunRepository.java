package ru.itmo.repository;

import ru.itmo.config.DatabaseConfig;
import ru.itmo.model.Run;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RunRepository {
    // Вставка нового запуска. Возвращает сгенерированный ID.
    public long save(Run run) throws SQLException {
        String sql = "INSERT INTO runs (experiment_id, name, operator_name, owner_username, created_at) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, run.getExperimentId());
            ps.setString(2, run.getName());
            ps.setString(3, run.getOperatorName());
            ps.setString(4, run.getOwnerUsername());
            ps.setTimestamp(5, Timestamp.from(run.getCreatedAt()));

            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Не удалось получить сгенерированный ID для запуска");
                }
            }
        }
    }

    // Преобразование строки ResultSet в объект Run
    private Run mapRow(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        long experimentId = rs.getLong("experiment_id");
        String name = rs.getString("name");
        String operatorName = rs.getString("operator_name");
        String ownerUsername = rs.getString("owner_username");
        Instant createdAt = rs.getTimestamp("created_at").toInstant();

        return new Run(id, experimentId, createdAt, name, operatorName, ownerUsername);
    }
    // Поиск запуска по ID
    public Run findById(long id) throws SQLException {
        String sql = "SELECT * FROM runs WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    // Все запуски
    public List<Run> findAll() throws SQLException {
        String sql = "SELECT * FROM runs ORDER BY id";
        List<Run> runs = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                runs.add(mapRow(rs));
            }
        }
        return runs;
    }

    // Запуски конкретного эксперимента
    public List<Run> findByExperimentId(long experimentId) throws SQLException {
        String sql = "SELECT * FROM runs WHERE experiment_id = ? ORDER BY id";
        List<Run> runs = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, experimentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    runs.add(mapRow(rs));
                }
            }
        }
        return runs;
    }

    // Запуски по владельцу
    public List<Run> findByOwner(String ownerUsername) throws SQLException {
        String sql = "SELECT * FROM runs WHERE owner_username = ? ORDER BY id";
        List<Run> runs = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ownerUsername);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    runs.add(mapRow(rs));
                }
            }
        }
        return runs;
    }

    // Обновление названия и оператора
    public void update(Run run) throws SQLException {
        String sql = "UPDATE runs SET name = ?, operator_name = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, run.getName());
            ps.setString(2, run.getOperatorName());
            ps.setLong(3, run.getId());
            ps.executeUpdate();
        }
    }

    // Удаление по ID
    public void deleteById(long id) throws SQLException {
        String sql = "DELETE FROM runs WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // Удаление всех запусков владельца
    public void deleteByOwner(String ownerUsername) throws SQLException {
        String sql = "DELETE FROM runs WHERE owner_username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ownerUsername);
            ps.executeUpdate();
        }
    }
    // Удаление всех запусков конкретного эксперимента (используется при каскадном удалении)
    public void deleteByExperimentId(long experimentId) throws SQLException {
        String sql = "DELETE FROM runs WHERE experiment_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, experimentId);
            ps.executeUpdate();
        }
    }
}
