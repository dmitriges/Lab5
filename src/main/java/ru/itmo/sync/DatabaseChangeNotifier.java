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
    private final AtomicBoolean running = new AtomicBoolean(false);

    private volatile Connection connection;
    private Thread listenerThread;

    public DatabaseChangeNotifier(Runnable onChange) {
        this.onChange = Objects.requireNonNull(onChange, "onChange");
    }

    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }

        listenerThread = new Thread(this::listenLoop, "database-change-listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void listenLoop() {
        while (running.get()) {
            try (Connection listenConnection = DatabaseConfig.getConnection()) {
                connection = listenConnection;
                PGConnection pgConnection = listenConnection.unwrap(PGConnection.class);

                try (Statement statement = listenConnection.createStatement()) {
                    statement.execute("LISTEN " + CHANNEL);
                }

                while (running.get()) {
                    PGNotification[] notifications = pgConnection.getNotifications(NOTIFICATION_TIMEOUT_MS);
                    if (notifications == null) {
                        continue;
                    }
                    for (PGNotification ignored : notifications) {
                        onChange.run();
                    }
                }
            } catch (SQLException e) {
                if (running.get()) {
                    System.err.println("Database change listener error: " + e.getMessage());
                    sleepBeforeReconnect();
                }
            } catch (RuntimeException e) {
                if (running.get()) {
                    System.err.println("Database change callback error: " + e.getMessage());
                }
            } finally {
                connection = null;
            }
        }
    }

    public static void publishChange() {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT pg_notify(?, ?)")) {
            statement.setString(1, CHANNEL);
            statement.setString(2, Long.toString(System.nanoTime()));
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
