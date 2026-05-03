package ru.itmo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public final class Experiment implements Serializable {
    // TODO разобраться
    @Serial
    private static final long serialVersionUID = 1L;

    private final long id;
    private final Instant createdAt;
    private String name;
    private String description;
    private String ownerUsername;
    private Instant updatedAt;

    public Experiment(long id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public Experiment(long id, Instant createdAt, String name, String description, String ownerUsername, Instant updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.setName(name);
        this.setDescription(description);
        this.setOwnerUsername(ownerUsername);
        this.updatedAt = updatedAt;
    }

    @JsonCreator
    public Experiment(
            @JsonProperty("id") long id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("ownerUsername") String ownerUsername,
            @JsonProperty("createdAt") Instant createdAt,
            @JsonProperty("updatedAt") Instant updatedAt
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.ownerUsername = ownerUsername;
        this.name = name;
        this.description = description;
        this.updatedAt = updatedAt;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getOwnerUsername() { return ownerUsername; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

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