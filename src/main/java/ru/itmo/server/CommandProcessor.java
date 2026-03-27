package ru.itmo.server;

import ru.itmo.network.Request;
import ru.itmo.network.Response;
import ru.itmo.server.commands.Command;

public class CommandProcessor {
    private final ServerCommandRegistry registry;

    public CommandProcessor(ServerCommandRegistry registry) {
        this.registry = registry;
    }

    public Response process(Request request) {
        if (request == null) {
            return new Response(false, "Пустой запрос", null);
        }

        Command command = (Command) registry.getCommand(request.getCommandName());
        if (command == null) {
            return new Response(false, "Неизвестная команда: " + request.getCommandName(), null);
        }

        try {
            return command.execute(request);
        } catch (Exception e) {
            return new Response(false, "Ошибка выполнения: " + e.getMessage(), null);
        }
    }
}
