package ru.itmo.server;

import ru.itmo.network.Request;
import ru.itmo.network.Response;
import ru.itmo.network.dto.ExperimentCreateData;
import ru.itmo.network.dto.RunAddData;
import ru.itmo.network.dto.RunResultAddData;
import ru.itmo.model.MeasurementParam;
import ru.itmo.services.ExperimentManager;
import ru.itmo.services.RunManager;
import ru.itmo.services.RunResultManager;
import ru.itmo.server.commands.ExpCreateCommand;
import ru.itmo.server.commands.RunAddCommand;
import ru.itmo.server.commands.ResAddCommand;

import java.util.List;

public class ServerMain {
    public static void main(String[] args) {
        ExperimentManager experimentManager = new ExperimentManager();
        RunManager runManager = new RunManager(experimentManager);
        RunResultManager runResultManager = new RunResultManager(runManager);

        ServerCommandRegistry registry = new ServerCommandRegistry();
        registry.register("exp_create", new ExpCreateCommand(experimentManager));
        registry.register("run_add", new RunAddCommand(runManager));
        registry.register("res_add", new ResAddCommand(runResultManager));

        CommandProcessor processor = new CommandProcessor(registry);

        Request req1 = new Request(
                "exp_create",
                List.of(),
                new ExperimentCreateData("Тестовый эксперимент", "Описание", "Egor")
        );

        Response resp1 = processor.process(req1);
        System.out.println(resp1.getMessage());

        Request req2 = new Request(
                "run_add",
                List.of(),
                new RunAddData(1, "Первый запуск", "Егор")
        );

        Response resp2 = processor.process(req2);
        System.out.println(resp2.getMessage());

        Request req3 = new Request(
                "res_add",
                List.of(),
                new RunResultAddData(1, MeasurementParam.PH, 7.2, "pH", "Нормальное значение")
        );

        Response resp3 = processor.process(req3);
        System.out.println(resp3.getMessage());
    }
}