package ru.itmo;

import ru.itmo.model.Experiment;
import ru.itmo.network.DataMapper;
import ru.itmo.network.Response;

import java.time.Instant;

public class ResponseJsonTest {
    public static void main(String[] args) throws Exception {
        Instant now = Instant.now();

        Experiment experiment = new Experiment(
                1,
                "Тестовый эксперимент",
                "Описание",
                "Egor",
                now,
                now
        );

        Response response = new Response(
                true,
                "Experiment created",
                experiment
        );

        String json = DataMapper.json().writeValueAsString(response);
        System.out.println("JSON:");
        System.out.println(json);

        Response restored = DataMapper.json().readValue(json, Response.class);
        System.out.println("success = " + restored.isSuccess());
        System.out.println("message = " + restored.getMessage());
        System.out.println("data class = " + restored.getData().getClass().getName());
    }
}
