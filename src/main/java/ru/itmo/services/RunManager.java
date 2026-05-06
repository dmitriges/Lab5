package ru.itmo.services;

import ru.itmo.model.Run;
import ru.itmo.repository.RunRepository;

import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class RunManager {
    private final TreeMap<Long, Run> cache = new TreeMap<>();
    private final RunRepository repository;
    private final ExperimentManager experimentManager;


    public RunManager(RunRepository repository, ExperimentManager experimentManager) {
        this.repository = repository;
        this.experimentManager = experimentManager;
        loadAll();
    }

    private void loadAll() {
        try {
            List<Run> all = repository.findAll();
            cache.clear();
            for (Run run : all) {
                cache.put(run.getId(), run);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка загрузки запусков из БД", e);
        }
    }


//Принимает ID эксперимента, название запуска и имя оператора.
// Проверяет, что эксперимент существует, создаёт новый запуск с текущим временем и возвращает его.

    public Run add(long experimentId, String runName, String operatorName, String ownerUsername) {
        if (!experimentManager.exists(experimentId)) {
            throw new NoSuchElementException("Experiment не найден: id=" + experimentId);
        }
        String expOwner = experimentManager.getOwner(experimentId);
        if (!expOwner.equals(ownerUsername)) {
            throw new SecurityException("Нельзя добавлять запуски к чужому эксперименту");
        }

        Instant now = Instant.now();
        // опять заглушка сначала создаем пробег без айди чтобы потом взять его из бд
        Run temp = new Run(0, experimentId, now, runName, operatorName, ownerUsername);
        try {
            long id = repository.save(temp);
            Run created = new Run(id, experimentId, now, runName, operatorName, ownerUsername);
            cache.put(id, created);
            return created;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении запуска", e);
        }
    }



    public Run getById(long id) {
        Run run = cache.get(id);
        if (run == null) throw new NoSuchElementException("Run не найден: id=" + id);
        return run;
    }

    public void ensureOwnership(long runId, String requester) {
        Run run = getById(runId);
        if (!run.getOwnerUsername().equals(requester)) {
            throw new SecurityException("У вас нет прав на изменение этого запуска.");
        }
    }

//Возвращает список всех запусков (порядок по ID).
    public List<Run> getAll() {
        return new ArrayList<>(cache.values());
    }


    public void remove(long id, String requester) {
        ensureOwnership(id, requester);
        try {
            repository.deleteById(id);
            cache.remove(id);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении запуска", e);
        }
    }


// containsKey - метод из treeMap
    public boolean exists(long id) {
        return cache.containsKey(id);
    }


    //Возвращает все запуски, принадлежащие указанному эксперименту.
    public List<Run> listByExperiment(long experimentId) {
        return cache.values().stream()
                .filter(r -> r.getExperimentId() == experimentId)
                .collect(Collectors.toList());
    }



    //Возвращает последние n запусков эксперимента (сортировка по убыванию даты создания)
    public List<Run> listLastByExperiment(long experimentId, int n) {
        if (n <= 0) return List.of();// если запросил 0 или меньше последних - возвращаем пустой лист
        return cache.values().stream()
                // получаем коллекцию всех объектов Run, хранящихся в TreeMap, и создаём из неё поток (stream).
                .filter(r -> r.getExperimentId() == experimentId)
                //проверка каждого объекта из Run на равенство по айди с
                .sorted(Comparator.comparing(Run::getCreatedAt).reversed())
                // по умолчанию сортировка от самых старых к самым новым поэтому реверсд
                .limit(n)
                .collect(Collectors.toList());
    }

    public Run update(long runId, String name, String operatorName, String requester) {
        ensureOwnership(runId, requester);
        Run run = getById(runId);
        if (name != null) run.setName(name);
        if (operatorName != null) run.setOperatorName(operatorName);
        try {
            repository.update(run);
            cache.put(runId, run);
            return run;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении запуска", e);
        }
    }


    public void clearByOwner(String owner) {
        try {
            repository.deleteByOwner(owner);
            cache.values().removeIf(run -> run.getOwnerUsername().equals(owner));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при очистке запусков", e);
        }
    }
}