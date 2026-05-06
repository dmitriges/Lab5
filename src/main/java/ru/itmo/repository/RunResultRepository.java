package ru.itmo.repository;

import ru.itmo.config.DatabaseConfig;
import ru.itmo.model.MeasurementParam;
import ru.itmo.model.RunResult;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
public class RunResultRepository {
    public long save(RunResult result) throws SQLException {
        String sql = "INSERT INTO run_results (run_id, param, value, unit, comment, owner_username, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, result.getRunId());
            ps.setString(2, result.getParam().name());   // сохраняем имя enum
            ps.setDouble(3, result.getValue());
            ps.setString(4, result.getUnit());
            ps.setString(5, result.getComment());
            ps.setString(6, result.getOwnerUsername());
            ps.setTimestamp(7, Timestamp.from(result.getCreatedAt()));

            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Не удалось получить сгенерированный ID для результата");
                }
            }
        }
    }
    private RunResult mapRow(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        long runId = rs.getLong("run_id");
        MeasurementParam param = MeasurementParam.valueOf(rs.getString("param"));
        double value = rs.getDouble("value");
        String unit = rs.getString("unit");
        String comment = rs.getString("comment");
        String ownerUsername = rs.getString("owner_username");
        Instant createdAt = rs.getTimestamp("created_at").toInstant();

        return new RunResult(id, createdAt, runId, param, value, unit, comment, ownerUsername);
    }


    public RunResult findById(long id) throws SQLException {
        String sql = "SELECT * FROM run_results WHERE id = ?";
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

    // Все результаты отсортированные по айди
    public List<RunResult> findAll() throws SQLException {
        String sql = "SELECT * FROM run_results ORDER BY id";
        List<RunResult> results = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    // Результаты конкретного запуска, у одного запуска может быть несколько результатов и мы фильтруем их по айди
    public List<RunResult> findByRunId(long runId) throws SQLException {
        String sql = "SELECT * FROM run_results WHERE run_id = ? ORDER BY id";
        List<RunResult> results = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, runId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    // Результаты по владельцу
    public List<RunResult> findByOwner(String ownerUsername) throws SQLException {
        String sql = "SELECT * FROM run_results WHERE owner_username = ? ORDER BY id";
        List<RunResult> results = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ownerUsername);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    // Обновление значения/единиц/комментария
    public void update(RunResult result) throws SQLException {
        String sql = "UPDATE run_results SET param = ?, value = ?, unit = ?, comment = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, result.getParam().name());
            ps.setDouble(2, result.getValue());
            ps.setString(3, result.getUnit());
            ps.setString(4, result.getComment());
            ps.setLong(5, result.getId());
            ps.executeUpdate();
        }
    }

    // Удаление по ID
    public void deleteById(long id) throws SQLException {
        String sql = "DELETE FROM run_results WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // Удаление всех результатов владельца
    public void deleteByOwner(String ownerUsername) throws SQLException {
        String sql = "DELETE FROM run_results WHERE owner_username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ownerUsername);
            ps.executeUpdate();
        }
    }

    // Удаление всех результатов конкретного запуска
    public void deleteByRunId(long runId) throws SQLException {
        String sql = "DELETE FROM run_results WHERE run_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, runId);
            ps.executeUpdate();
        }
    }
}
