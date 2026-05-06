/*
package ru.itmo.cli.commands;

import ru.itmo.storage.FileStorage;

import java.nio.file.Path;

public class LoadCommand extends BaseCommand {
    private final FileStorage fileStorage;

    public LoadCommand(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("Ошибка: укажите путь к XML-файлу.");
            return;
        }

        try {
            Path path = Path.of(args[0]);
            fileStorage.loadIntoManagers(path);
            System.out.println("Данные загружены из файла: " + path);
        } catch (Exception e) {
            System.out.println("Ошибка загрузки: " + e.getMessage());
        }
    }
}
*/
