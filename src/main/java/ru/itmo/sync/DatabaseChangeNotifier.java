package ru.itmo.sync;

import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import ru.itmo.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class DatabaseChangeNotifier implements AutoCloseable {
    private static final String CHANNEL = "lab5_data_changed";
    private static final int NOTIFICATION_TIMEOUT_MS = 1000;
    private static final int RECONNECT_DELAY_MS = 1000;

    private final Runnable onChange;
    // колбэк вызывается при  получении уведомления
    private final AtomicBoolean running = new AtomicBoolean(false);
    // все операции с ним атомарны. Это значит, что когда один поток меняет значение,
    // а другой в это же время его читает, они не «пересекаются» – читающий всегда видит либо старое,
    // либо новое значение, но никогда не видит «промежуточное» состояние.

    private volatile Connection connection;
    // VOLATILE - volatile — это модификатор поля,
    // который гарантирует видимость изменений этого поля между разными потоками.

    private Thread listenerThread;

    public DatabaseChangeNotifier(Runnable onChange) {
        this.onChange = Objects.requireNonNull(onChange, "onChange");
    }

    public void start() {
        if (!running.compareAndSet(false, true)) {
            // гарантирует что поток запуститься только один раз сравнивает с false
            // если false то устанавливает true, и наоборот
            return;
        }

        listenerThread = new Thread(this::listenLoop, "database-change-listener");
        listenerThread.setDaemon(true);
        // поток демон значит, что JVM не будет ждать наш демон-поток и завершится
        listenerThread.start();
    }

    //Внешний цикл – переподключение чтобы при обрыве соединения с БД не умирать, а попытаться переподключиться
    private void listenLoop() {
        while (running.get()) {
            try (Connection listenConnection = DatabaseConfig.getConnection()) {
                connection = listenConnection;
                // PGNotification – это объект, который содержит информацию об одном уведомлении:
                // имя канала, сообщение (payload) и PID (ID процесса)
                PGConnection pgConnection = listenConnection.unwrap(PGConnection.class);
                // превращаем  Connection в PGConnection методом unwrap
                // класс PGConnection расширяет возможности Connection позволяя использовать такие методы как

                try (Statement statement = listenConnection.createStatement()) {
                    statement.execute("LISTEN " + CHANNEL);
                }
//Внутренний цикл – получение уведомлений
                while (running.get()) {
                    PGNotification[] notifications = pgConnection.getNotifications(NOTIFICATION_TIMEOUT_MS);
                    if (notifications == null) {
                        continue;
                    }
                    for (PGNotification ignored : notifications) {
                        onChange.run();
                        // для каждого появившегося уведомления вызываем колбэк
                    }
                }
            } catch (SQLException e) {
                if (running.get()) {
                    System.err.println("Database change listener error: " + e.getMessage());
                    sleepBeforeReconnect();
          // метод для паузы + проверки ошибки
                }
            } catch (RuntimeException e) {
                if (running.get()) {
                    System.err.println("Database change callback error: " + e.getMessage());
                }
            } finally {
                connection = null;
                // при выходе из try соединение закрывается,
                // обнуляем поле чтобы close не пытался повторно его закрыть
            }
        }
    }

    public static void publishChange() {
        // отправляет уведомление в канал
   // статический метод - можно вызывать без создания объекта
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT pg_notify(?, ?)")) {
//SQL-функция pg_notify. Эта функция принимает два аргумента: имя канала и сообщение.
// Здесь используется SELECT, потому что pg_notify — это функция,
// которая возвращает void и вызывается внутри запроса.
            // просто notify не поддерживает prepareStatement
            statement.setString(1, CHANNEL);
            // название канала
            statement.setString(2, Long.toString(System.nanoTime()));
            // текущее время в наносекундах - для уникальности уведомления
            statement.execute();
        } catch (SQLException e) {
            System.err.println("Database change notification failed: " + e.getMessage());
        }
    }

    private static void sleepBeforeReconnect() {
        try {
            Thread.sleep(RECONNECT_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
//  Если во время сна другой поток вызовет listenerThread.interrupt(),
//  то Thread.sleep выбросит InterruptedException.
//  Мы его ловим и восстанавливаем флаг прерывания: Thread.currentThread().interrupt().
//  не можем здесь обработать прерывание, поэтому сообщаем системе, что оно было,
//  и поток сможет корректно завершиться позже

    }

    @Override
    public void close() {
        running.set(false);

        Connection activeConnection = connection;
        if (activeConnection != null) {
            try {
                activeConnection.close();
            } catch (SQLException e) {
                System.err.println("Database change listener close error: " + e.getMessage());
            }
        }

        if (listenerThread != null) {
            listenerThread.interrupt();
        }
    }
}
