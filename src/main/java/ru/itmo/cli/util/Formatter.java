package ru.itmo.cli.util;

import java.time.Instant;
import java.time.ZoneId;//часовой пояс.
import java.time.format.DateTimeFormatter;//форматировщик даты/времени.

//класс форматирующий данные при выводе
public class Formatter {
    private static Formatter instance;
    private final DateTimeFormatter formatter;// переменная, содержащая настроенный форматировщик даты и времени.

    private Formatter() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
    }//вывод по типу 2026-03-14 15:30:45.
// .withZone(ZoneId.systemDefault()) при форматировании Instant нужно использовать системный часовой пояс

    public static Formatter getInstance() {
        if (instance == null) {
            instance = new Formatter();
        }
        return instance;
    }

    //возвращает отформатированную строку
    public String formatInstant(Instant instant) {
        if (instant == null) return "";
        return formatter.format(instant);
    }
}