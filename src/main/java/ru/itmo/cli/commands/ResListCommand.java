package ru.itmo.cli.commands;

import ru.itmo.model.MeasurementParam;
import ru.itmo.model.RunResult;
import ru.itmo.services.RunResultManager;
import java.util.List;

//Принимает ID запуска и опциональный флаг --param PARAM.
//Возвращает список результатов этого запуска (все или только указанного параметра).
//Выводит их в виде аккуратной таблицы.
public class ResListCommand extends BaseCommand {
    private final RunResultManager runResultManager;

    public ResListCommand(RunResultManager runResultManager) {
        this.runResultManager = runResultManager;
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("Ошибка: укажите ID запуска.");
            return;
        }
        long runId = Long.parseLong(args[0]);
        MeasurementParam filterParam = null;

        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--param") && i + 1 < args.length) {
                try {
                    filterParam = MeasurementParam.valueOf(args[i + 1].toUpperCase());
                    //Переменная filterParam инициализируется null, что означает «без фильтрации».
                    //
                    //Цикл проходит по оставшимся аргументам (начиная с индекса 1) в поисках флага --param.
                    //
                    //При обнаружении флага проверяется наличие следующего аргумента (значения параметра).
                    // Значение приводится к верхнему регистру и преобразуется в элемент перечисления
                    // MeasurementParam.

                } catch (IllegalArgumentException e) {
                    System.out.println("Неизвестный параметр: " + args[i + 1]);
                    return;
                }
                i++;
                break;
            }
        }

        List<RunResult> results;
        if (filterParam != null) {
            results = runResultManager.listByRunAndParam(runId, filterParam);
        } else {
            results = runResultManager.listByRun(runId);
            //Если задан параметр для фильтрации, вызывается метод listByRunAndParam, который возвращает только те результаты, у которых param совпадает с указанным.
            //Иначе вызывается listByRun, возвращающий все результаты указанного запуска.
        }

        if (results.isEmpty()) {
            System.out.println("Нет результатов для данного запуска.");
            return;
        }

        System.out.printf("%-5s %-15s %-10s %-10s %-30s%n", "ID", "Param", "Value", "Unit", "Comment");
        for (RunResult runResult : results) {
            System.out.printf("%-5d %-15s %-10.3f %-10s %-30s%n",
                    runResult.getId(),
                    runResult.getParam(),
                    runResult.getValue(),
                    runResult.getUnit(),
                    runResult.getComment().isEmpty() ? "-" : runResult.getComment());
            //Если список результатов пуст, выводится соответствующее сообщение.
            //Иначе выводится заголовок таблицы с фиксированной шириной колонок и выравниванием влево.
            //Для каждого результата выводятся его ID, параметр, значение (с тремя знаками после запятой),
            // единицы измерения и комментарий.
            // Если комментарий пуст, вместо него ставится прочерк -.
        }
    }
}