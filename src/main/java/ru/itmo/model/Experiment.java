package ru.itmo.model;
import java.time.Instant;

public final class Experiment {
    // Уникальный номер эксперимента. Программа назначает сама.
    private final long id;

    // Название эксперимента. Нельзя пустое. До 128 символов.
    private String name;

    // Описание (кратко “что делаем”). Можно пусто. До 512 символов.
    private String description;

    // Кто создал (логин). На ранних этапах можно "SYSTEM".
    private String ownerUsername;

    // Когда создан. Программа ставит автоматически.
    private final Instant createdAt;

    // Когда изменяли. Программа обновляет автоматически.
    private Instant updatedAt;

    public Experiment(long id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public Experiment(Instant updatedAt, Instant createdAt, String ownerUsername, String description, String name, long id) {
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.ownerUsername = ownerUsername;
        this.description = description;
        this.name = name;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
