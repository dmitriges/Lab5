package ru.itmo.cli.commands;

//Для реализации полиморфизма, чтобы у всех команд был тип данных Command
// благодаря этому со всеми командами можно работать одинаково, не вдаваясь в реализацию конкретной команды
public interface Command {
    void execute(String[] args);
}