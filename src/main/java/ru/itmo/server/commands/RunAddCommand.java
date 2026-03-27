package ru.itmo.server.commands;

import ru.itmo.model.Run;
import ru.itmo.network.Request;
import ru.itmo.network.Response;
import ru.itmo.network.dto.RunAddData;
import ru.itmo.services.RunManager;

public class RunAddCommand implements Command {
    private final RunManager runManager;

    public RunAddCommand(RunManager runManager) {
        this.runManager = runManager;
    }

    @Override
    public Response execute(Request request) {
        if (!(request.getPayload() instanceof RunAddData data)) {
            return new Response(false, "Некорректные данные для run_add", null);
        }

        Run run = runManager.add(
                data.getExperimentId(),
                data.getRunName(),
                data.getOperatorName()
        );

        return new Response(true, "run_id=" + run.getId(), run);
    }
}
