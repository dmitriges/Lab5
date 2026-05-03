package ru.itmo.cli;

import ru.itmo.cli.commands.*;
import ru.itmo.services.*;

import java.util.Scanner;
import ru.itmo.storage.FileStorage;


public class Cli {
    private final Scanner scanner;
    private final CommandRegistry registry;
    private boolean running = true;
    // Флаг, управляющий работой главного цикла. Пока true, программа принимает команды.

    public Cli() {
        // Инициализация менеджеров
        ExperimentManager experimentManager = new ExperimentManager();
        RunManager runManager = new RunManager(experimentManager);
        RunResultManager runResultManager = new RunResultManager(runManager);
        SummaryManager summaryManager = new SummaryManager(experimentManager, runManager, runResultManager);
        FileStorage fileStorage = new FileStorage(experimentManager, runManager, runResultManager);

        // Реестр команд
        //Каждая команда получает необходимые ей зависимости
        registry = new CommandRegistry();
        registry.register("help", new HelpCommand(registry));
        registry.register("exit", new ExitCommand(this));
        // Вызывается метод register у объекта registry
        // который связывает строку "exit" с объектом команды.
        // Теперь при вводе пользователем команды exit реестр вернёт этот объект.
        //new ExitCommand(this) — создаётся новый экземпляр класса ExitCommand.
        // В его конструктор передаётся this — ссылка на текущий объект Cli.
        //Класс ExitCommand должен иметь возможность завершить работу программы, то есть остановить цикл в Cli.
        // Для этого ему нужна ссылка на объект Cli, чтобы вызвать его метод stop()
        registry.register("exp_create", new ExpCreateCommand(experimentManager));
        registry.register("exp_list", new ExpListCommand(experimentManager));
        registry.register("exp_show", new ExpShowCommand(experimentManager, runManager));
        registry.register("exp_update", new ExpUpdateCommand(experimentManager));
        registry.register("run_add", new RunAddCommand(runManager, experimentManager));
        registry.register("run_list", new RunListCommand(runManager));
        registry.register("run_show", new RunShowCommand(runManager, runResultManager));
        registry.register("res_add", new ResAddCommand(runResultManager, runManager));
        registry.register("res_list", new ResListCommand(runResultManager));
        registry.register("exp_summary", new ExpSummaryCommand(summaryManager));
        registry.register("save", new SaveCommand(fileStorage));
        registry.register("load", new LoadCommand(fileStorage));

        scanner = new Scanner(System.in);
        //Создание Scanner для последующего чтения ввода.
    }

    public void start() {
        System.out.println("Добро пожаловать в систему управления экспериментами.");
        System.out.println("Введите 'help' для списка команд.");

        while (running) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            //Читает строку, удаляет лишние пробелы, пропускает пустые строки.
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            //Разбивает строку на части по пробелам
            String commandName = parts[0].toLowerCase();
            String[] args = new String[parts.length - 1];
            System.arraycopy(parts, 1, args, 0, args.length);
            // Это встроенный метод Java для копирования элементов из одного массива в другой
            //массив строк, полученный разбиением введённой пользователем команды.
            // Например, если пользователь ввёл "exp_show 2", то parts будет ["exp_show", "2"].
            //1 — начинаем копировать со второго элемента исходного массива

            try {
                Command command = registry.getCommand(commandName);
                if (command == null) {
                    System.out.println("Неизвестная команда. Введите 'help' для справки.");
                } else {
                    command.execute(args);
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: ожидалось число, но получено \"" + e.getMessage() + "\"");
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
        scanner.close();
    }

    public void stop() {
        running = false;
    }

    public static void main(String[] args) {
        new Cli().start();
    }
}