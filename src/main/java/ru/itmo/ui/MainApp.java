package ru.itmo.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.itmo.model.Experiment;
import ru.itmo.repository.ExperimentRepository;
import ru.itmo.repository.RunRepository;
import ru.itmo.repository.RunResultRepository;
import ru.itmo.repository.UserRepository;
import ru.itmo.services.ExperimentManager;
import ru.itmo.services.RunManager;
import ru.itmo.services.RunResultManager;
import ru.itmo.services.UserManager;
import ru.itmo.sync.DatabaseChangeNotifier; // ДОП
import ru.itmo.ui.util.AlertUtil;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import ru.itmo.config.DatabaseInitializer; // ДОП

public class MainApp extends Application { //приложение на основе java-fx

    // --- Менеджеры (инициализируются в start) ---
    private ExperimentManager experimentManager;
    private RunManager runManager;
    private RunResultManager runResultManager;
    private DatabaseChangeNotifier databaseChangeNotifier; // ДОП

    // Текущий пользователь (логин)
    private String currentUser;

    // UI-компоненты
    // создает главную табличку
    private final TableView<Experiment> tableView = new TableView<>();
    //создаёт объект пути к файлу в текущей рабочей директории.
    private final ProgressBar progressBar = new javafx.scene.control.ProgressBar();
    private ToolBar toolbar;
    private final Label statusLabel = new javafx.scene.control.Label("Готов");

    //поле которое нужно было для ранних этапов и отключено для этапа 6
    /*private static final Path DEFAULT_XML_PATH = Path.of("data.xml"); //стандартный XML-файл, с которым приложение работает по умолчанию.*/

   /* private <T> void runAsyncTask(Task<T> task, String successMessage) {
        if (currentTask != null && !currentTask.isDone()) {
            AlertUtil.showError("Предыдущая операция ещё не завершена. Подождите.");
            //Если сейчас уже идёт одна фоновая операция, вторую запускать нельзя.
            return;
        }
        currentTask = task;
        // Блокируем все кнопки на тулбаре (чтобы не нажали повторно)
        disableToolbarButtons(true);
        progressBar.setVisible(true);
        statusLabel.setVisible(true);
        progressBar.progressProperty().bind(task.progressProperty());

        //Пусть progressBar автоматически показывает тот прогресс, который сообщает task
        statusLabel.textProperty().bind(task.messageProperty());

        //Task — это класс из javafx.concurrent.
        //Он имеет несколько свойств (properties), одно из них — onSucceeded
        task.setOnSucceeded(workerStateEvent -> {
            if (successMessage != null) AlertUtil.showInfo(successMessage);
            finishAsyncTask();
        });
        task.setOnFailed(workerStateEvent -> {
            // метод класса Task, который устанавливает обработчик, вызываемый, когда задача завершается с ошибкой
            Throwable ex = task.getException();//возвращает исключение, которое было выброшено внутри call setOnFailed
            AlertUtil.showError("Ошибка: " + (ex != null ? ex.getMessage() : "неизвестная ошибка"));
            //форматирование ошибки+ диалоговое окно
            finishAsyncTask();// разблокирует кнопки прячет бар прогресса
        });
        new Thread(task).start();
        // мы вынесли в отдельный поток долгую операцию чтобы программа не зависала на долгой операции
    }*/
/*
    private void finishAsyncTask() {
        disableToolbarButtons(false);
        progressBar.setVisible(false);
        statusLabel.setVisible(false);
        progressBar.progressProperty().unbind();
        statusLabel.textProperty().unbind();
        currentTask = null;
    }*/

    /*private void disableToolbarButtons(boolean disable) {
        // Находим тулбар (можно сохранить ссылку при создании)
        // Предположим, что сохранили в поле ToolBar toolbar;
        if (toolbar != null) {
            toolbar.getItems().forEach(node -> node.setDisable(disable));
        }
    }
    private ToolBar toolbar; // поле класса*/

    @Override
    public void start(Stage stage) {
        // --- Инициализация репозиториев и менеджеров ---
        DatabaseInitializer.initialize();
// ДОП

        ExperimentRepository experimentRepo = new ExperimentRepository();
        RunRepository runRepo = new RunRepository();
        RunResultRepository resultRepo = new RunResultRepository();
        UserRepository userRepo = new UserRepository();

        experimentManager = new ExperimentManager(experimentRepo);
        runManager = new RunManager(runRepo, experimentManager);
        runResultManager = new RunResultManager(resultRepo, runManager);
        UserManager userManager = new UserManager(userRepo);

        LoginDialog loginDialog = new LoginDialog(userManager);
        Optional<String> loginResult = loginDialog.showAndWaitForLogin();
        if (loginResult.isEmpty()) {
            // Если окно закрыли без входа – завершаем приложение
            System.exit(0);
        }
        currentUser = loginResult.get();

        // построение UI
        BorderPane root = new BorderPane();
        configureTable();
        // статусная панель внизу
        HBox statusBox = new HBox(10, statusLabel, progressBar);
        statusBox.setStyle("-fx-padding: 5;");
        progressBar.setVisible(false);
        statusLabel.setVisible(false);
        root.setBottom(statusBox);

        toolbar = createToolBar(stage); // сохраняем
        root.setTop(toolbar);
        root.setCenter(tableView);
        refreshTable();

        Scene scene = new Scene(root, 1200, 650);
        stage.setTitle("Experiment Manager");
        stage.setScene(scene);
        stage.show();
        startRealtimeSync(); // ДОП

    }

    private void configureTable() {
        TableColumn<Experiment, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getId()));
