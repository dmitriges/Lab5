package ru.itmo.cli;

import ru.itmo.cli.commands.*;
import ru.itmo.config.DatabaseInitializer;
import ru.itmo.repository.ExperimentRepository;
import ru.itmo.repository.RunRepository;
import ru.itmo.repository.RunResultRepository;
import ru.itmo.repository.UserRepository;
import ru.itmo.services.*;

import java.util.Scanner;



public class Cli {
    private final Scanner scanner;
    private final CommandRegistry registry;
    private boolean running = true;
    // Флаг, управляющий работой главного цикла. Пока true программа принимает команды.


    // Данные пользователя (null, пока не выполнен вход)
    private String currentUser = null;

   /* // для этапа 5
    private final UserStorage userStorage;
    private String currentUser = null;   // null означает "не авторизован"*/

// ДЛЯ ЭТАПА 6 ЗАМЕНЯЕТСЯ НА
    // Менеджер пользователей (работает с БД через UserRepository)
    private final UserManager userManager;

    public Cli() {

        // для этапа 6 создаем поля классов которые сделали
        DatabaseInitializer.initialize();

        ExperimentRepository experimentRepo = new ExperimentRepository();
        RunRepository runRepo = new RunRepository();
        RunResultRepository resultRepo = new RunResultRepository();
        UserRepository userRepo = new UserRepository();


        // --- Создаём менеджеры, внедряя в них репозитории ---
        ExperimentManager experimentManager = new ExperimentManager(experimentRepo);
        RunManager runManager = new RunManager(runRepo, experimentManager);
        RunResultManager runResultManager = new RunResultManager(resultRepo, runManager);
        SummaryManager summaryManager = new SummaryManager(experimentManager, runManager, runResultManager);

        /*//для этапа 5
        // Хранилище пользователей (файл users.json в текущей папке)
        this.userStorage = new UserStorage("users.json");*/

// ДЛЯ ЭТАПА 6 ЗАМЕНЯЕТСЯ НА
        // Менеджер пользователей
        this.userManager = new UserManager(userRepo);
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
        registry.register("exp_create", new ExpCreateCommand(experimentManager, this));
        // передаем this в команды требующие авторизации  и/или Cli
        registry.register("exp_list", new ExpListCommand(experimentManager));
        registry.register("exp_show", new ExpShowCommand(experimentManager, runManager));
        registry.register("exp_update", new ExpUpdateCommand(experimentManager, this));
        registry.register("run_add", new RunAddCommand(runManager, experimentManager, this));
        registry.register("run_list", new RunListCommand(runManager));
        registry.register("run_show", new RunShowCommand(runManager, runResultManager));
        registry.register("res_add", new ResAddCommand(runResultManager, runManager, this));
        registry.register("res_list", new ResListCommand(runResultManager));
        registry.register("exp_summary", new ExpSummaryCommand(summaryManager));
/*        registry.register("save", new SaveCommand(fileStorage, this));
        registry.register("load", new LoadCommand(fileStorage));*/ //ОТКЛЮЧАЮТСЯ ДЛЯ 6 ЭТАПА
        registry.register("logout", new LogoutCommand(this));
        registry.register("clear", new ClearCommand(experimentManager, runManager, runResultManager, this));

        scanner = new Scanner(System.in);
        //Создание Scanner для последующего чтения ввода.

        // Новые команды для этапа 5 + геттеры сеттеры UPD для 6 этапа теперь через юзер менеджера для удобства
        registry.register("register", new RegisterCommand(userManager));
        registry.register("login", new LoginCommand(this, userManager));
    }
    public String getCurrentUser() {
        return currentUser;
    }
    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
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
