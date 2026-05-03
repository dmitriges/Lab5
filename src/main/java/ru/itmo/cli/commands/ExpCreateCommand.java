package ru.itmo.cli.commands;

import ru.itmo.cli.Cli;
import ru.itmo.model.Experiment;
import ru.itmo.services.ExperimentManager;

public class ExpCreateCommand extends BaseCommand {
    private final ExperimentManager experimentManager;
    private final Cli cli;
    public ExpCreateCommand(ExperimentManager experimentManager, Cli cli) { this.experimentManager = experimentManager; this.cli = cli; }

    @Override
    public void execute(String[] args) {

        if (cli.getCurrentUser() == null) {
            System.out.println("Ошибка: необходимо войти в систему.");
            return;
        }
        
        System.out.println("Создание нового эксперимента.");
        String name = input.promptNonEmpty("Название: ");
        String description = input.prompt("Описание (можно оставить пустым): ");
        String owner = cli.getCurrentUser();

        Experiment exp = experimentManager.add(name, description, owner);
        System.out.println("Experiment_id=" + exp.getId());
    }
}