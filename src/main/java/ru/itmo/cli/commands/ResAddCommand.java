package ru.itmo.cli.commands;

import ru.itmo.model.MeasurementParam;
import ru.itmo.model.RunResult;
import ru.itmo.services.RunManager;
import ru.itmo.services.RunResultManager;

public class ResAddCommand extends BaseCommand {
    private final RunResultManager runResultManager;
    private final RunManager runManager;

    public ResAddCommand(RunResultManager runResultManager, RunManager runManager) {
        this.runResultManager = runResultManager;
        this.runManager = runManager;
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("Ошибка: укажите ID запуска.");
            return;
        }
        long runId = Long.parseLong(args[0]);

        if (!runManager.exists(runId)) {
            System.out.println("Ошибка: запуск с id " + runId + " не найден.");
            return;
        }

        System.out.println("Добавление результата к запуску " + runId);
        MeasurementParam param = input.promptParam("Параметр (PH, CONDUCTIVITY, TURBIDITY, NITRATE): ");
        double value = input.promptDouble("Значение: ");
        String unit = input.promptNonEmpty("Единицы измерения (например, mg/L): ");
        String comment = input.prompt("Комментарий (можно оставить пустым): ");// запрашивает комментарий может быть пустым

        //Выводится сообщение о начале ввода.
        //
        //input.promptParam — запрашивает у пользователя один из допустимых параметров измерения (enum).
        // Метод циклически повторяет запрос, пока не будет введено корректное значение.




        RunResult runResult = runResultManager.add(runId, param, value, unit, comment);

        //Все собранные данные передаются в метод add менеджера результатов.
        // Менеджер создаёт объект RunResult с автоматически сгенерированным ID и текущим временем,
        // добавляет его в коллекцию и возвращает созданный объект.
        System.out.println("OK result_id=" + runResult.getId());
    }
}