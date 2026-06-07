module ru.itmo {
    // JavaFX модули
    requires javafx.controls;
    requires javafx.fxml;

    // База данных
    requires java.sql;

    requires org.postgresql.jdbc;

    // Jackson
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.xml;
    requires com.fasterxml.jackson.datatype.jsr310;


    // Открываем ВСЕ пакеты (включая вложенные)
    opens ru.itmo.cli;
    opens ru.itmo.cli.commands;
    opens ru.itmo.cli.util;
    opens ru.itmo.config;
    opens ru.itmo.model;
    opens ru.itmo.repository;
    opens ru.itmo.services;
    opens ru.itmo.ui;
    opens ru.itmo.ui.util;

    // Экспортируем ВСЕ пакеты
    exports ru.itmo.cli;
    exports ru.itmo.cli.commands;
    exports ru.itmo.cli.util;
    exports ru.itmo.config;
    exports ru.itmo.model;
    exports ru.itmo.repository;
    exports ru.itmo.services;
    exports ru.itmo.ui;
    exports ru.itmo.ui.util;
}