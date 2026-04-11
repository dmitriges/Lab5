package ru.itmo.ui;

import javafx.scene.control.TextInputDialog;//стандартный диалог JavaFX с полем ввода текста
import ru.itmo.model.Experiment;

import java.util.Optional;//Optional — контейнер, который может содержать значение или быть пустым.
// Используется для безопасной обработки результата диалога.

//диалоги для создания и редактирования экспериментов
public final class ExperimentDialog {
    private ExperimentDialog() {
    }
// Метод вызывается, когда пользователь нажимает кнопку "Add" в главном окне
    // возвращает ExperimentInputData контейнер с тремя значениями name... dicr owner
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

    // общий метод для диалогов выше
    private static String askRequiredText(String title, String header, String content, String initialValue) {
        TextInputDialog dialog = new TextInputDialog(initialValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        Optional<String> result = dialog.showAndWait();

        if (result.isEmpty() || result.get().isBlank()) {
            // из эмпти метод из опшинал, бланк из CharSequence, реализованный в String
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
        //get()	Выбрасывает NoSuchElementException
        //orElse(null)	Возвращает null (без исключения)
    }

    public record ExperimentInputData(String name, String description, String owner) {
    }

    public record ExperimentEditData(String name, String description) {
    }
}