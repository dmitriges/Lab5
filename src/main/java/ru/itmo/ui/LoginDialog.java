package ru.itmo.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.itmo.services.UserManager;

import java.util.Optional;


 //Диалоговое окно входа / регистрации.
 //Возвращает логин успешно вошедшего пользователя.

public class LoginDialog extends Stage {

    private final UserManager userStorage;
    private TextField loginField;
    private PasswordField passwordField;
    private Label messageLabel;
    private String loggedInUser = null;

    public LoginDialog(UserManager userStorage) {
        this.userStorage = userStorage;
        setTitle("Вход в систему");
        initModality(Modality.APPLICATION_MODAL);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        Label loginLabel = new Label("Логин:");
        grid.add(loginLabel, 0, 0);
        loginField = new TextField();
        loginField.setPromptText("Введите логин");
        grid.add(loginField, 1, 0);

        Label passwordLabel = new Label("Пароль:");
        grid.add(passwordLabel, 0, 1);
        passwordField = new PasswordField();
        passwordField.setPromptText("Введите пароль");
        grid.add(passwordField, 1, 1);

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");
        grid.add(messageLabel, 1, 2);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button loginButton = new Button("Войти");
        Button registerButton = new Button("Регистрация");
        buttonBox.getChildren().addAll(loginButton, registerButton);
        grid.add(buttonBox, 1, 3);

        loginButton.setOnAction(e -> handleLogin());
        registerButton.setOnAction(e -> handleRegister());

        Scene scene = new Scene(grid, 350, 200);
        setScene(scene);
        setResizable(false);
    }

    private void handleLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText();
        if (login.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Логин и пароль обязательны");
            return;
        }
        try {
            userStorage.authenticate(login, password);
            loggedInUser = login;
            close(); // успех – закрываем окно
        } catch (IllegalArgumentException ex) {
            messageLabel.setText("Ошибка: " + ex.getMessage());
        }
    }

    private void handleRegister() {
        String login = loginField.getText().trim();
        String password = passwordField.getText();
        if (login.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Логин и пароль обязательны");
            return;
        }
        try {
            userStorage.register(login, password);
            messageLabel.setText("Регистрация успешна. Теперь войдите.");
            // очищаем поля пароля для явного входа
            passwordField.clear();
        } catch (IllegalArgumentException ex) {
            messageLabel.setText("Ошибка: " + ex.getMessage());
        }
    }


    //Показывает диалог и возвращает Optional с логином, если вход выполнен.

    public Optional<String> showAndWaitForLogin() {
        super.showAndWait();
        return Optional.ofNullable(loggedInUser);
    }
}