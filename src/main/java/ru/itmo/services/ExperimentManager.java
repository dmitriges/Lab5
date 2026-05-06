package ru.itmo.services;

import ru.itmo.model.Experiment;
import ru.itmo.repository.ExperimentRepository;

import java.sql.SQLException;
import java.time.Instant;
import java.util.*;


public class ExperimentManager {
    private final TreeMap<Long, Experiment> cache  = new TreeMap<>();
    private final ExperimentRepository repository;

    public ExperimentManager(ExperimentRepository repository) {
        this.repository = repository;
        loadAll();
    }

    public void loadAll() {
        try {
            List<Experiment> all = repository.findAll();
            cache.clear();
            for (Experiment exp : all) {
                cache.put(exp.getId(), exp);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка загрузки экспериментов из БД", e);
        }
    }

    // Добавление нового эксперимента
    public Experiment add(String name, String description, String ownerUsername) {
        Instant now = Instant.now();
        // Создаём объект без id, временный, айди задаст БД
        Experiment temp = new Experiment(0, name, description, ownerUsername, now, now);
        try {
            // отправляем запрос в бд
            long id = repository.save(temp);
            // Создаём объект с реальным id
            Experiment created = new Experiment(id, name, description, ownerUsername, now, now);
            cache.put(id, created);
            return created;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении эксперимента", e);
        }
    }

    public Experiment getById(long id) {
        Experiment exp = cache.get(id);
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

    public List<Experiment> getAll() {
        return new ArrayList<>(cache.values());
    }

    // Обновление эксперимента
    public Experiment update(long id, String newName, String newDescription, String requester) {
        ensureOwnership(id, requester);
        Experiment exp = getById(id);
        if (newName != null) exp.setName(newName);
        if (newDescription != null) exp.setDescription(newDescription);
        try {
            repository.update(exp);  // обновление в БД (updated_at уже обновлён сеттером)
            cache.put(id, exp);     // обновляем кэш
            return exp;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении эксперимента", e);
        }
    }

    // Удаление эксперимента (каскадное удаление запусков и результатов происходит в БД)
    public void remove(long id, String requester) {
        ensureOwnership(id, requester);
        try {
            repository.deleteById(id); // надо удалить и из бд и из кеша
            cache.remove(id);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении эксперимента", e);
        }
    }

    // Очистка всех экспериментов владельца
    public void clearByOwner(String ownerUsername) {
        try {
            repository.deleteByOwner(ownerUsername);
            // Удаляем из кэша
            cache.values().removeIf(exp -> exp.getOwnerUsername().equals(ownerUsername));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при очистке экспериментов", e);
        }
    }

    public boolean exists(long id) {
        return cache.containsKey(id);
    }
    public String getOwner(long experimentId) {
        Experiment exp = getById(experimentId);   // выбросит NoSuchElementException, если не найден
        return exp.getOwnerUsername();
    }
}