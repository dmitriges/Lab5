package ru.itmo.model;

import java.time.Instant;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class RunResult {
    private final long id;
    private final Instant createdAt;
    private long runId;
    private MeasurementParam param;
    private double value;
    private String unit;
    private String comment;

    public RunResult(long id, Instant createdAt, String comment, String unit, double value, MeasurementParam param, long runId) {
        this.id = id;
        this.createdAt = createdAt;
        this.setParam(param);
        this.setValue(value);
        this.setUnit(unit);
        this.setComment(comment);
        this.runId = runId;
    }

    @JsonCreator
    public RunResult(
            @JsonProperty("id") long id,
            @JsonProperty("createdAt") Instant createdAt,
            @JsonProperty("runId") long runId,
            @JsonProperty("param") MeasurementParam param,
            @JsonProperty("value") double value,
            @JsonProperty("unit") String unit,
            @JsonProperty("comment") String comment
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.runId = runId;
        this.comment = comment;
        this.unit = unit;
        this.value = value;
        this.param = param;
    }

    // Геттеры
    public long getId() { return id; }
    public Instant getCreatedAt() { return createdAt; }
    public long getRunId() { return runId; }
    public MeasurementParam getParam() { return param; }
    public double getValue() { return value; }
    public String getUnit() { return unit; }
    public String getComment() { return comment; }

    // Сеттеры с валидацией
    public void setRunId(long runId) {
        if (runId <= 0) {
            throw new IllegalArgumentException("ID запуска должен быть положительным числом");
        }
        this.runId = runId;
    }

    public void setParam(MeasurementParam param) {
        if (param == null) {
            throw new IllegalArgumentException("Параметр измерения не может быть null");
        }
        this.param = param;
    }
// TODO почему isFinite???? как может быть не конечным при ручном вводе?
    public void setValue(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Значение должно быть конечным числом");
        }
        this.value = value;
    }

    public void setUnit(String unit) {
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("Единицы измерения не могут быть пустыми");
        }
        if (unit.length() > 16) {
            throw new IllegalArgumentException("Единицы измерения не могут превышать 16 символов");
        }
        this.unit = unit;
    }

    public void setComment(String comment) {
        if (comment == null) {
            throw new IllegalArgumentException("Комментарий не может быть null (используйте пустую строку)");
        }
        if (comment.length() > 128) {
            throw new IllegalArgumentException("Комментарий не может превышать 128 символов");
        }
        this.comment = comment;
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RunResult that)) return false;
        return id == that.id && runId == that.runId
                && Double.compare(value, that.value) == 0
                && Objects.equals(createdAt, that.createdAt)
                && param == that.param
                && Objects.equals(unit, that.unit)
                && Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, runId, param, value, unit, comment);
    }

    @Override
    public String toString() {
        return "RunResult{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", runId=" + runId +
                ", param=" + param +
                ", value=" + value +
                ", unit='" + unit + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}