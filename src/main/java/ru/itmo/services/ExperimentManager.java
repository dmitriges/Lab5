package ru.itmo.services;

import ru.itmo.model.Experiment;

import java.time.Instant;
import java.util.*;

public class ExperimentManager {
        private final TreeMap<Long, Experiment> experiments = new TreeMap<>();
        private long nextId = 1;


        public long getExperimentId(){
            return System.currentTimeMillis() + experiments.size();
        }

        public Experiment addExperiment(String name, String description, String ownerUsername) {
            long id = nextId++;
            Instant now = Instant.now();//

            // Создаём объект с id/createdAt, дальше заполняем поля через сеттеры валидированные
            Experiment exp = new Experiment(id, now);

            // owner по ТЗ может быть SYSTEM на ранних этапах
            exp.setOwnerUsername(ownerUsername == null || ownerUsername.isBlank() ? "SYSTEM" : ownerUsername);

            exp.setDescription(description == null ? "" : description);

            exp.setName(name);

            // Для только что созданного эксперимента логично updatedAt = createdAt.
            exp.setUpdatedAt(exp.getCreatedAt());

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
        // способ для того, чтобы получать данные поэтому ArrayList, tree map нам нужен для того, чтобы
        // осуществлять сортировку по id
        public List<Experiment> getAll() {
            return new ArrayList<>(experiments.values()); // уже отсортировано по id
        }

        public Experiment update(long id, String name, String description) {
            Experiment exp = getById(id);

            if (name != null) {
                exp.setName(name);
            }
            if (description != null) {
                exp.setDescription(description);
            }
            // updatedAt уже обновится внутри сеттеров
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

