package ru.itmo.services;

import ru.itmo.model.Run;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class RunManager {
    private final TreeMap<Long, Run> runs = new TreeMap<>();
    private long nextId = 1;
    private final ExperimentManager experimentManager;

    public RunManager(ExperimentManager experimentManager) {
        if (experimentManager == null) {
            throw new NullPointerException("experimentManager не может быть null");
        }
        this.experimentManager = experimentManager;
    }

    private long generateId() {
        return nextId++;
    }
//Принимает ID эксперимента, название запуска и имя оператора.
// Проверяет, что эксперимент существует, создаёт новый запуск с текущим временем и возвращает его.
    public Run add(long experimentId, String runName, String operatorName) {
        if (!experimentManager.exists(experimentId)) {
            throw new NoSuchElementException("Experiment не найден: id=" + experimentId);
        }
        long id = generateId();
        Instant now = Instant.now();
        Run run = new Run(id, experimentId, runName, operatorName, now);
        runs.put(id, run);
        return run;
    }
    //
    public Run getById(long id) {
        Run run = runs.get(id);
        if (run == null) {
            throw new NoSuchElementException("Run не найден: id=" + id);
        }
        return run;
    }
//Возвращает список всех запусков (порядок по ID).
    public List<Run> getAll() {
        return new ArrayList<>(runs.values());
    }

    public void remove(long id) {
        if (runs.remove(id) == null) {
            throw new NoSuchElementException("Run не найден: id=" + id);
        }
    }
// containsKey - метод из treeMap
    public boolean exists(long id) {
        return runs.containsKey(id);
    }
    //Возвращает все запуски, принадлежащие указанному эксперименту.
    public List<Run> listByExperiment(long experimentId) {
        return runs.values().stream()
                .filter(r -> r.getExperimentId() == experimentId)
        //r -> r.getExperimentId() == experimentId. Для каждого элемента r (объект Run) проверяется,
                // равен ли его experimentId переданному аргументу.
                // Если да – элемент остаётся в потоке, если нет – отбрасывается.
                .collect(Collectors.toList());
        //.collect(Collectors.toList()) – терминальная операция, которая собирает элементы оставшегося потока
        // в новый список (List<Run>). Collectors.toList() – стандартный коллектор, создающий ArrayList.
    }
    //Альтернативная реализация
    //public List<Run> listByExperiment(long experimentId) {
    //    List<Run> result = new ArrayList<>();
    //    for (Run run : runs.values()) {
    //        if (run.getExperimentId() == experimentId) {
    //            result.add(run);
    //        }
    //    }
    //    return result;
    //}

    //Возвращает последние n запусков эксперимента (сортировка по убыванию даты создания)
    public List<Run> listLastByExperiment(long experimentId, int n) {
        if (n <= 0) return List.of();// если запросил 0 или меньше последних - возвращаем пустой лист
        return runs.values().stream()
                // получаем коллекцию всех объектов Run, хранящихся в TreeMap, и создаём из неё поток (stream).
                .filter(r -> r.getExperimentId() == experimentId)
                //проверка каждого объекта из Run на равенство по айди с
                .sorted(Comparator.comparing(Run::getCreatedAt).reversed())
                // по умолчанию сортировка от самых старых к самым новым поэтому реверсд
                .limit(n)
                .collect(Collectors.toList());
    }

    public Run update(long runId, String name, String operatorName) {
        Run run = getById(runId);
        if (name != null) {
            run.setName(name);
        }
        if (operatorName != null) {
            run.setOperatorName(operatorName);
        }
        return run;
    }
}