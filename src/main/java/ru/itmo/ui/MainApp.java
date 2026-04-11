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

    private final ExperimentManager experimentManager = new ExperimentManager();
    private final RunManager runManager = new RunManager(experimentManager);
    private final RunResultManager runResultManager = new RunResultManager(runManager);
    private final FileStorage fileStorage = new FileStorage(
            experimentManager,
            runManager,
            runResultManager
    );

    private final TableView<Experiment> tableView = new TableView<>();

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        configureTable();
        loadDefaultFileOnStart();

        ToolBar toolBar = createToolBar(stage);

        root.setTop(toolBar);
        root.setCenter(tableView);

        refreshTable();

        Scene scene = new Scene(root, 1200, 650);
        stage.setTitle("Experiment Manager");
        stage.setScene(scene);
        stage.show();
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
        try {
            fileStorage.save(DEFAULT_XML_PATH);
            AlertUtil.showInfo("Данные сохранены в data.xml");
        } catch (Exception e) {
            AlertUtil.showError("Ошибка сохранения: " + e.getMessage());
        }
    }

    private void saveAs(Stage stage) {
        FileChooser fileChooser = createXmlFileChooser("Сохранить XML");
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }

        try {
            fileStorage.save(file.toPath());
            AlertUtil.showInfo("Данные сохранены в файл: " + file.getAbsolutePath());
        } catch (Exception e) {
            AlertUtil.showError("Ошибка сохранения: " + e.getMessage());
        }
    }

    private void loadDefaultFile() {
        try {
            fileStorage.loadIntoManagers(DEFAULT_XML_PATH);
            AlertUtil.showInfo("Данные загружены из data.xml. Нажмите Refresh для обновления таблицы.");
        } catch (Exception e) {
            AlertUtil.showError("Ошибка загрузки: " + e.getMessage());
        }
    }

    private void loadFrom(Stage stage) {
        FileChooser fileChooser = createXmlFileChooser("Загрузить XML");
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        try {
            fileStorage.loadIntoManagers(file.toPath());
            AlertUtil.showInfo("Данные загружены из файла: " + file.getAbsolutePath() + ". Нажмите Refresh для обновления таблицы.");
        } catch (Exception e) {
            AlertUtil.showError("Ошибка загрузки: " + e.getMessage());
        }
    }

    private void loadDefaultFileOnStart() {
        if (!Files.exists(DEFAULT_XML_PATH)) {
            return;
        }

        try {
            fileStorage.loadIntoManagers(DEFAULT_XML_PATH);
        } catch (Exception e) {
            AlertUtil.showError("Ошибка загрузки при старте: " + e.getMessage());
        }
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