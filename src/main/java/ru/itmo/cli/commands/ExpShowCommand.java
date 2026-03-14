package ru.itmo.cli.commands;

import ru.itmo.model.Experiment;
import ru.itmo.model.Run;
import ru.itmo.services.ExperimentManager;
import ru.itmo.services.RunManager;
import java.util.List;


// выводит подробную информацию о конкретном эксперименте:
// его идентификатор, название, описание, владельца, даты создания и последнего изменения,
// а также количество запусков, относящихся к этому эксперименту.
public class ExpShowCommand extends BaseCommand {
    private final ExperimentManager experimentManager;
    //менеджер экспериментов, через который мы получаем объект Experiment по ID.
    private final RunManager runManager;
//нужен для подсчёта количества запусков данного эксперимента.

    public ExpShowCommand(ExperimentManager experimentManager, RunManager runManager) {
        this.experimentManager = experimentManager;
        this.runManager = runManager;
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("Ошибка: укажите ID эксперимента.");
            return;
        }
        long id = Long.parseLong(args[0]);//Первый элемент массива строк args, по которому выдаем инфу
        // в зависимости от введенных данных

        //Long.parseLong(...) — статический метод класса Long,
        // который принимает строку и пытается преобразовать её в примитивный тип long (целое число).

        // некорректный ввод будет обработан в главном цикле
        Experiment exp = experimentManager.getById(id);
        List<Run> runs = runManager.listByExperiment(id);
        System.out.println("Experiment #" + exp.getId());
        System.out.println("name: " + exp.getName());
        System.out.println("description: " + (exp.getDescription().isEmpty() ? "<пусто>" : exp.getDescription()));
        System.out.println("owner: " + exp.getOwnerUsername());
        System.out.println("created: " + formatter.formatInstant(exp.getCreatedAt()));
        System.out.println("updated: " + formatter.formatInstant(exp.getUpdatedAt()));
        System.out.println("runs: " + runs.size());
    }
}