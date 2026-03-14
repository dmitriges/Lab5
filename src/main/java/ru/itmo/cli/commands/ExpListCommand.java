package ru.itmo.cli.commands;

import ru.itmo.model.Experiment;
import ru.itmo.services.ExperimentManager;
import java.util.List;

public class ExpListCommand implements Command {
    private final ExperimentManager experimentManager;
    //ссылка на менеджер экспериментов, через который будут получаться данные.

    public ExpListCommand(ExperimentManager experimentManager) {
        this.experimentManager = experimentManager;
    }

    @Override
    public void execute(String[] args) {
        List<Experiment> experiments = experimentManager.getAll();
        //Запрашивает у менеджера список всех экспериментов
        if (experiments.isEmpty()) {
            System.out.println("Нет экспериментов.");
            return;
        }
        System.out.printf("%-5s %-30s%n", "ID", "Name");
        //Выводится заголовок таблицы с помощью форматированного вывода.
        //%-5s — строка, выровненная влево, шириной 5 символов.
        //%-30s — строка, выровненная влево, шириной 30 символов.
        //%n — перевод строки
        for (Experiment exp : experiments) {
            System.out.printf("%-5d %-30s%n", exp.getId(), exp.getName());
        }//Выводит идентификатор (как число, выровненное влево на 5 позиций)
        // и имя (выровненное влево на 30 позиций).
    }
}