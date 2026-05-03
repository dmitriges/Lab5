package ru.itmo.cli.commands;

import ru.itmo.cli.Cli;
import ru.itmo.services.ExperimentManager;

public class ExpUpdateCommand extends BaseCommand {
    private final ExperimentManager experimentManager;
    private final Cli cli;

    public ExpUpdateCommand(ExperimentManager experimentManager, Cli cli) {
        this.experimentManager = experimentManager;
        this.cli = cli;
    }

    @Override
    public void execute(String[] args) {

        if (cli.getCurrentUser() == null) {
            System.out.println("Ошибка: необходимо войти в систему.");
            return;
        }
        if (args.length < 2) {
            System.out.println("Ошибка: укажите ID и поля для обновления (name=... description=...).");
            return;
        }
        long id = Long.parseLong(args[0]);
        //Первый аргумент — это идентификатор эксперимента. Преобразуем его из строки в число
        String newName = null;
        String newDescription = null;


        if (cli.getCurrentUser() == null) {
            System.out.println("Ошибка: необходимо войти в систему.");
            return;
        }
        // проверка, что владелец - из experimentManager
        experimentManager.ensureOwnership(id, cli.getCurrentUser());

        //начинаем цикл по аргументам
        for (int i = 1; i < args.length; i++) {

            // Аргумент команды имеют формат поле=значение
            String[] pair = args[i].split("=", 2);
            // Проверяет, получилось ли ровно две части.
            // Если нет (например, аргумент не содержит =), значит аргумент некорректен.
            if (pair.length != 2) {
                System.out.println("Пропущен некорректный аргумент: " + args[i]);
                continue;
            }
            String field = pair[0].toLowerCase();
            //Извлекает ключ (имя поля) из первой части разбитого аргумента
            String value = input.unquote(pair[1]);
            if (field.equals("name")) {
                newName = value;
            } else if (field.equals("description")) {
                newDescription = value;
            } else {
                System.out.println("Неизвестное поле: " + field);
            }
        }

//если эксперимент не найден или валидация не пройдена
        try {
            experimentManager.update(id, newName, newDescription, cli.getCurrentUser());
            System.out.println("OK");
        } catch (Exception e) {
            System.out.println("Ошибка при обновлении: " + e.getMessage());
        }
    }
}