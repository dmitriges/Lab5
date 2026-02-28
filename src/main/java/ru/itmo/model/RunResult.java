package ru.itmo.model;

import java.time.Instant;
import java.util.Objects;

public final class RunResult {
    // Уникальный номер результата. Программа назначает сама.
    private final long id;
    // Когда добавили результат. Программа ставит автоматически.
    private final Instant createdAt;
    // К какому запуску относится (id запуска).
    // Должен ссылаться на реально существующий Run.
    private long runId;
    // Что измеряли (PH/CONDUCTIVITY/NITRATE...). Выбирается из списка MeasurementParam.
    private MeasurementParam param;
    // Числовое значение результата.
    private double value;
    // Единицы (например "mg/L"). Нельзя пустое. До 16 символов.
    private String unit;
    // Комментарий (например “after 60 min”). Можно пусто. До 128 символов.
    private String comment;



    public RunResult(long id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public RunResult(long id, Instant createdAt, long runId, MeasurementParam param, double value, String unit, String comment) {
        this.id = id;
        this.createdAt = createdAt;
        this.runId = runId;
        this.param = param;
        this.value = value;
        this.setUnit(unit);
        this.setComment(comment);
    }

    public long getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getRunId() {
        return runId;
    }

    public void setRunId(long runId) {
        this.runId = runId;
    }

    public MeasurementParam getParam() {
        return param;
    }

    public void setParam(MeasurementParam param) {
        this.param = param;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
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
        if (comment != null && comment.length() > 128) {
            throw new IllegalArgumentException("Комментарий не может превышать 128 символов");
        }
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RunResult runResult)) return false;
        return id == runResult.id && runId == runResult.runId && Double.compare(value, runResult.value) == 0 && Objects.equals(createdAt, runResult.createdAt) && param == runResult.param && Objects.equals(unit, runResult.unit) && Objects.equals(comment, runResult.comment);
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
