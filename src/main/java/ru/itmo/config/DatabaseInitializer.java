package ru.itmo.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {
    private static final String CREATE_USERS = """
            CREATE TABLE IF NOT EXISTS users (
                login VARCHAR(64) PRIMARY KEY,
                password_hash VARCHAR(64) NOT NULL
            )
            """;

    private static final String CREATE_EXPERIMENTS = """
            CREATE TABLE IF NOT EXISTS experiments (
                id BIGSERIAL PRIMARY KEY,
                name VARCHAR(128) NOT NULL,
                description VARCHAR(512) NOT NULL,
                owner_username VARCHAR(64) NOT NULL REFERENCES users(login),
                created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                updated_at TIMESTAMP WITH TIME ZONE NOT NULL
            )
            """;

    private static final String CREATE_RUNS = """
            CREATE TABLE IF NOT EXISTS runs (
                id BIGSERIAL PRIMARY KEY,
                experiment_id BIGINT NOT NULL REFERENCES experiments(id) ON DELETE CASCADE,
                name VARCHAR(128) NOT NULL,
                operator_name VARCHAR(64) NOT NULL,
                owner_username VARCHAR(64) NOT NULL REFERENCES users(login),
                created_at TIMESTAMP WITH TIME ZONE NOT NULL
            )
            """;

    private static final String CREATE_RUN_RESULTS = """
            CREATE TABLE IF NOT EXISTS run_results (
                id BIGSERIAL PRIMARY KEY,
                run_id BIGINT NOT NULL REFERENCES runs(id) ON DELETE CASCADE,
                param VARCHAR(64) NOT NULL,
                value DOUBLE PRECISION NOT NULL,
                unit VARCHAR(16) NOT NULL,
                comment VARCHAR(128) NOT NULL,
                owner_username VARCHAR(64) NOT NULL REFERENCES users(login),
                created_at TIMESTAMP WITH TIME ZONE NOT NULL
            )
            """;

    private DatabaseInitializer() {
    }

    public static void initialize() {
        try (Connection connection = DatabaseConfig.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(CREATE_USERS);
            statement.execute(CREATE_EXPERIMENTS);
            statement.execute(CREATE_RUNS);
            statement.execute(CREATE_RUN_RESULTS);
        } catch (SQLException e) {
            throw new RuntimeException("Database schema initialization failed", e);
        }
    }
}
