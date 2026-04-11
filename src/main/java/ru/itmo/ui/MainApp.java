package ru.itmo.ui;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.itmo.model.Experiment;
import ru.itmo.services.ExperimentManager;
import ru.itmo.services.RunManager;
import ru.itmo.services.RunResultManager;
import ru.itmo.storage.FileStorage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MainApp extends Application { //приложение на основе java-fx

    private static final Path DEFAULT_XML_PATH = Path.of("data.xml"); //стандартный XML-файл, с которым приложение работает по умолчанию.

    private final javafx.scene.control.ProgressBar progressBar = new javafx.scene.control.ProgressBar();
    private final javafx.scene.control.Label statusLabel = new javafx.scene.control.Label("Готов");
    private javafx.concurrent.Task<?> currentTask;

    private final ExperimentManager experimentManager = new ExperimentManager();
    private final RunManager runManager = new RunManager(experimentManager);
    private final RunResultManager runResultManager = new RunResultManager(runManager);
    private final FileStorage fileStorage = new FileStorage(
            experimentManager,
            runManager,
            runResultManager
    );

    private final TableView<Experiment> tableView = new TableView<>();


    private <T> void runAsyncTask(javafx.concurrent.Task<T> task, Runnable onSuccess, String successMessage) {
        if (currentTask != null && !currentTask.isDone()) {
            AlertUtil.showError("Предыдущая операция ещё не завершена. Подождите."); //Если сейчас уже идёт одна фоновая операция, вторую запускать нельзя.
            return;
        }
        currentTask = task;
        // Блокируем все кнопки на тулбаре (чтобы не нажали повторно)
        disableToolbarButtons(true);
        progressBar.setVisible(true);
        statusLabel.setVisible(true);
        progressBar.progressProperty().bind(task.progressProperty()); //Пусть progressBar автоматически показывает тот прогресс, который сообщает task
        statusLabel.textProperty().bind(task.messageProperty());

        task.setOnSucceeded(e -> {
            if (onSuccess != null) onSuccess.run();
            if (successMessage != null) AlertUtil.showInfo(successMessage);
            finishAsyncTask();
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            AlertUtil.showError("Ошибка: " + (ex != null ? ex.getMessage() : "неизвестная ошибка")); //если ex != null, взять ex.getMessage() иначе показать "неизвестная ошибка"
            finishAsyncTask();
        });
        new Thread(task).start();
    }

    private void finishAsyncTask() {
        disableToolbarButtons(false);
        progressBar.setVisible(false);
        statusLabel.setVisible(false);
        progressBar.progressProperty().unbind();
        statusLabel.textProperty().unbind();
        currentTask = null;
    }

    private void disableToolbarButtons(boolean disable) {
        // Находим тулбар (можно сохранить ссылку при создании)
        // Предположим, что сохранили в поле ToolBar toolbar;
        if (toolbar != null) {
            toolbar.getItems().forEach(node -> node.setDisable(disable));
        }
    }
    private ToolBar toolbar; // поле класса

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        configureTable();
        // статусная панель внизу
        javafx.scene.layout.HBox statusBox = new javafx.scene.layout.HBox(10, statusLabel, progressBar);
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

        loadDefaultFileOnStartAsync(); // загружаем асинхронно
    }

    private void configureTable() {
        TableColumn<Experiment, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getId()));

        TableColumn<Experiment, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

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

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(event -> handleClear());

        MenuButton saveMenuButton = new MenuButton("Save");
        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction(event -> saveDefaultFile());

        MenuItem saveAsItem = new MenuItem("Save As");
        saveAsItem.setOnAction(event -> saveAs(stage));

        saveMenuButton.getItems().addAll(saveItem, saveAsItem);

        MenuButton loadMenuButton = new MenuButton("Load");
        MenuItem loadItem = new MenuItem("Load");
        loadItem.setOnAction(event -> loadDefaultFile());

        MenuItem loadFromItem = new MenuItem("Load From");
        loadFromItem.setOnAction(event -> loadFrom(stage));

        loadMenuButton.getItems().addAll(loadItem, loadFromItem);

        return new ToolBar(
                refreshButton,
                addButton,
                editButton,
                deleteButton,
                clearButton,
                saveMenuButton,
                loadMenuButton
        );
    }

    private void handleAdd() {
        ExperimentDialog.ExperimentInputData data = ExperimentDialog.showAddDialog();
        if (data == null) {
            return;
        }

        try {
            experimentManager.add(
                    data.name(),
                    data.description(),
                    data.owner()
            );
        } catch (Exception e) {
            AlertUtil.showError("Ошибка добавления: " + e.getMessage());
        }
    }

    private void handleEdit() {
        Experiment selected = getSelectedExperimentOrShowError();
        if (selected == null) {
            return;
        }

        ExperimentDialog.ExperimentEditData data = ExperimentDialog.showEditDialog(selected);
        if (data == null) {
            return;
        }

        try {
            experimentManager.update(
                    selected.getId(),
                    data.name(),
                    data.description()
            );
        } catch (Exception e) {
            AlertUtil.showError("Ошибка редактирования: " + e.getMessage());
        }
    }

    private void handleDelete() {
        Experiment selected = getSelectedExperimentOrShowError();
        if (selected == null) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение удаления");
        confirm.setHeaderText("Удаление эксперимента");
        confirm.setContentText("Удалить эксперимент с id = " + selected.getId() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            experimentManager.remove(selected.getId());
        } catch (Exception e) {
            AlertUtil.showError("Ошибка удаления: " + e.getMessage());
        }
    }

    private void handleClear() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение очистки");
        confirm.setHeaderText("Очистка данных");
        confirm.setContentText("Удалить все эксперименты из текущей сессии?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        runResultManager.clear();
        runManager.clear();
        experimentManager.clear();
    }

    private void saveDefaultFile() {
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Сохранение в data.xml...");
                fileStorage.save(DEFAULT_XML_PATH);
                updateMessage("Сохранено");
                return null;
            }
        };
        task.setOnSucceeded(e -> AlertUtil.showInfo("Данные сохранены в data.xml"));
        task.setOnFailed(e -> AlertUtil.showError("Ошибка сохранения: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    private void saveAs(Stage stage) {
        FileChooser fileChooser = createXmlFileChooser("Сохранить XML");
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Сохранение в " + file.getName());
                fileStorage.save(file.toPath());
                updateMessage("Сохранено");
                return null;
            }
        };
        task.setOnSucceeded(e -> AlertUtil.showInfo("Данные сохранены в файл: " + file.getAbsolutePath()));
        task.setOnFailed(e -> AlertUtil.showError("Ошибка сохранения: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    private void loadDefaultFile() {
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Загрузка из data.xml...");
                fileStorage.loadIntoManagers(DEFAULT_XML_PATH);
                updateMessage("Загрузка завершена");
                return null;
            }
        };
        task.setOnSucceeded(e -> AlertUtil.showInfo("Данные загружены из data.xml. Нажмите Refresh."));
        task.setOnFailed(e -> AlertUtil.showError("Ошибка загрузки: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    private void loadFrom(Stage stage) {
        FileChooser fileChooser = createXmlFileChooser("Загрузить XML");
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return;
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Загрузка из " + file.getName());
                fileStorage.loadIntoManagers(file.toPath());
                updateMessage("Загрузка завершена");
                return null;
            }
        };
        task.setOnSucceeded(e -> AlertUtil.showInfo("Данные загружены из файла: " + file.getAbsolutePath() + ". Нажмите Refresh."));
        task.setOnFailed(e -> AlertUtil.showError("Ошибка загрузки: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    private void loadDefaultFileOnStartAsync() {
        if (!Files.exists(DEFAULT_XML_PATH)) return;
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Загрузка данных при старте...");
                fileStorage.loadIntoManagers(DEFAULT_XML_PATH);
                updateMessage("Загрузка завершена");
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            // Обновлять таблицу не нужно, так как пользователь сам нажмёт Refresh
            AlertUtil.showInfo("Данные загружены из data.xml. Нажмите Refresh.");
        });
        task.setOnFailed(e -> AlertUtil.showError("Ошибка загрузки при старте: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    private FileChooser createXmlFileChooser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML files", "*.xml")
        );
        return fileChooser;
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
        List<Experiment> experiments = experimentManager.getAll().stream()
                .sorted(Comparator.comparingLong(Experiment::getId))
                .toList();

        tableView.setItems(FXCollections.observableArrayList(experiments));
        tableView.refresh();
    }

    public static void main(String[] args) {
        launch(args);
    }
}