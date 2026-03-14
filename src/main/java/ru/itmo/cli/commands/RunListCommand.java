package ru.itmo.cli.commands;

import ru.itmo.model.Run;
import ru.itmo.services.RunManager;
import java.util.List;

//Возвращает список запусков этого эксперимента (все или последние N).
public class RunListCommand extends BaseCommand {
    private final RunManager runManager;

    public RunListCommand(RunManager runManager) {
        this.runManager = runManager;
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("Ошибка: укажите ID эксперимента.");
            return;
        }
        long experimentId = Long.parseLong(args[0]);
        int lastN = -1;

        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--last") && i + 1 < args.length) {
                lastN = Integer.parseInt(args[i + 1]);
                i++;
                break;
            }//При обнаружении флага проверяется наличие следующего аргумента (числа N),
            // который парсится в int. Если парсинг не удаётся — ошибка.
            //Если флаг не найден, lastN остаётся -1.
        }

        List<Run> runs;
        //если флаг был указан корректно  возвращает список последних lastN запусков
        if (lastN > 0) {
            runs = runManager.listLastByExperiment(experimentId, lastN);
        } else {
            runs = runManager.listByExperiment(experimentId);
        }

        if (runs.isEmpty()) {
            System.out.println("Нет запусков для данного эксперимента.");
            return;
        }

        System.out.printf("%-5s %-25s %-15s %-20s%n", "ID", "Run name", "Operator", "Time");
        for (Run run : runs) {
            System.out.printf("%-5d %-25s %-15s %-20s%n",
                    run.getId(),
                    run.getName(),
                    run.getOperatorName(),
                    formatter.formatInstant(run.getCreatedAt()));
        }
        //выводится заголовок таблицы с помощью форматированного вывода (printf), где указана ширина колонок и выравнивание влево.

        //Для каждого запуска выводятся его ID, название, оператор и отформатированное время создания
    }
}