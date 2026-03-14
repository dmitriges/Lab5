package ru.itmo.cli.commands;

import ru.itmo.model.MeasurementParam;
import ru.itmo.services.ParamStats;
import ru.itmo.services.SummaryManager;
import java.util.Map;

public class ExpSummaryCommand extends BaseCommand {
    private final SummaryManager summaryManager;

    public ExpSummaryCommand(SummaryManager summaryManager) {
        this.summaryManager = summaryManager;
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("Ошибка: укажите ID эксперимента.");
            return;
        }
        long experimentId = Long.parseLong(args[0]);
        Map<MeasurementParam, ParamStats> summary = summaryManager.expSummary(experimentId);

        if (summary.isEmpty()) {
            System.out.println("Нет данных для сводки.");
            return;
        }

        for (Map.Entry<MeasurementParam, ParamStats> entry : summary.entrySet()) {
            ParamStats stats = entry.getValue();
            System.out.printf("%s: count=%d min=%.2f max=%.2f avg=%.2f%n",
                    entry.getKey(), stats.count(), stats.min(), stats.max(), stats.avg());
        }
    }
}