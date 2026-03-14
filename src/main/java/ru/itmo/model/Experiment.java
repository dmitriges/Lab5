package ru.itmo.model;

import java.time.Instant;
import java.util.Objects;

public final class Experiment {
    private final long id;
    private final Instant createdAt;
    private String name;
    private String description;
    private String ownerUsername;
    private Instant updatedAt;

    // Конструктор только с id и createdAt (для случаев, когда данные будут заполняться сеттерами позже)
    public Experiment(long id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = createdAt; // изначально равно createdAt
    }

    // полный конструктор с валидацией через сеттеры
    public Experiment(long id, String name, String description, String ownerUsername, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
        // Используем сеттеры для валидации
        this.setName(name);
        this.setDescription(description);
        this.setOwnerUsername(ownerUsername);
        this.updatedAt = createdAt; // после всех проверок устанавливаем updatedAt = createdAt
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getOwnerUsername() { return ownerUsername; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    // Сеттеры с валидацией и обновлением updatedAt
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название эксперимента не может быть пустым");
        }
        if (name.length() > 128) {
            throw new IllegalArgumentException("Название эксперимента не может превышать 128 символов");
        }
        this.name = name;
        this.updatedAt = Instant.now();
    }

    public void setDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException("Описание не может быть null (используйте пустую строку)");
        }
        if (description.length() > 512) {
            throw new IllegalArgumentException("Описание не может превышать 512 символов");
        }
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public void setOwnerUsername(String ownerUsername) {
        if (ownerUsername == null || ownerUsername.isBlank()) {
            throw new IllegalArgumentException("Имя владельца (логин) не может быть пустым");
        }
        if (ownerUsername.length() > 64) {
            throw new IllegalArgumentException("Имя владельца не может превышать 64 символа");
        }
        this.ownerUsername = ownerUsername;
        this.updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Experiment that)) return false;
        return id == that.id && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(name, that.name) && Objects.equals(description, that.description)
                && Objects.equals(ownerUsername, that.ownerUsername) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, name, description, ownerUsername, updatedAt);
    }

    @Override
    public String toString() {
        return "Experiment{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", ownerUsername='" + ownerUsername + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}