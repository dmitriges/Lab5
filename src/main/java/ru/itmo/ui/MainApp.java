package ru.itmo.ui;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

import static javafx.collections.FXCollections.observableArrayList;

public class MainApp extends Application { //приложение на основе java-fx

    private static final Path DEFAULT_XML_PATH = Path.of("data.xml"); //стандартный XML-файл, с которым приложение работает по умолчанию.
//создаёт объект пути к файлу в текущей рабочей директории.
    private final ProgressBar progressBar = new javafx.scene.control.ProgressBar();
    private final Label statusLabel = new javafx.scene.control.Label("Готов");
    private Task<?> currentTask;//Ссылка на текущую фоновую задачу (если она выполняется).

    private final ExperimentManager experimentManager = new ExperimentManager();
    private final RunManager runManager = new RunManager(experimentManager);
    private final RunResultManager runResultManager = new RunResultManager(runManager);
    private final FileStorage fileStorage = new FileStorage(
            experimentManager,
            runManager,
            runResultManager
    );
    private final TableView<Experiment> tableView = new TableView<>();
// создает главную табличку

    private <T> void runAsyncTask(Task<T> task, String successMessage) {
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
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Сохранение в data.xml...");
                fileStorage.save(DEFAULT_XML_PATH);
                updateMessage("Сохранено");
                return null;
            }
        };
        runAsyncTask(task, "Данные сохранены в data.xml");
    }

    private void saveAs(Stage stage) {
        FileChooser fileChooser = createXmlFileChooser("Сохранить XML");
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Сохранение в " + file.getName());
                fileStorage.save(file.toPath());
                updateMessage("Сохранено");
                return null;
            }
        };
        runAsyncTask(task, "Данные сохранены в файл: " + file.getAbsolutePath());
    }

    private void loadDefaultFile() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Загрузка из data.xml...");
                fileStorage.loadIntoManagers(DEFAULT_XML_PATH);
                updateMessage("Загрузка завершена");
                return null;
            }
        };
        runAsyncTask(task, "Данные загружены из data.xml. Нажмите Refresh.");
    }

    private void loadFrom(Stage stage) {
        FileChooser fileChooser = createXmlFileChooser("Загрузить XML");
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return;
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Загрузка из " + file.getName());
                fileStorage.loadIntoManagers(file.toPath());
                updateMessage("Загрузка завершена");
                return null;
            }
        };
        runAsyncTask(task, "Данные загружены из файла: " + file.getAbsolutePath() + ". Нажмите Refresh.");
    }

    private void loadDefaultFileOnStartAsync() {
        if (!Files.exists(DEFAULT_XML_PATH)) return;
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Загрузка данных при старте...");
                fileStorage.loadIntoManagers(DEFAULT_XML_PATH);
                updateMessage("Загрузка завершена");
                return null;
            }
        };
        // Не блокируем кнопки при старте, но используем runAsyncTask
        runAsyncTask(task,  "Данные загружены из data.xml. Нажмите Refresh.");
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

        tableView.setItems(observableArrayList(experiments));// tableView принимает ObservableList,
        // делаем его на основе наших experiments
        tableView.refresh();
    }

    public static void main(String[] args) {
        launch(args);
    }
}