package ru.itmo.cli.commands;

import ru.itmo.model.Experiment;
import ru.itmo.services.ExperimentManager;

public class ExpCreateCommand extends BaseCommand {
    private final ExperimentManager experimentManager;

    public ExpCreateCommand(ExperimentManager experimentManager) {
        this.experimentManager = experimentManager;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Создание нового эксперимента.");
        String name = input.promptNonEmpty("Название: ");
        String description = input.prompt("Описание (можно оставить пустым): ");
        String owner = input.prompt("Владелец (Enter для SYSTEM): ");
        if (owner.isBlank()) owner = "SYSTEM";

        Experiment exp = experimentManager.add(name, description, owner);
        System.out.println("Experiment_id=" + exp.getId());
    }
}