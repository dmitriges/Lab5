package ru.itmo.server.commands;

import ru.itmo.model.RunResult;
import ru.itmo.network.Request;
import ru.itmo.network.Response;
import ru.itmo.network.dto.RunResultAddData;
import ru.itmo.services.RunResultManager;

public class ResAddCommand implements Command {
    private final RunResultManager runResultManager;

    public ResAddCommand(RunResultManager runResultManager) {
        this.runResultManager = runResultManager;
    }

    @Override
    public Response execute(Request request) {
        if (!(request.getPayload() instanceof RunResultAddData data)) {
            return new Response(false, "Некорректные данные для res_add", null);
        }

        RunResult result = runResultManager.add(
                data.getRunId(),
                data.getParam(),
                data.getValue(),
                data.getUnit(),
                data.getComment()
        );

        return new Response(true, "OK result_id=" + result.getId(), result);
    }
}
