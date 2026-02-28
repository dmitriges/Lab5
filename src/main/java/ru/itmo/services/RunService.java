package ru.itmo.services;
import ru.itmo.model.Run;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class RunService {

    private final NavigableMap<Long, Run> runs = new TreeMap<>();
    private long nextId = 1;

    private final ExperimentService experimentService;

    public RunService(ExperimentService experimentService) {
        this.experimentService = Objects.requireNonNull(experimentService);
    }

    public Run add(long experimentId, String runName, String operatorName) {
        if (!experimentService.exists(experimentId)) {
            throw new NoSuchElementException("Experiment не найден: id=" + experimentId);
        }

        long id = nextId++;
        Instant now = Instant.now();

        // Используем твой текущий публичный конструктор (id, experimentId, createdAt)
        Run run = new Run(id, experimentId, now);
        run.setName(runName);
        run.setOperatorName(operatorName);

        runs.put(id, run);
        return run;
    }

    public Run getById(long id) {
        Run run = runs.get(id);
        if (run == null) {
            throw new NoSuchElementException("Run не найден: id=" + id);
        }
        return run;
    }

    public List<Run> getAll() {
        return new ArrayList<>(runs.values());
    }

    public void remove(long id) {
        if (runs.remove(id) == null) {
            throw new NoSuchElementException("Run не найден: id=" + id);
        }
    }

    public boolean exists(long id) {
        return runs.containsKey(id);
    }

    public List<Run> listByExperiment(long experimentId) {
        // если хочешь — можно тоже бросать ошибку, если experiment не существует
        return runs.values().stream()
                .filter(r -> r.getExperimentId() == experimentId)
                .collect(Collectors.toList());
    }

    public List<Run> listLastByExperiment(long experimentId, int n) {
        if (n <= 0) return List.of();

        // "last" разумнее трактовать как последние по времени createdAt
        return runs.values().stream()
                .filter(r -> r.getExperimentId() == experimentId)
                .sorted(Comparator.comparing(Run::getCreatedAt).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    public Run update(long runId, String name, String operatorName) {
        Run run = getById(runId); // если нет — кинет NoSuchElementException

        // Обновляем только если передали значение (null = “не менять”)
        if (name != null) {
            run.setName(name); // тут твоя валидация (не пусто, <=128)
        }
        if (operatorName != null) {
            run.setOperatorName(operatorName); // валидация (не пусто, <=64)
        }

        // В TreeMap можно ничего не делать: объект уже лежит в map, мы меняем его по ссылке
        return run;
    }
}