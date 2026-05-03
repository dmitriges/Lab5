package ru.itmo.services;

import ru.itmo.model.Experiment;
import java.time.Instant;
import java.util.*;

public class ExperimentManager {
    private final TreeMap<Long, Experiment> experiments = new TreeMap<>();
    private long nextId = 1;

    private long generateId() {
        return nextId++;
    }

    public Experiment add(String name, String description, String ownerUsername) {
        // Если ownerUsername не задан, используем "SYSTEM"
        String owner = (ownerUsername == null || ownerUsername.isBlank()) ? "SYSTEM" : ownerUsername;
        long id = generateId();
        Instant now = Instant.now();
        Experiment exp = new Experiment(id, now);
        exp.setName(name);
        exp.setDescription(description);
        exp.setOwnerUsername(owner);
        experiments.put(id, exp);
        return exp;
    }

    public Experiment getById(long id) {
        Experiment exp = experiments.get(id);
        if (exp == null) {
            throw new NoSuchElementException("Experiment не найден: id=" + id);
        }
        return exp;
    }

    // метод для этапа 5, проверка владельца
    public void ensureOwnership(long experimentId, String username) {
        Experiment exp = getById(experimentId);
        if (!exp.getOwnerUsername().equals(username)) {
            throw new SecurityException("Нет прав на изменение объекта.");
        }
    }


    // принимает лист загруженных экспериментов
    public void importData(java.util.List<Experiment> loadedExperiments) {
        experiments.clear();
        // очищает текущее значение поля experiment в experimentManager

        //
        long maxId = 0;
        for (Experiment experiment : loadedExperiments) {
            experiments.put(experiment.getId(), experiment);
            if (experiment.getId() > maxId) {
                maxId = experiment.getId();
            }
        }

        nextId = maxId + 1;
    }

    public List<Experiment> getAll() {
        return new ArrayList<>(experiments.values());
    }


    public java.util.Map<Long, Experiment> exportData() {
        return new java.util.TreeMap<>(experiments);
    }

    public Experiment update(long id, String newName, String newDescription, String requester) {
        ensureOwnership(id, requester);
        Experiment exp = getById(id);
        if (newName != null) exp.setName(newName);
        if (newDescription != null) exp.setDescription(newDescription);
        return exp;
    }

    public void remove(long id, String requester) {
        ensureOwnership(id, requester);
        if (experiments.remove(id) == null) {
            throw new NoSuchElementException("Experiment не найден: id=" + id);
        }
    }

    public void clearByOwner(String ownerUsername) {
        experiments.values().removeIf(exp -> exp.getOwnerUsername() != null && exp.getOwnerUsername().equals(ownerUsername));
    }

    public boolean exists(long id) {
        return experiments.containsKey(id);
    }


    public String getOwner(long experimentId) {
        Experiment exp = getById(experimentId);   // выбросит NoSuchElementException, если не найден
        return exp.getOwnerUsername();
    }
}