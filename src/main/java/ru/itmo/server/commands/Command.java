package ru.itmo.server.commands;

import ru.itmo.network.Request;
import ru.itmo.network.Response;

public interface Command {
    Response execute(Request request);
}
