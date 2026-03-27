package ru.itmo.network;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.itmo.network.dto.ExperimentCreateData;
import ru.itmo.network.dto.RunAddData;
import ru.itmo.network.dto.RunResultAddData;

import java.util.List;

public class Request {
    private final String commandName;
    private final List<String> args;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "@type"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ExperimentCreateData.class, name = "ExperimentCreateData"),
            @JsonSubTypes.Type(value = RunAddData.class, name = "RunAddData"),
            @JsonSubTypes.Type(value = RunResultAddData.class, name = "RunResultAddData")
    })
    private final Object payload;

    @JsonCreator
    public Request(
            @JsonProperty("commandName") String commandName,
            @JsonProperty("args") List<String> args,
            @JsonProperty("payload") Object payload
    ) {
        this.commandName = commandName;
        this.args = args;
        this.payload = payload;
    }

    public String getCommandName() {
        return commandName;
    }

    public List<String> getArgs() {
        return args;
    }

    public Object getPayload() {
        return payload;
    }
}