//cellData – это объект типа CellDataFeatures<Experiment, Long>

        // делаем так, чтобы если name слишком длинное то был красивый перенос
        TableColumn<Experiment, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        nameColumn.setCellFactory(column -> new TableCell<Experiment, String>() {
            private final Text text = new Text();
            {
                text.wrappingWidthProperty().bind(column.widthProperty());
                setGraphic(text);//заставляет текст переноситься,
                // когда он превышает текущую ширину колонки.
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    text.setText(null);
                } else {
                    text.setText(item);
                }
            }
        });
        nameColumn.setPrefWidth(180);  // ширина колонки


        TableColumn<Experiment, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription()));

        TableColumn<Experiment, String> ownerColumn = new TableColumn<>("Owner");
        ownerColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOwnerUsername()));

        TableColumn<Experiment, String> createdAtColumn = new TableColumn<>("Created At");
        createdAtColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getCreatedAt())));

        TableColumn<Experiment, String> updatedAtColumn = new TableColumn<>("Updated At");
        updatedAtColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getUpdatedAt())));

        idColumn.setPrefWidth(70);
        nameColumn.setPrefWidth(180);
        descriptionColumn.setPrefWidth(260);
        ownerColumn.setPrefWidth(140);
        createdAtColumn.setPrefWidth(240);
        updatedAtColumn.setPrefWidth(240);
        tableView.getColumns().setAll(
                idColumn,
                nameColumn,
                descriptionColumn,
                ownerColumn,
                createdAtColumn,
                updatedAtColumn
        );
        tableView.setFixedCellSize(-1);// отключает фиксированную высоту строки,
        // позволяя строкам растягиваться под контент.
    }

    private ToolBar createToolBar(Stage stage) {
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> refreshTable());

        Button addButton = new Button("Add");
        addButton.setOnAction(event -> handleAdd());

        Button editButton = new Button("Edit");
        editButton.setOnAction(event -> handleEdit());

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> handleDelete());

        Button clearButton = new Button("Clear my Data");
        clearButton.setOnAction(event -> handleClear());

        // Метка текущего пользователя
        Label userLabel = new Label("Вы: " + currentUser);
        userLabel.setStyle("-fx-font-weight: bold;");

        ToolBar bar = new ToolBar(
                refreshButton, addButton, editButton, deleteButton, clearButton,
                new Separator(), userLabel
        );
        return bar;
    }

    private void handleAdd() {
        ExperimentDialog.ExperimentInputData data = ExperimentDialog.showAddDialog();
        if (data == null) return;

        try {
            experimentManager.add(data.name(), data.description(), currentUser);
            refreshTable();   // технически по условию должно быть ручное обновление,
            // но можно оставить автоматическое обновление для удобства;
            // если строго следовать ТЗ – убрать refreshTable() и ждать кнопку Refresh
        } catch (Exception e) {
            AlertUtil.showError("Ошибка добавления: " + e.getMessage());
        }
    }

    private void handleEdit() {
        Experiment selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showError("Сначала выберите эксперимент в таблице.");
            return;
        }
        if (!selected.getOwnerUsername().equals(currentUser)) {
            AlertUtil.showError("У вас нет прав на редактирование этого эксперимента.");
            return;
        }
        ExperimentDialog.ExperimentEditData data = ExperimentDialog.showEditDialog(selected);
        if (data == null) return;
        try {
            experimentManager.update(selected.getId(), data.name(), data.description(), currentUser);
            refreshTable();
        } catch (Exception e) {
            AlertUtil.showError("Ошибка редактирования: " + e.getMessage());
        }
    }


    private void handleDelete() {
        Experiment selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showError("Сначала выберите эксперимент в таблице.");
            return;
        }
        if (!selected.getOwnerUsername().equals(currentUser)) {
            AlertUtil.showError("У вас нет прав на удаление этого эксперимента.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение удаления");
        confirm.setHeaderText("Удаление эксперимента");
        confirm.setContentText("Удалить эксперимент с id = " + selected.getId() + "?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            experimentManager.remove(selected.getId(), currentUser);
            refreshTable();
        } catch (Exception e) {
            AlertUtil.showError("Ошибка удаления: " + e.getMessage());
        }
    }


    private void handleClear() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение очистки");
        confirm.setHeaderText("Очистка ваших данных");
        confirm.setContentText("Удалить все ваши эксперименты, запуски и результаты?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            runResultManager.clearByOwner(currentUser);
            runManager.clearByOwner(currentUser);
            experimentManager.clearByOwner(currentUser);
            refreshTable();
        } catch (Exception e) {
            AlertUtil.showError("Ошибка очистки: " + e.getMessage());
        }
    }

    private Experiment getSelectedExperimentOrShowError() {
        Experiment selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showError("Сначала выберите эксперимент в таблице.");
            return null;
        }
        return selected;
    }

    private void refreshTable() {
        // Перезагружаем кэш экспериментов из БД
        experimentManager.loadAll();

        List<Experiment> experiments = experimentManager.getAll().stream()
                .sorted(Comparator.comparingLong(Experiment::getId))
                .toList();
        tableView.setItems(javafx.collections.FXCollections.observableArrayList(experiments));
        tableView.refresh();
    }
    // ДОП МЕТОДЫ
    private void startRealtimeSync() {
        databaseChangeNotifier = new DatabaseChangeNotifier(() ->
                Platform.runLater(this::reloadDataFromDatabase)
        );
        databaseChangeNotifier.start();
    }

    private void reloadDataFromDatabase() {
        try {
            experimentManager.loadAll();
            runManager.loadAll();
            runResultManager.loadAll();
            refreshTable();
        } catch (RuntimeException e) {
            System.err.println("Realtime refresh error: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        if (databaseChangeNotifier != null) {
            databaseChangeNotifier.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
