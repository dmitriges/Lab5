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

    private long generateId() {
        return nextId++;
    }

    public RunResult add(long runId, MeasurementParam param,double value, String unit, String comment) {
        if (!runManager.exists(runId)) {
            throw new NoSuchElementException("Run не найден: id=" + runId);
        }
        // Преобразуем null комментарий в пустую строку (сеттер требует не null)
        String safeComment = comment == null ? "" : comment;
        long id = generateId();
        Instant now = Instant.now();
        RunResult rr = new RunResult(id, now, runId, param, value, unit, safeComment);
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

    public java.util.Map<Long, RunResult> exportData() {
        return new java.util.LinkedHashMap<>(results);
    }

    public void importData(java.util.List<RunResult> loadedResults) {
        results.clear();

        long maxId = 0;
        for (RunResult result : loadedResults) {
            results.put(result.getId(), result);
            if (result.getId() > maxId) {
                maxId = result.getId();
            }
        }

        nextId = maxId + 1;
    }

    public RunResult update(long resultId,
                            MeasurementParam param,
                            Double value,
                            String unit,
                            String comment) {
        RunResult rr = getById(resultId);
        if (param != null) {
            rr.setParam(param);
        }
        if (value != null) {
            rr.setValue(value);
        }
        if (unit != null) {
            rr.setUnit(unit);
        }
        if (comment != null) {
            rr.setComment(comment);
        }
        return rr;
    }

    public void clear() {
        results.clear();
        nextId = 1;
    }
}