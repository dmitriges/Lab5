package ru.itmo.ui;

import javafx.scene.control.Alert;

public final class AlertUtil {
    private AlertUtil() {
    }

    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        //Alert - встроенный класс JavaFX для создания стандартных диалоговых окон-уведомлений.
        alert.setTitle("Ошибка");//Alert наследует свои основные свойства от родительского класса Dialog
        alert.setHeaderText("Операция не выполнена");
        alert.setContentText(message);
        alert.showAndWait();// Высвечивает окно, останавливает работу кода
    }//showAndWait() возвращает Optional<ButtonType>
    // какой именно button нажал пользователь (OK, Cancel, Yes, No и т.д.).

    public static void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
