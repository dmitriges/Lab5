package ru.itmo.ui;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ru.itmo.model.Experiment;
import ru.itmo.services.ExperimentManager;

public class MainApp extends Application {

    private final ExperimentManager experimentManager = new ExperimentManager();
    private final ru.itmo.services.RunManager runManager = new ru.itmo.services.RunManager(experimentManager);
    private final ru.itmo.services.RunResultManager runResultManager = new ru.itmo.services.RunResultManager(runManager);
    private final ru.itmo.storage.FileStorage fileStorage = new ru.itmo.storage.FileStorage(
            experimentManager,
            runManager,
            runResultManager
    );

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();

        TableView<Experiment> tableView = new TableView<>();
        tableView.setItems(FXCollections.observableArrayList());

        TableColumn<Experiment, Long> idColumn = new TableColumn<>("ID");
        TableColumn<Experiment, String> nameColumn = new TableColumn<>("Name");
        TableColumn<Experiment, String> ownerColumn = new TableColumn<>("Owner");
        TableColumn<Experiment, String> descriptionColumn = new TableColumn<>("Description");
        TableColumn<Experiment, String> createdAtColumn = new TableColumn<>("Created At");
        TableColumn<Experiment, String> updatedAtColumn = new TableColumn<>("Updated At");


        idColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getId()));

        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        ownerColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOwnerUsername()));

        descriptionColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));

        createdAtColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(cellData.getValue().getCreatedAt())
                ));

        updatedAtColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(cellData.getValue().getUpdatedAt())
                ));

        tableView.getColumns().addAll(idColumn, nameColumn, ownerColumn, descriptionColumn, createdAtColumn, updatedAtColumn);

        Button refreshButton = new Button("Refresh");
        Button addButton = new Button("Add");
        Button deleteButton = new Button("Delete");
        Button editButton = new Button("Edit");
        Button clearButton = new Button("Clear");
        javafx.scene.control.MenuButton saveMenuButton = new javafx.scene.control.MenuButton("Save");
        javafx.scene.control.MenuItem saveItem = new javafx.scene.control.MenuItem("Save");
        javafx.scene.control.MenuItem saveAsItem = new javafx.scene.control.MenuItem("Save As");
        saveMenuButton.getItems().addAll(saveItem, saveAsItem);

        javafx.scene.control.MenuButton loadMenuButton = new javafx.scene.control.MenuButton("Load");
        javafx.scene.control.MenuItem loadItem = new javafx.scene.control.MenuItem("Load");
        javafx.scene.control.MenuItem loadFromItem = new javafx.scene.control.MenuItem("Load From");
        loadMenuButton.getItems().addAll(loadItem, loadFromItem);

        java.nio.file.Path defaultPath = java.nio.file.Path.of("data.xml");
        if (java.nio.file.Files.exists(defaultPath)) {
            try {
                fileStorage.loadIntoManagers(defaultPath);
            } catch (Exception e) {
                AlertUtil.showError("Ошибка загрузки при старте: " + e.getMessage());
            }
        }

        refreshButton.setOnAction(event -> refreshTable(tableView, experimentManager));

        addButton.setOnAction(event -> {
            ExperimentDialog.ExperimentInputData data = ExperimentDialog.showAddDialog();
            if (data == null) {
                return;
            }

            experimentManager.add(
                    data.name(),
                    data.description(),
                    data.owner()
            );
        });

        deleteButton.setOnAction(event -> {
            Experiment selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertUtil.showError("Сначала выберите эксперимент в таблице");
                return;
            }

            javafx.scene.control.Alert confirm = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.CONFIRMATION
            );
            confirm.setTitle("Подтверждение удаления");
            confirm.setHeaderText("Удаление эксперимента");
            confirm.setContentText("Удалить эксперимент с id = " + selected.getId() + "?");

            java.util.Optional<javafx.scene.control.ButtonType> result = confirm.showAndWait();
            if (result.isEmpty() || result.get() != javafx.scene.control.ButtonType.OK) {
                return;
            }

            experimentManager.remove(selected.getId());
        });

        editButton.setOnAction(event -> {
            Experiment selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertUtil.showError("Сначала выберите эксперимент в таблице");
                return;
            }

            ExperimentDialog.ExperimentEditData data = ExperimentDialog.showEditDialog(selected);
            if (data == null) {
                return;
            }

            experimentManager.update(
                    selected.getId(),
                    data.name(),
                    data.description()
            );
        });

        clearButton.setOnAction(event -> {
            javafx.scene.control.Alert confirm = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.CONFIRMATION
            );
            confirm.setTitle("Подтверждение очистки");
            confirm.setHeaderText("Очистка данных");
            confirm.setContentText("Удалить все эксперименты из текущей сессии?");

            java.util.Optional<javafx.scene.control.ButtonType> result = confirm.showAndWait();
            if (result.isEmpty() || result.get() != javafx.scene.control.ButtonType.OK) {
                return;
            }
            runManager.clear();
            runResultManager.clear();
            experimentManager.clear();
        });

        saveItem.setOnAction(event -> {
            try {
                fileStorage.save(java.nio.file.Path.of("data.xml"));
                AlertUtil.showInfo("Данные сохранены в data.xml");
            } catch (Exception e) {
                AlertUtil.showError("Ошибка сохранения: " + e.getMessage());
            }
        });

        saveAsItem.setOnAction(event -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Сохранить XML");
            fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("XML files", "*.xml")
            );

            java.io.File file = fileChooser.showSaveDialog(stage);
            if (file == null) {
                return;
            }

            try {
                fileStorage.save(file.toPath());
                AlertUtil.showInfo("Данные сохранены в файл: " + file.getAbsolutePath());
            } catch (Exception e) {
                AlertUtil.showError("Ошибка сохранения: " + e.getMessage());
            }
        });

        loadItem.setOnAction(event -> {
            try {
                fileStorage.loadIntoManagers(java.nio.file.Path.of("data.xml"));
                AlertUtil.showInfo("Данные загружены из data.xml. Нажмите Refresh для обновления таблицы.");
            } catch (Exception e) {
                AlertUtil.showError("Ошибка загрузки: " + e.getMessage());
            }
        });

        loadFromItem.setOnAction(event -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Загрузить XML");
            fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("XML files", "*.xml")
            );

            java.io.File file = fileChooser.showOpenDialog(stage);
            if (file == null) {
                return;
            }

            try {
                fileStorage.loadIntoManagers(file.toPath());
                AlertUtil.showInfo("Данные загружены из файла: " + file.getAbsolutePath() + ". Нажмите Refresh для обновления таблицы.");
            } catch (Exception e) {
                AlertUtil.showError("Ошибка загрузки: " + e.getMessage());
            }
        });

        ToolBar toolBar = new ToolBar(refreshButton, addButton, editButton,
                                              deleteButton, clearButton, saveMenuButton, loadMenuButton);

        root.setTop(toolBar);
        root.setCenter(tableView);

        refreshTable(tableView, experimentManager);

        Scene scene = new Scene(root, 900, 600);

        stage.setTitle("Experiment Manager");
        stage.setScene(scene);
        stage.show();
    }

    private void refreshTable(TableView<Experiment> tableView, ExperimentManager experimentManager) {
        tableView.setItems(FXCollections.observableArrayList(
                experimentManager.getAll().stream()
                        .sorted(java.util.Comparator.comparingLong(Experiment::getId))
                        .toList()
        ));
        tableView.refresh();
    }

    public static void main(String[] args) {
        launch(args);
    }
}