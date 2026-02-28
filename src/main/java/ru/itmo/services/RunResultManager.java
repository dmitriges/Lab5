package ru.itmo.services;

import ru.itmo.model.MeasurementParam;
import ru.itmo.model.RunResult;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class RunResultManager {

    private final TreeMap<Long, RunResult> results = new TreeMap<>();
    private long nextId = 1;

    private final RunManager runManager;

    public RunResultManager(RunManager runManager) {
        this.runManager = Objects.requireNonNull(runManager);
    }

    public RunResult add(long runId,
                         MeasurementParam param,
                         double value,
                         String unit,
                         String comment) {

        if (!runManager.exists(runId)) {
            throw new NoSuchElementException("Run не найден: id=" + runId);
        }
        if (param == null) {
            throw new IllegalArgumentException("Ошибка: param не может быть null");
        }
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Ошибка: value должно быть конечным числом");
        }

        long id = nextId++;
        Instant now = Instant.now();

        RunResult rr = new RunResult(
                id,
                now,
                runId,
                param,
                value,
                unit,
                comment == null ? "" : comment
        );

        results.put(id, rr);
        return rr;
    }

    public RunResult getById(long id) {
        RunResult rr = results.get(id);
        if (rr == null) {
            throw new NoSuchElementException("RunResult не найден: id=" + id);
        }
        return rr;
    }

    public List<RunResult> getAll() {
        return new ArrayList<>(results.values());
    }

    public void remove(long id) {
        if (results.remove(id) == null) {
            throw new NoSuchElementException("RunResult не найден: id=" + id);
        }
    }

    public List<RunResult> listByRun(long runId) {
        return results.values().stream()
                .filter(r -> r.getRunId() == runId)
                .collect(Collectors.toList());
    }

    public List<RunResult> listByRunAndParam(long runId, MeasurementParam param) {
        return results.values().stream()
                .filter(r -> r.getRunId() == runId)
                .filter(r -> r.getParam() == param)
                .collect(Collectors.toList());
    }

    public RunResult update(long resultId,
                            MeasurementParam param,
                            Double value,
                            String unit,
                            String comment) {

        RunResult rr = getById(resultId); // если нет — NoSuchElementException

        // param: null = не менять
        if (param != null) {
            rr.setParam(param); // рекомендую добавить в домене проверку param != null
        }

        // value: Double, чтобы можно было передать null ("не менять")
        if (value != null) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Ошибка: value должно быть конечным числом");
            }
            rr.setValue(value); // в домене можно тоже проверять finiteness
        }

        // unit: null = не менять
        if (unit != null) {
            rr.setUnit(unit); // твоя валидация (не пусто, <=16)
        }

        // comment: null = не менять (или можно null -> "", как решишь)
        if (comment != null) {
            rr.setComment(comment); // твоя валидация (<=128)
        }

        return rr;
    }
}