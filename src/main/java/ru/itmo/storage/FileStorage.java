package ru.itmo.storage;

import ru.itmo.network.DataMapper;
import ru.itmo.services.ExperimentManager;
import ru.itmo.services.RunManager;
import ru.itmo.services.RunResultManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    public AppData load(Path path) throws IOException {
        String xml = Files.readString(path);
        AppData data = DataMapper.xml().readValue(xml, AppData.class);
        FileValidator.validate(data);
        return data;
    }

    public void loadIntoManagers(Path path) throws IOException {
        AppData data = load(path);

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

        String xml = DataMapper.xml().writeValueAsString(data);
        Files.writeString(path, xml);
    }
}
