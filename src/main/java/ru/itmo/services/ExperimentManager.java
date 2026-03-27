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
        Experiment exp = new Experiment(id, name, description, owner, now, now);
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

    public List<Experiment> getAll() {
        return new ArrayList<>(experiments.values());
    }

    public Experiment update(long id, String newName, String newDescription) {
        Experiment exp = getById(id);
        if (newName != null) {
            exp.setName(newName);
        }
        if (newDescription != null) {
            exp.setDescription(newDescription);
        }
        return exp;
    }

    public void remove(long id) {
        if (experiments.remove(id) == null) {
            throw new NoSuchElementException("Experiment не найден: id=" + id);
        }
    }

    public boolean exists(long id) {
        return experiments.containsKey(id);
    }
}