package ru.itmo.cli.commands;

import ru.itmo.model.Run;
import ru.itmo.services.ExperimentManager;
import ru.itmo.services.RunManager;

public class RunAddCommand extends BaseCommand {
    private final RunManager runManager;
    private final ExperimentManager experimentManager;

    public RunAddCommand(RunManager runManager, ExperimentManager experimentManager) {
        this.runManager = runManager;
        this.experimentManager = experimentManager;
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("Ошибка: укажите ID эксперимента.");
            return;
        }
        long experimentId = Long.parseLong(args[0]);
        // Проверяется существование эксперимента (менеджер сам кинет исключение, но можно предупредить)
        if (!experimentManager.exists(experimentId)) {
            System.out.println("Ошибка: эксперимент с id " + experimentId + " не найден.");
            return;
        }

        System.out.println("Добавление запуска к эксперименту " + experimentId);
        String runName = input.promptNonEmpty("Название запуска: ");
        String operator = input.promptNonEmpty("Оператор: ");

        Run run = runManager.add(experimentId, runName, operator);
        System.out.println(" run_id=" + run.getId());
    }
}