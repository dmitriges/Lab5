package ru.itmo.cli.commands;

import ru.itmo.cli.Cli;
import ru.itmo.model.Run;
import ru.itmo.services.ExperimentManager;
import ru.itmo.services.RunManager;

public class RunAddCommand extends BaseCommand {
    private final RunManager runManager;
    private final ExperimentManager experimentManager;
    private final Cli cli;

    public RunAddCommand(RunManager runManager, ExperimentManager experimentManager, Cli cli) {
        this.runManager = runManager;
        this.experimentManager = experimentManager;
        this.cli = cli;
    }

    @Override
    public void execute(String[] args) {

        if (cli.getCurrentUser() == null) {
            System.out.println("Ошибка: необходимо войти в систему.");
            return;
        }

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
        String operator = cli.getCurrentUser();

        Run run = runManager.add(experimentId, runName, cli.getCurrentUser(), cli.getCurrentUser());
        System.out.println(" run_id=" + run.getId());
    }
}