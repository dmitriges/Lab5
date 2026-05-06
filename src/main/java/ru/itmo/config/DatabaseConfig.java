package ru.itmo.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConfig {
    private static final Properties props = new Properties();
    // поле хранит загруженные параметры, удобные методы, хранит пару ключ значение
    // хэшмап не имеет необходимых методов и их бы пришлось писать самим

    static { // читаем файл регистрируем драйвер далее
        try (InputStream is = DatabaseConfig.class.getResourceAsStream("/database.properties")) {
            if (is == null) {
                throw new RuntimeException("Файл database.properties не найден в ресурсах");
            }
            props.load(is);
            // Загружаем драйвер PostgreSQL
            Class.forName("org.postgresql.Driver"); // загружает класс в память JVM.
            // существует автоматическая загрузка но могут возникнуть проблемы при создании fat jar
        } catch (IOException | ClassNotFoundException e) {
            //ловим ошибку чтения и ненайденный класс драйвера
            throw new RuntimeException("Ошибка инициализации конфигурации БД", e);
        }
    }

    private DatabaseConfig() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password")
        ); // подключаемся к бд
    }
}
