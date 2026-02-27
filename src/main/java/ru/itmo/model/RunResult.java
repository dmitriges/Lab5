package ru.itmo.model;

import java.time.Instant;

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
        this.unit = unit;
        this.comment = comment;
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
        this.unit = unit;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
