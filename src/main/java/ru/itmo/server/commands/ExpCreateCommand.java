package ru.itmo.server.commands;

import ru.itmo.model.Experiment;
import ru.itmo.network.Request;
import ru.itmo.network.Response;
import ru.itmo.network.dto.ExperimentCreateData;
import ru.itmo.services.ExperimentManager;

public class ExpCreateCommand implements Command {
    private final ExperimentManager experimentManager;

    public ExpCreateCommand(ExperimentManager experimentManager) {
        this.experimentManager = experimentManager;
    }

    @Override
    public Response execute(Request request) {
        if (!(request.getPayload() instanceof ExperimentCreateData data)) {
            return new Response(false, "Некорректные данные для exp_create", null);
        }

        String owner = data.getOwnerUsername();
        if (owner == null || owner.isBlank()) {
            owner = "SYSTEM";
        }

        Experiment exp = experimentManager.add(
                data.getName(),
                data.getDescription(),
                owner
        );

        return new Response(true, "Experiment_id=" + exp.getId(), exp);
    }
}
