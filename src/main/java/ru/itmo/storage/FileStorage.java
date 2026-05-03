package ru.itmo.storage;

import ru.itmo.services.ExperimentManager;
import ru.itmo.services.RunManager;
import ru.itmo.services.RunResultManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
//логика сохранения загрузки
public class FileStorage {
    private final ExperimentManager experimentManager;
    private final RunManager runManager;
    private final RunResultManager runResultManager;

    public FileStorage(ExperimentManager experimentManager,
                       RunManager runManager,
                       RunResultManager runResultManager) {
        this.experimentManager = experimentManager;
        this.runManager = runManager;
        this.runResultManager = runResultManager;
    }
// path это интерфейс java nio
    //представляет путь к файлу
    public AppData load(Path path) throws IOException {
        AppData data = DataMapper.getXml().readValue(path.toFile(), AppData.class);
        // jackson ожидает на вход File (старый тип появившийся в Java давно)
        // теперь строку xml приводим к типу объекта класса AppData
        // записываем значение в data
        FileValidator.validate(data);
        return data;
    }
    // Старые данные в памяти не меняются - все ок

    public void loadIntoManagers(Path path) throws IOException {
        AppData data = load(path);
// записываем новые данные
        experimentManager.importData(data.getExperiments());
        runManager.importData(data.getRuns());
        runResultManager.importData(data.getResults());
    }

    public void save(Path path) throws IOException {
        AppData data = new AppData(
                experimentManager.exportData(),
                runManager.exportData(),
                runResultManager.exportData()
        );

        DataMapper.getXml().writeValue(path.toFile(), data);
        // jackson ожидает на вход File (старый тип появившийся в Java давно)
        // теперь строку xml приводим к типу объекта класса AppData
    }
}
