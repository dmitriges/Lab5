package ru.itmo.model;
import java.time.Instant;
public final class Run {
    // Уникальный номер запуска. Программа назначает сама.
    private final long id;

    // К какому эксперименту относится (id эксперимента).
    // Должен ссылаться на реально существующий Experiment.
    private final long experimentId;

    // Название запуска reminder: “Run-2026-02-03-A”. Нельзя пустое. До 128 символов.
    private String name;

            // Кто выполнял запуск (логин или имя). Нельзя пустое. До 64 символов.
    private String operatorName;

    // Когда запуск зарегистрирован. Программа ставит автоматически.
    private final Instant createdAt;

    public Run(long id, long experimentId, Instant createdAt) {
        this.id = id;
        this.experimentId = experimentId;
        this.createdAt = createdAt;
    }

    private Run(long id, long experimentId, String name, String operatorName, Instant createdAt) {
        this.id = id;
        this.experimentId = experimentId;
        this.setName(name);
        this.setOperatorName(operatorName);
        this.createdAt = createdAt;
    }


    public long getId() {
        return id;
    }

    public long getExperimentId() {
        return experimentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название запуска не может быть пустым");
        }
        if (name.length() > 128) {
            throw new IllegalArgumentException("Название запуска не может превышать 128 символов");
        }
        this.name = name;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        if (operatorName == null || operatorName.isBlank()) {
            throw new IllegalArgumentException("Имя оператора не может быть пустым");
        }
        if (operatorName.length() > 64) {
            throw new IllegalArgumentException("Имя оператора не может превышать 64 символов");
        }
        this.operatorName = operatorName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
