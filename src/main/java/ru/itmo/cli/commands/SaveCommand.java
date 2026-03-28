package ru.itmo.cli.commands;

import ru.itmo.storage.FileStorage;

import java.nio.file.Path;

public class SaveCommand extends BaseCommand {
    private final FileStorage fileStorage;

    public SaveCommand(FileStorage fileStorage) {
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
            fileStorage.save(path);
            System.out.println("Данные сохранены в файл: " + path);
        } catch (Exception e) {
            System.out.println("Ошибка сохранения: " + e.getMessage());
        }
    }
}
