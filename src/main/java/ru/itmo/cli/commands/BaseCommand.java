package ru.itmo.cli.commands;

import ru.itmo.cli.util.InputHelper;
import ru.itmo.cli.util.Formatter;

// создает готовые поля input и formatter для того чтобы в дочерних классах их каждый раз не получать
public abstract class BaseCommand implements Command {
    protected final InputHelper input = InputHelper.getInstance();
    protected final Formatter formatter = Formatter.getInstance();
}