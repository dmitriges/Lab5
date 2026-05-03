package ru.itmo.ui;

import javafx.scene.control.TextInputDialog;
import ru.itmo.model.Experiment;
import ru.itmo.ui.util.AlertUtil;

import java.util.Optional;

public final class ExperimentDialog {
    private ExperimentDialog() {}

    public static ExperimentInputData showAddDialog() {
        String name = askRequiredText("Создание эксперимента", "Name:", "", 128);
        if (name == null) return null;

        String description = askOptionalText("Создание эксперимента", "Description:", "", 512);
        if (description == null) return null;

        return new ExperimentInputData(name, description);
    }

    public static ExperimentEditData showEditDialog(Experiment experiment) {
        String name = askRequiredText("Редактирование эксперимента", "Name:", experiment.getName(), 128);
        if (name == null) return null;

        String description = askOptionalText("Редактирование эксперимента", "Description:", experiment.getDescription(), 512);
        if (description == null) return null;

        return new ExperimentEditData(name, description);
    }

    /**
     * Запрашивает непустую строку с ограничением длины.
     * Показывает диалог снова при некорректном вводе.
     * Возвращает null только если пользователь нажал Cancel.
     */
    private static String askRequiredText(String header, String content, String initialValue, int maxLength) {
        while (true) {
            TextInputDialog dialog = new TextInputDialog(initialValue);
            dialog.setTitle("Ввод данных");
            dialog.setHeaderText(header);
            dialog.setContentText(content);

            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty()) {
                return null; // Cancel
            }
            String input = result.get().trim();

            // Проверка на пустоту
            if (input.isEmpty()) {
                AlertUtil.showError("Поле не может быть пустым. Пожалуйста, введите значение.");
                // После закрытия ошибки снова откроется диалог
                continue;
            }
            // Проверка длины
            if (input.length() > maxLength) {
                AlertUtil.showError("Значение слишком длинное (макс. " + maxLength + " символов). Введите заново.");
                continue;
            }
            return input;
        }
    }

    /**
     * Запрашивает необязательную строку (может быть пустой), но с ограничением длины.
     * Возвращает null при Cancel или пустую строку при пустом вводе ("").
     */
    private static String askOptionalText(String header, String content, String initialValue, int maxLength) {
        while (true) {
            TextInputDialog dialog = new TextInputDialog(initialValue);
            dialog.setTitle("Ввод данных");
            dialog.setHeaderText(header);
            dialog.setContentText(content);

            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty()) return null;

            String input = result.get().trim();
            if (input.isEmpty()) {
                return ""; // Пустое значение разрешено
            }
            if (input.length() > maxLength) {
                AlertUtil.showError("Значение слишком длинное (макс. " + maxLength + " символов). Введите заново.");
                continue;
            }
            return input;
        }
    }

    public record ExperimentInputData(String name, String description) {}
    public record ExperimentEditData(String name, String description) {}
}



// Старая реализация на всякий случай - новая содержит циклы для того чтобы не выходить при пустой строке ввода,
// а получить понятное сообщение об ошибке
/*
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
}*/
