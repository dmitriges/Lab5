package ru.itmo.cli.commands;

import ru.itmo.services.ExperimentManager;

public class ExpUpdateCommand extends BaseCommand {
    private final ExperimentManager experimentManager;

    public ExpUpdateCommand(ExperimentManager experimentManager) {
        this.experimentManager = experimentManager;
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            System.out.println("Ошибка: укажите ID и поля для обновления (name=... description=...).");
            return;
        }
        long id = Long.parseLong(args[0]);
        //Первый аргумент — это идентификатор эксперимента. Преобразуем его из строки в число
        String newName = null;
        String newDescription = null;

        //начинаем цикл по аргументам
        for (int i = 1; i < args.length; i++) {

            //Aргументы команды имеют формат поле=значение
            String[] pair = args[i].split("=", 2);
            // Проверяет, получилось ли ровно две части.
            // Если нет (например, аргумент не содержит =), значит аргумент некорректен.
            if (pair.length != 2) {
                System.out.println("Пропущен некорректный аргумент: " + args[i]);
                continue;
            }
            String field = pair[0].toLowerCase();
            //звлекает ключ (имя поля) из первой части разбитого аргумента
            String value = input.unquote(pair[1]);
            if (field.equals("name")) {
                newName = value;
            } else if (field.equals("description")) {
                newDescription = value;
            } else {
                System.out.println("Неизвестное поле: " + field);
            }
        }

//если эксперимент не найден или валидация не пройдена)
        try {
            experimentManager.update(id, newName, newDescription);
            System.out.println("OK");
        } catch (Exception e) {
            System.out.println("Ошибка при обновлении: " + e.getMessage());
        }
    }
}