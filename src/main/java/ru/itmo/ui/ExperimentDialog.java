package ru.itmo.ui;

import javafx.scene.control.TextInputDialog;
import ru.itmo.model.Experiment;

import java.util.Optional;

public final class ExperimentDialog {
    private ExperimentDialog() {
    }

    public static ExperimentInputData showAddDialog() {
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Add Experiment");
        nameDialog.setHeaderText("Создание эксперимента");
        nameDialog.setContentText("Name:");

        Optional<String> nameResult = nameDialog.showAndWait();
        if (nameResult.isEmpty() || nameResult.get().isBlank()) {
            return null;
        }

        TextInputDialog descriptionDialog = new TextInputDialog();
        descriptionDialog.setTitle("Add Experiment");
        descriptionDialog.setHeaderText("Создание эксперимента");
        descriptionDialog.setContentText("Description:");

        Optional<String> descriptionResult = descriptionDialog.showAndWait();
        if (descriptionResult.isEmpty()) {
            return null;
        }

        TextInputDialog ownerDialog = new TextInputDialog();
        ownerDialog.setTitle("Add Experiment");
        ownerDialog.setHeaderText("Создание эксперимента");
        ownerDialog.setContentText("Owner:");

        Optional<String> ownerResult = ownerDialog.showAndWait();
        if (ownerResult.isEmpty() || ownerResult.get().isBlank()) {
            return null;
        }

        return new ExperimentInputData(
                nameResult.get(),
                descriptionResult.get(),
                ownerResult.get()
        );
    }

    public static ExperimentEditData showEditDialog(Experiment experiment) {
        TextInputDialog nameDialog = new TextInputDialog(experiment.getName());
        nameDialog.setTitle("Edit Experiment");
        nameDialog.setHeaderText("Редактирование эксперимента");
        nameDialog.setContentText("Name:");

        Optional<String> nameResult = nameDialog.showAndWait();
        if (nameResult.isEmpty() || nameResult.get().isBlank()) {
            return null;
        }

        TextInputDialog descriptionDialog = new TextInputDialog(experiment.getDescription());
        descriptionDialog.setTitle("Edit Experiment");
        descriptionDialog.setHeaderText("Редактирование эксперимента");
        descriptionDialog.setContentText("Description:");

        Optional<String> descriptionResult = descriptionDialog.showAndWait();
        if (descriptionResult.isEmpty()) {
            return null;
        }

        return new ExperimentEditData(
                nameResult.get(),
                descriptionResult.get()
        );
    }

    public record ExperimentInputData(String name, String description, String owner) {
    }

    public record ExperimentEditData(String name, String description) {
    }
}
