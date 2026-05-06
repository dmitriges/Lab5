package ru.itmo.services;

import ru.itmo.model.MeasurementParam;
import ru.itmo.model.RunResult;
import ru.itmo.repository.RunResultRepository;

import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class RunResultManager {
    private final TreeMap<Long, RunResult> cache = new TreeMap<>();
    private final RunResultRepository repository;
    private final RunManager runManager;

    public RunResultManager(RunResultRepository repository, RunManager runManager) {
        this.repository = repository;
        this.runManager = runManager;
        loadAll();
    }

    private void loadAll() {
        try {
            List<RunResult> all = repository.findAll();
            cache.clear();
            for (RunResult runResult : all) {
                cache.put(runResult.getId(), runResult);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка загрузки результатов из БД", e);
        }
    }

    public RunResult add(long runId, MeasurementParam param, double value, String unit,
                         String comment, String ownerUsername) {
        if (!runManager.exists(runId)) {
            throw new NoSuchElementException("Run не найден: id=" + runId);
        }
        String runOwner = runManager.getById(runId).getOwnerUsername();
        if (!runOwner.equals(ownerUsername)) {
            throw new SecurityException("Нельзя добавлять результаты к чужому запуску");
        }

        String safeComment = comment == null ? "" : comment;
        Instant now = Instant.now();
        RunResult temp = new RunResult(0, now, runId, param, value, unit, safeComment, ownerUsername);
        try {
            long id = repository.save(temp);
            RunResult created = new RunResult(id, now, runId, param, value, unit, safeComment, ownerUsername);
            cache.put(id, created);
            return created;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении результата", e);
        }
    }


    public void ensureOwnership(long resultId, String requester) {
        RunResult rr = getById(resultId);
        if (!rr.getOwnerUsername().equals(requester)) {
            throw new SecurityException("У вас нет прав на изменение этого результата.");
        }
    }
    public RunResult getById(long id) {
        RunResult rr = cache.get(id);
        if (rr == null) {
            throw new NoSuchElementException("RunResult не найден: id=" + id);
        }
        return rr;
    }

    public List<RunResult> getAll() {
        return new ArrayList<>(cache.values());
    }


    public void remove(long id, String requester) {
        ensureOwnership(id, requester);
        try {
            repository.deleteById(id);
            cache.remove(id);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении результата", e);
        }
    }

    public List<RunResult> listByRun(long runId) {
        return cache.values().stream()
                .filter(r -> r.getRunId() == runId)
                .collect(Collectors.toList());
    }

    public List<RunResult> listByRunAndParam(long runId, MeasurementParam param) {
        return cache.values().stream()
                .filter(r -> r.getRunId() == runId)
                .filter(r -> r.getParam() == param)
                .collect(Collectors.toList());
    }

    public RunResult update(long resultId, MeasurementParam param, Double value,
                            String unit, String comment, String requester) {
        ensureOwnership(resultId, requester);
        RunResult runResult = getById(resultId);
        if (param != null) runResult.setParam(param);
        if (value != null) runResult.setValue(value);
        if (unit != null) runResult.setUnit(unit);
        if (comment != null) runResult.setComment(comment);
        try {
            repository.update(runResult);
            cache.put(resultId, runResult);
            return runResult;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении результата", e);
        }
    }

    public void clearByOwner(String owner) {
        try {
            repository.deleteByOwner(owner);
            cache.values().removeIf(runResult -> runResult.getOwnerUsername().equals(owner));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при очистке результатов", e);
        }
    }
}