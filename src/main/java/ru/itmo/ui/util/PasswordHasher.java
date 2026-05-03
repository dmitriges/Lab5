package ru.itmo.ui.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;


//Берём пароль (строка) получаем его байтовое представление в UTF-8.
//
//Получаем экземпляр MessageDigest для SHA-256.
//
//Вычисляем digest(байты) получаем массив из 32 байт.
//
//Преобразуем массив байт в hex-строку (64 символа).
//
//Возвращаем эту строку как хеш пароля.


// SHA-256 хеш пароль в виде шестнадцатеричной строки.
public final class PasswordHasher {
    private PasswordHasher() {}

    public static String hash(String password) {
        // исключение практически не возможно,
        // Но public static MessageDigest getInstance(String algorithm)
        //        throws NoSuchAlgorithmException
        // поэтому нам нужно проверить на это исключения для корректной работы метода.
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            //статический метод, возвращающий объект MessageDigest, реализующий указанный алгоритм.

            byte[] hashBytes = instance.digest(password.getBytes(StandardCharsets.UTF_8));
            // превращаем строку пароля в массив байтов в кодировке UTF-8.
            // Использование константы StandardCharsets.UTF_8 предпочтительнее строковой "UTF-8", потому что:
            //Не бросает проверяемое исключение UnsupportedEncodingException (в отличие от String.getBytes(String charsetName)).

           //instance(byte[] input) – выполняет хеширование переданного массива байтов (!произвольного размера)
            // и возвращает результат в виде массива фиксированного размера 32 байта.
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Алгоритм SHA-256 не поддерживается", e);
        }
    }
    //Хеш-функция выдаёт бинарные данные (32 байта для SHA-256).
    // Хранить бинарные данные в JSON или показывать их в консоли неудобно.
    // Поэтому мы переводим их в читаемую hex-строку длиной 64 символа (по два символа на байт).

//преобразовать массив байтов в строку шестнадцатеричных цифр
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            // StringBuilder позволяет многократно добавлять кусочки
            //append(String s) – добавляет переданную строку в конец буфера.
            sb.append(String.format("%02x", b));
            // форматируем так, чтобы каждый 16ричный символ кодировался двумя знаками -
            // иначе байты меньше 16 будут кодироваться одним символом 16ричной системы
        }
        return sb.toString();
    }
}