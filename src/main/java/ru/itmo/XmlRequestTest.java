package ru.itmo;

import ru.itmo.network.DataMapper;
import ru.itmo.network.Request;
import ru.itmo.network.dto.ExperimentCreateData;

import java.util.List;

public class XmlRequestTest {
    public static void main(String[] args) throws Exception {
        Request request = new Request(
                "exp_create",
                List.of(),
                new ExperimentCreateData("Тестовый эксперимент", "Описание", "Egor")
        );

        String xml = DataMapper.xml().writeValueAsString(request);
        System.out.println("XML:");
        System.out.println(xml);

        Request restored = DataMapper.xml().readValue(xml, Request.class);
        System.out.println("commandName = " + restored.getCommandName());
        System.out.println("payload class = " + restored.getPayload().getClass().getName());
    }
}