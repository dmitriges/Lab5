package ru.itmo.model;

import java.time.Instant;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Run {
    private final long id;
    private final long experimentId;
    private final Instant createdAt;
    private String name;
    private String operatorName;

    @JsonCreator
    public Run(
            @JsonProperty("id") long id,
            @JsonProperty("experimentId") long experimentId,
            @JsonProperty("name") String name,
            @JsonProperty("operatorName") String operatorName,
            @JsonProperty("createdAt") Instant createdAt
    ) {
        this.id = id;
        this.experimentId = experimentId;
        this.createdAt = createdAt;
        this.setName(name);
        this.setOperatorName(operatorName);
    }

    public long getId() { return id; }
    public long getExperimentId() { return experimentId; }
    public String getName() { return name; }
    public String getOperatorName() { return operatorName; }
    public Instant getCreatedAt() { return createdAt; }

    // Сеттеры с валидацией
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название запуска не может быть пустым");
        }
        if (name.length() > 128) {
            throw new IllegalArgumentException("Название запуска не может превышать 128 символов");
        }
        this.name = name;
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

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Run run)) return false;
        return id == run.id && experimentId == run.experimentId
                && Objects.equals(name, run.name)
                && Objects.equals(operatorName, run.operatorName)
                && Objects.equals(createdAt, run.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, experimentId, name, operatorName, createdAt);
    }

    @Override
    public String toString() {
        return "Run{" +
                "id=" + id +
                ", experimentId=" + experimentId +
                ", name='" + name + '\'' +
                ", operatorName='" + operatorName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}