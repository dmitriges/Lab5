package ru.itmo.model;
// валидация будет выполняться в сеттерах, а затем в конструкторе заменить обращение к полю на
// обращение к сеттеру, чтобы в конструкторе тоже происходила проверка
// плюсы такого подхода: Material целиком и полностью сам за себя отвечает,
// и изменения вносить не нужно, поэтому разделять данные и валидацию не обязательно.

import java.time.Instant;

public final class Experiment {
    // Уникальный номер эксперимента. Программа назначает сама.
    private final long id;
    // Когда создан. Программа ставит автоматически.
    private final Instant createdAt;
    // Название эксперимента. Нельзя пустое. До 128 символов.
    private String name;
    // Описание (кратко “что делаем”). Можно пусто. До 512 символов.
    private String description;
    // Кто создал (логин). На ранних этапах можно "SYSTEM".
    private String ownerUsername;
    // Когда изменяли. Программа обновляет автоматически.
    private Instant updatedAt;

    public Experiment(long id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public Experiment(Instant updatedAt, Instant createdAt, String ownerUsername, String description, String name, long id) {
      //TODO как сделать так чтобы сначала updatedAt принял значение createdAt, а далее уже свое значение времени обновления?
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.setOwnerUsername(ownerUsername);
        this.setDescription(description);
        this.setName(name);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название эксперимента не может быть пустым");
        }
        if (name.length() > 128) {
            throw new IllegalArgumentException("Название эксперимента не может превышать 128 символов");
        }
        this.name = name;
        setUpdatedAt(Instant.now()); // автоматическое обновление даты изменения
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description.length() <= 512){
            this.description = description;
        } else {
            throw new IllegalArgumentException("Недопустимый формат для описания эксперимента");
        }
        setUpdatedAt(Instant.now());
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        if (ownerUsername == null || ownerUsername.isBlank()) {
            throw new IllegalArgumentException("Имя владельца (логин) не может быть пустым");
        }
        if (ownerUsername.length() > 64) {
            throw new IllegalArgumentException("Имя владельца не может превышать 64 символа");
        }
        this.ownerUsername = ownerUsername;
        setUpdatedAt(Instant.now());
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
