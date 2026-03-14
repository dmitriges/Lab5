package ru.itmo.services;

import ru.itmo.model.MeasurementParam;
import ru.itmo.model.Run;
import ru.itmo.model.RunResult;

import java.util.*;
import java.util.stream.Collectors;

//класс отвечающий за сводку по эксперименту посредством команды expSummary
//Как он работает?
//Принимает experimentId.
//
//Проверяет существование эксперимента через ExperimentManager.exists().
//
//Получает все запуски этого эксперимента через RunManager.listByExperiment().
//
//Собирает все результаты этих запусков через RunResultManager.listByRun().
//
//Группирует результаты по параметру (MeasurementParam).
//
//Для каждой группы вычисляет count, min, max, avg.
//
//Возвращает карту (Map<MeasurementParam, ParamStats>), где ParamStats – это record с полями статистики.
public class SummaryManager {

    private final ExperimentManager experimentManager;
    private final RunManager runManager;
    private final RunResultManager runResultManager;

    public SummaryManager(ExperimentManager experimentManager,
                          RunManager runManager,
                          RunResultManager runResultManager) {
        this.experimentManager = Objects.requireNonNull(experimentManager);
        this.runManager = Objects.requireNonNull(runManager);
        this.runResultManager = Objects.requireNonNull(runResultManager);
    }


    public Map<MeasurementParam, ParamStats> expSummary(long experimentId) {
        // 1) Проверяем, что experiment существует (как в ТЗ "experiment не найден")
        if (!experimentManager.exists(experimentId)) {
            throw new NoSuchElementException("Experiment не найден: id=" + experimentId);
        }

        // 2) Берем все runs этого эксперимента
        List<Run> runs = runManager.listByExperiment(experimentId);

        // Если запусков нет — возвращаем пустую сводку
        if (runs.isEmpty()) {
            return Map.of();
        }

        // 3) Собираем все результаты со всех runs в один список
        List<RunResult> allResults = new ArrayList<>();
        for (Run run : runs) {
            allResults.addAll(runResultManager.listByRun(run.getId()));
        }

        if (allResults.isEmpty()) {
            return Map.of();
        }

        // 4) Группируем по параметру
        Map<MeasurementParam, List<RunResult>> byParam = allResults.stream()
                .collect(Collectors.groupingBy(RunResult::getParam));

        // 5) Считаем count/min/max/avg для каждой группы
        Map<MeasurementParam, ParamStats> summary = new EnumMap<>(MeasurementParam.class);

        for (Map.Entry<MeasurementParam, List<RunResult>> entry : byParam.entrySet()) {
            MeasurementParam param = entry.getKey();
            List<RunResult> results = entry.getValue();

            long count = results.size();

            double min = results.stream()
                    .mapToDouble(RunResult::getValue)
                    .min()
                    .orElse(Double.NaN);

            double max = results.stream()
                    .mapToDouble(RunResult::getValue)
                    .max()
                    .orElse(Double.NaN);

            double avg = results.stream()
                    .mapToDouble(RunResult::getValue)
                    .average()
                    .orElse(Double.NaN);

            summary.put(param, new ParamStats(count, min, max, avg));
        }

        // Возвращаем в удобном виде.
        // EnumMap хранит ключи-перечисления компактно и быстро.
        return summary;
    }
}