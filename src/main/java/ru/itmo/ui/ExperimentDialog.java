package ru.itmo.ui;

import javafx.scene.control.TextInputDialog;
import ru.itmo.model.Experiment;

import java.util.Optional;

public final class ExperimentDialog {
    private ExperimentDialog() {
    }

    public static ExperimentInputData showAddDialog() {
        String name = askRequiredText(
                "Add Experiment",
                "Создание эксперимента",
                "Name:",
                ""
        );
        if (name == null) {
            return null;
        }

        String description = askOptionalText(
                "Add Experiment",
                "Создание эксперимента",
                "Description:",
                ""
        );
        if (description == null) {
            return null;
        }

        String owner = askRequiredText(
                "Add Experiment",
                "Создание эксперимента",
                "Owner:",
                ""
        );
        if (owner == null) {
            return null;
        }

        return new ExperimentInputData(name, description, owner);
    }

    public static ExperimentEditData showEditDialog(Experiment experiment) {
        String name = askRequiredText(
                "Edit Experiment",
                "Редактирование эксперимента",
                "Name:",
                experiment.getName()
        );
        if (name == null) {
            return null;
        }

        String description = askOptionalText(
                "Edit Experiment",
                "Редактирование эксперимента",
                "Description:",
                experiment.getDescription()
        );
        if (description == null) {
            return null;
        }

        return new ExperimentEditData(name, description);
    }

    private static String askRequiredText(String title, String header, String content, String initialValue) {
        TextInputDialog dialog = new TextInputDialog(initialValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty() || result.get().isBlank()) {
            return null;
        }
        return result.get();
    }

    private static String askOptionalText(String title, String header, String content, String initialValue) {
        TextInputDialog dialog = new TextInputDialog(initialValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public record ExperimentInputData(String name, String description, String owner) {
    }

    public record ExperimentEditData(String name, String description) {
    }
}