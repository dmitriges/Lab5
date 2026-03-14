package ru.itmo.cli.util;

import ru.itmo.model.MeasurementParam;
import java.util.Scanner;

//Утилитный класс для работы с пользовательским вводом.
//Реализован как синглтон, чтобы не создавать много экземпляров Scanner.

//Синглтон — это шаблон (паттерн) проектирования, который делает две вещи:
//Дает гарантию, что у класса будет всего один экземпляр класса.
//Предоставляет глобальную точку доступа к экземпляру данного класса.

// предоставляет удобные методы для запроса данных у пользователя (строк, чисел, параметров)
// с автоматической проверкой корректности и повторным запросом при ошибке.
public class InputHelper {
    private static InputHelper instance;
    private final Scanner scanner;

    private InputHelper() {
        scanner = new Scanner(System.in);//создали сканер, который будет использоваться везде
    }

    public static InputHelper getInstance() {
        if (instance == null) {
            instance = new InputHelper();
        }
        return instance;
    }
//
    public String prompt(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }
    // nextLine()  используется для считывания строки текста, вплоть до символа перевода строки.

    //требует непустой ввод
    public String promptNonEmpty(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("Значение не может быть пустым. Попробуйте снова.");
        }
    }

    public MeasurementParam promptParam(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return MeasurementParam.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Неверный параметр. Допустимые: PH, CONDUCTIVITY, TURBIDITY, NITRATE.");
            }
        }
    }

    public double promptDouble(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число.");
            }
        }
    }
    public String unquote(String string) {
        if (string == null) return null;
        string = string.trim();
        if (string.startsWith("\"") && string.endsWith("\"") && string.length() >= 2) {
            return string.substring(1, string.length() - 1);
        }
        if (string.startsWith("'") && string.endsWith("'") && string.length() >= 2) {
            return string.substring(1, string.length() - 1);
        }
        return string;
    }
}