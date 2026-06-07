package ru.itmo.repository;

import ru.itmo.config.DatabaseConfig;
import ru.itmo.model.Experiment;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
public class ExperimentRepository {

    // ВАЖНОЕ ЗАМЕЧАНИЕ если работать не с PreparedStatement а писать что-то вроде
    //String sql = "SELECT * FROM run_results WHERE owner_username = '" + ownerUsername + "'";
    // злоумышленник может ввести часть SQL запроса и например получить доступ к чужим данным,
    //или даже удалить БД



    // Метод вставки нового эксперимента. Возвращает сгенерированный ID.
    public long save(Experiment experiment) throws SQLException {
        String sql = "INSERT INTO experiments (name, description, owner_username, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?)";
        // prepared statement ? - плейсхолдер будет заменен на параметры ниже

        // специальная форма try закрывает ресурсы после выхода из блока
        // ресурсы - классы реализующие интерфейс AutoCloseable или Сloseable
        //Ресурс — это объект, который после использования нужно явно освободить,
        // потому что он занимает системные ресурсы (память, файловые дескрипторы, сетевые соединения).
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Перегруженная версия prepareStatement, где первый параметр, как обычно,
            // создает подготовленный запрос, а второй указывает,
            // хотим ли мы получить сгенерированные ключи после выполнения запроса.
//Statement.RETURN_GENERATED_KEYS — константа со значением 1. Сообщает JDBC-драйверу, чтобы он вернул ID
//Без этого флага метод ps.getGeneratedKeys() будет пустым.

            ps.setString(1, experiment.getName());
            ps.setString(2, experiment.getDescription());
            ps.setString(3, experiment.getOwnerUsername());
            ps.setTimestamp(4, Timestamp.from(experiment.getCreatedAt()));
            ps.setTimestamp(5, Timestamp.from(experiment.getUpdatedAt()));
            // Timestamp.from() преобразует java.time.Instant в java.sql.Timestamp, понятный БД.

            ps.executeUpdate();
            // выполняем запрос
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
//ResultSet содержит указание на текущую строку, по строкам можно перемещаться методами как next
                if (generatedKeys.next()) {
                    //Изначально текущая строка находится перед первой строкой результата.
                    // И чтобы получить первую строку, нужно хотя бы раз вызвать метод next().
                    return generatedKeys.getLong(1);   // возвращаем сгенерированный ID как лонг
                } else {
                    throw new SQLException("Не удалось получить сгенерированный ID для эксперимента");
                }
            }
        }
    }



    // Вспомогательный метод для преобразования строки ResultSet в объект Experiment
    private Experiment mapRow(ResultSet rs) throws SQLException {
        // принимает текущую строку
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        String ownerUsername = rs.getString("owner_username");
        Instant createdAt = rs.getTimestamp("created_at").toInstant();
        Instant updatedAt = rs.getTimestamp("updated_at").toInstant();
// берем данные из таблицы и преобразуем их в эксперимент
        return new Experiment(id, name, description, ownerUsername, createdAt, updatedAt);
    }

    // Поиск эксперимента по ID
    public Experiment findById(long id) throws SQLException {
        String sql = "SELECT * FROM experiments WHERE id = ?";
        // выбираем все колонки для эксперимента с определенным айди который задается в  ps.setLong(1, id);
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // SQL шаблон, который предусматривает то что ранее у нас юыл плейсхолдер
            ps.setLong(1, id);
            // говорим на место первого вопросительного знака подставь айди
            try (ResultSet rs = ps.executeQuery()) {
                // выполняет запрос по ранее заданным параметрам и передает в переменную типа ResultSet

                if (rs.next()) {
                    return mapRow(rs);
                    // если есть хотя бы одна строка,
                    // преобразуем её в объект через mapRow. Иначе возвращается null (эксперимент не найден).
                }
            }
        }
        return null;
    }

    // Получение всех экспериментов
    public List<Experiment> findAll() throws SQLException {
        String sql = "SELECT * FROM experiments ORDER BY id";
        // сортировка по ID как в TreeMap
        List<Experiment> experiments = new ArrayList<>();
        // будем складывать эксприменты из бд
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             // создаем пустой объект для выполнения запроса
             ResultSet rs = stmt.executeQuery(sql)) {
            // выполняет запрос

            while (rs.next()) {
                experiments.add(mapRow(rs));
            }
        }
        return experiments;
    }

    // Поиск экспериментов по владельцу, работает как findAll, но с фильтрацией и PreparedStatement
    public List<Experiment> findByOwner(String ownerUsername) throws SQLException {
        String sql = "SELECT * FROM experiments WHERE owner_username = ? ORDER BY id";
        List<Experiment> experiments = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ownerUsername);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    experiments.add(mapRow(rs));
                }
            }
        }
        return experiments;
    }

    // Обновление названия и описания по конкретному айди (владелец не меняется)
    public void update(Experiment experiment) throws SQLException {
        String sql = "UPDATE experiments SET name = ?, description = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, experiment.getName());
            ps.setString(2, experiment.getDescription());
            ps.setTimestamp(3, Timestamp.from(experiment.getUpdatedAt()));
            ps.setLong(4, experiment.getId());
            ps.executeUpdate();
        }
    }

    // Удаление одного эксперимента (владелец не проверяется – проверка выполняется в менеджере)
    public void deleteById(long id) throws SQLException {
        String sql = "DELETE FROM experiments WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
    // Удаление всех экспериментов заданного владельца
    public void deleteByOwner(String ownerUsername) throws SQLException {
        String sql = "DELETE FROM experiments WHERE owner_username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ownerUsername);
            ps.executeUpdate();
        }
    }
}
