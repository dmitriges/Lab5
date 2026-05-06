/*
package ru.itmo.storage;
*/

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ru.itmo.model.User;
import ru.itmo.ui.util.PasswordHasher;

import java.io.File;
import java.io.IOException;
import java.util.*;
// НУЖЕН ДЛЯ ЭТАПА 5 НО ПОЛНОСТЬЮ ОТКЛЮЧАЕТСЯ НА ЭТАПЕ 6 И ЗАМЕНЯЕТСЯ НА USERMANAGER.

//хранение списка зарегистрированных пользователей в оперативной памяти,
//
//загрузка пользователей из файла users.json при старте программы,
//
//сохранение изменений обратно в файл,
//
//регистрация нового пользователя с проверкой уникальности логина и хеширование пароля,
//
//аутентификация (проверка пары логин/пароль).
/*

public class UserStorage {
    private final File file;
    //ссылается на файл users.json

    private final ObjectMapper mapper;
    // для преобразования списка в json и обратно

    private final Map<String, User> usersByLogin = new HashMap<>();*/
    // поиск пользователя по логину


     // @param filePath путь к файлу users.json
/*
    public UserStorage(String filePath) {
        this.file = new File(filePath);*/
        //Получает путь к файлу, создаёт объект File.
/*

        this.mapper = new ObjectMapper();
*/

        //Файл JSON будет содержать отступы и переносы строк, чтобы его удобно было читать
    /*    mapper.enable(SerializationFeature.INDENT_OUTPUT);
        load();
    }*/

    //Загружает пользователей из файла. Если файл отсутствует или пуст – остаёмся с пустым списком.
 /*   private void load() {
        if (!file.exists()) {
            return;
        }
        try {*/
// !!!!!после компиляции в байт-коде вся информация о типе-параметре исчезает
// Решение - сложная система:
// создаем анонимный класс

//информация о классе родителя сохраняется даже в байт коде,
// поэтому метод getGenericSuperclass() внутри TypeReference может извлечь тип аргумента после стирания
        /*    List<User> userList = mapper.readValue(file, new TypeReference<List<User>>() {});
            for (User user : userList) {
                usersByLogin.put(user.getLogin(), user);
            }
        } catch (IOException e) {
            System.err.println("Предупреждение: не удалось загрузить файл пользователей. "
                    + "Работаем с пустым списком. Ошибка: " + e.getMessage());
            // Не прерываем выполнение, чтобы программа могла стартовать*/
/*
        }
    }
*/


    //Сохраняет текущий список пользователей в файл.

   /* private void save() {
        try {
            List<User> userList = new ArrayList<>(usersByLogin.values());
            // делаем список из юзеров
            mapper.writeValue(file, userList);
            // превращаем список юзеров в JSON, записываем в файл
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл пользователей: " + e.getMessage(), e);
        }
    }
*/
    //
     //Регистрирует нового пользователя + валидация

  /*  public void register(String login, String password) {
        validateLogin(login);
        validatePassword(password);

        if (usersByLogin.containsKey(login)) {
            throw new IllegalArgumentException("Пользователь с логином '" + login + "' уже существует");
        }

        String hash = PasswordHasher.hash(password);
        User user = new User(login, hash);
        usersByLogin.put(login, user);
        save();
    }
*/

// аутентификация
   /* public User authenticate(String login, String password) {
        validateLogin(login);
        validatePassword(password);

        User user = usersByLogin.get(login);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь '" + login + "' не найден");
        }
        String hash = PasswordHasher.hash(password);
        if (!hash.equals(user.getPasswordHash())) {
            throw new IllegalArgumentException("Неверный пароль");
        }
        return user;
    }
*/
 //чтобы быстро проверить, занят ли логин, не вызывая исключений
   /* public boolean exists(String login) {
        return usersByLogin.containsKey(login);
    }

    private void validateLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Логин не может быть пустым");
        }
        if (login.contains(" ")) {
            throw new IllegalArgumentException("Логин не должен содержать пробелы");
        }
        if (login.length() > 64) {
            throw new IllegalArgumentException("Логин слишком длинный (макс. 64 символа)");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }
        if (password.length() < 4) {
            throw new IllegalArgumentException("Пароль должен содержать минимум 4 символа");
        }
        if (password.length() > 67) {
            throw new IllegalArgumentException("Пароль должен содержать минимум 4 символа");
        }
    }
}*/