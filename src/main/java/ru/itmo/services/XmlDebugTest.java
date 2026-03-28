package ru.itmo.services;

import ru.itmo.network.DataMapper;
import ru.itmo.network.Request;
import ru.itmo.network.dto.ExperimentCreateData;

import java.util.List;

public class XmlDebugTest {
    public static void main(String[] args) throws Exception {

        Request req = new Request(
                "exp_create",
                List.of(),
                new ExperimentCreateData("test", "desc", "user")
        );

        String xml = DataMapper.xml().writeValueAsString(req);

        System.out.println("=== XML ===");
        System.out.println(xml);

        Request back = DataMapper.xml().readValue(xml, Request.class);

        System.out.println("=== BACK ===");
        System.out.println(back.getCommandName());
        System.out.println(back.getPayload().getClass());
    }
}