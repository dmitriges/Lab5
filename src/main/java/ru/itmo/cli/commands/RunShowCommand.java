package ru.itmo.cli.commands;

import ru.itmo.model.Run;
import ru.itmo.model.RunResult;
import ru.itmo.services.RunManager;
import ru.itmo.services.RunResultManager;
import java.util.List;

public class RunShowCommand extends BaseCommand {
    private final RunManager runManager;
    private final RunResultManager runResultManager;

    public RunShowCommand(RunManager runManager, RunResultManager runResultManager) {
        this.runManager = runManager;
        this.runResultManager = runResultManager;
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("Ошибка: укажите ID запуска.");
            return;
        }
        long id = Long.parseLong(args[0]);
        Run run = runManager.getById(id);
        List<RunResult> results = runResultManager.listByRun(id);
        System.out.println("Run #" + run.getId());
        System.out.println("experiment_id: " + run.getExperimentId());
        System.out.println("name: " + run.getName());
        System.out.println("operator: " + run.getOperatorName());
        System.out.println("created: " + formatter.formatInstant(run.getCreatedAt()));
        System.out.println("results: " + results.size());
    }
}