package ru.itmo.cli.commands;

import ru.itmo.model.Experiment;
import ru.itmo.services.ExperimentManager;
import java.util.ArrayList;
import java.util.List;

// переписал так, чтобы был красивый вывод
public class ExpListCommand implements Command {
    private final ExperimentManager experimentManager;

    private static final int ID_WIDTH = 5;
    private static final int NAME_WIDTH = 30;
    private static final int OWNER_WIDTH = 20;

    public ExpListCommand(ExperimentManager experimentManager) {
        this.experimentManager = experimentManager;
    }

    @Override
    public void execute(String[] args) {
        List<Experiment> experiments = experimentManager.getAll();
        if (experiments.isEmpty()) {
            System.out.println("Нет экспериментов.");
            return;
        }

        // Заголовок
        System.out.printf("%-5s %-30s %-20s%n", "ID", "Name", "Owner");

        for (Experiment exp : experiments) {
            String idStr = String.format("%-5d", exp.getId());
            String ownerStr = exp.getOwnerUsername();
            List<String> nameLines = wrapText(exp.getName(), NAME_WIDTH);

            // Первая строка с ID и владельцем
            System.out.printf("%-5s %-30s %-20s%n",
                    idStr,
                    nameLines.get(0),
                    ownerStr);

            // Дополнительные строки названия (если есть)
            for (int i = 1; i < nameLines.size(); i++) {
                System.out.printf("%-5s %-30s %-20s%n",
                        "",                    // ID не повторяем
                        nameLines.get(i),
                        "");                   // Owner не повторяем
            }
        }
    }


    // Разобьем текст на строки заданной ширины, стараясь разрывать по пробелам
    private List<String> wrapText(String text, int width) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add("");
            return lines;
        }

        String remaining = text;
        while (remaining.length() > width) {
            int breakPos = remaining.lastIndexOf(' ', width);
            if (breakPos <= 0) {
                breakPos = width;
            }
            lines.add(remaining.substring(0, breakPos).trim());
            remaining = remaining.substring(breakPos).trim();
        }
        if (!remaining.isEmpty()) {
            lines.add(remaining);
        }
        return lines;
    }
}