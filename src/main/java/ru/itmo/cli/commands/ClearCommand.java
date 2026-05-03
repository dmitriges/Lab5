package ru.itmo.cli.commands;

import ru.itmo.cli.Cli;
import ru.itmo.services.ExperimentManager;
import ru.itmo.services.RunManager;
import ru.itmo.services.RunResultManager;

public class ClearCommand extends BaseCommand {
    private final ExperimentManager experimentManager;
    private final RunManager runManager;
    private final RunResultManager runResultManager;
    private final Cli cli;

    public ClearCommand(ExperimentManager experimentManager, RunManager runManager, RunResultManager runResultManager, Cli cli) {
        this.experimentManager = experimentManager;
        this.runManager = runManager;
        this.runResultManager = runResultManager;
        this.cli = cli;
    }

    @Override
    public void execute(String[] args) {
        if (cli.getCurrentUser() == null) {
            System.out.println("Ошибка: необходимо войти в систему.");
            return;
        }
        String owner = cli.getCurrentUser();
        runResultManager.clearByOwner(owner);
        runManager.clearByOwner(owner);
        experimentManager.clearByOwner(owner);
        System.out.println("Все ваши эксперименты, запуски и результаты удалены.");
    }
}