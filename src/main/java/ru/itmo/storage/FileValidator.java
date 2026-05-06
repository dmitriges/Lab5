/*
package ru.itmo.storage;

import ru.itmo.model.Experiment;
import ru.itmo.model.Run;
import ru.itmo.model.RunResult;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public final class FileValidator {
    // TODO нужен ли он вообще? ( метод FileValidator )
    private FileValidator() {
    }

    public static void validate(AppData data) {
        if (data == null) {
            throw new IllegalArgumentException("Данные файла отсутствуют");
        }

        List<Experiment> experiments = data.getExperiments();
        List<Run> runs = data.getRuns();
        List<RunResult> results = data.getResults();

        if (experiments == null || runs == null || results == null) {
            throw new IllegalArgumentException("Одна или несколько коллекций в файле отсутствуют");
        }

        Set<Long> experimentIds = new HashSet<>();
        for (Experiment experiment : experiments) {
            if (experiment == null) {
                throw new IllegalArgumentException("Обнаружен null в experiments");
            }// проверяет на отсутствие null

            validateExperiment(experiment);//проверка полей

            if (!experimentIds.add(experiment.getId())) {
                throw new IllegalArgumentException("Дублирующийся id Experiment: " + experiment.getId());
            } //исключение на дубликат, если совпадает
        }

        Set<Long> runIds = new HashSet<>();
        for (Run run : runs) {
            if (run == null) {
                throw new IllegalArgumentException("Обнаружен null в runs");
            }

            validateRun(run);

            if (!runIds.add(run.getId())) {
                throw new IllegalArgumentException("Дублирующийся id Run: " + run.getId());
            }

            if (!experimentIds.contains(run.getExperimentId())) {
                throw new NoSuchElementException(
                        "Run с id=" + run.getId() + " ссылается на несуществующий Experiment id=" + run.getExperimentId()
                );
            }
        }

        Set<Long> resultIds = new HashSet<>();
        for (RunResult result : results) {
            if (result == null) {
                throw new IllegalArgumentException("Обнаружен null в results");
            }

            validateRunResult(result);

            if (!resultIds.add(result.getId())) {
                throw new IllegalArgumentException("Дублирующийся id RunResult: " + result.getId());
            }

            if (!runIds.contains(result.getRunId())) {
                throw new NoSuchElementException(
                        "RunResult с id=" + result.getId() + " ссылается на несуществующий Run id=" + result.getRunId()
                );
            }
        }
    }

    private static void validateExperiment(Experiment experiment) {
        if (experiment.getId() <= 0) {
            throw new IllegalArgumentException("Некорректный id Experiment: " + experiment.getId());
        }
        if (experiment.getCreatedAt() == null) {
            throw new IllegalArgumentException("createdAt у Experiment не может быть null");
        }
        if (experiment.getUpdatedAt() == null) {
            throw new IllegalArgumentException("updatedAt у Experiment не может быть null");
        }

        // Валидация, так чтобы updatedAt корректно обновлялся - теперь не вызываем сеттеры,
        // которые обновляют, а через отдельные методы валидируем.
        Experiment.validateFields(experiment.getName(), experiment.getDescription(), experiment.getOwnerUsername());

    }

    private static void validateRun(Run run) {
        if (run.getId() <= 0) {
            throw new IllegalArgumentException("Некорректный id Run: " + run.getId());
        }
        if (run.getExperimentId() <= 0) {
            throw new IllegalArgumentException("Некорректный experimentId у Run: " + run.getExperimentId());
        }
        if (run.getCreatedAt() == null) {
            throw new IllegalArgumentException("createdAt у Run не может быть null");
        }
// Валидация, так чтобы updatedAt корректно обновлялся - теперь не вызываем сеттеры,
        // которые обновляют, а через отдельные методы валидируем.
        Run.validateFields(run.getName(), run.getOperatorName());
    }

    private static void validateRunResult(RunResult result) {
        if (result.getId() <= 0) {
            throw new IllegalArgumentException("Некорректный id RunResult: " + result.getId());
        }
        if (result.getRunId() <= 0) {
            throw new IllegalArgumentException("Некорректный runId у RunResult: " + result.getRunId());
        }
        if (result.getCreatedAt() == null) {
            throw new IllegalArgumentException("createdAt у RunResult не может быть null");
        }
        if (result.getParam() == null) {
            throw new IllegalArgumentException("param у RunResult не может быть null");
        }

        // Валидация, так чтобы updatedAt корректно обновлялся - теперь не вызываем сеттеры,
        // которые обновляют, а через отдельные методы валидируем.
        RunResult.validateFields(result.getParam(), result.getValue(), result.getUnit(), result.getComment());
    }
}*/
