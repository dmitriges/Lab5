package ru.itmo;

import ru.itmo.model.MeasurementParam;
import ru.itmo.services.ExperimentManager;
import ru.itmo.services.RunManager;
import ru.itmo.services.RunResultManager;
import ru.itmo.storage.FileStorage;

import java.nio.file.Path;

public class XmlStorageTest {
    public static void main(String[] args) throws Exception {
        ExperimentManager experimentManager = new ExperimentManager();
        RunManager runManager = new RunManager(experimentManager);
        RunResultManager runResultManager = new RunResultManager(runManager);

       // пользователь тест как владелец чтобы проверка для этапа 5 тоже работала
        String owner = "test";

        experimentManager.add("Эксперимент 1", "Описание 1", owner);
        runManager.add(1, "Запуск 1", owner, owner);
        runResultManager.add(1, MeasurementParam.PH, 7.0, "pH", "Норма", owner);

        FileStorage storage = new FileStorage(experimentManager, runManager, runResultManager);
        Path path = Path.of("test-data.xml");

        storage.save(path);
        System.out.println("Файл сохранён");

        experimentManager.remove(1, owner);

        System.out.println("После удаления:");
        System.out.println("experiments = " + experimentManager.getAll().size());

        storage.loadIntoManagers(path);
        System.out.println("Файл загружен в менеджеры");

        System.out.println("После загрузки:");
        System.out.println("experiments = " + experimentManager.getAll().size());
        System.out.println("runs = " + runManager.getAll().size());
        System.out.println("results = " + runResultManager.getAll().size());
    }
}