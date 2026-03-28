package ru.itmo.network;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.itmo.model.Experiment;
import ru.itmo.model.Run;
import ru.itmo.model.RunResult;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "response")
// делает XML нормальным
//избавляет от кривых корневых тегов
public class Response {
    private final boolean success;
    private final String message;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "@type"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Experiment.class, name = "Experiment"),
            @JsonSubTypes.Type(value = Run.class, name = "Run"),
            @JsonSubTypes.Type(value = RunResult.class, name = "RunResult")
    })
    private final Object data;

    @JsonCreator
    public Response(
            @JsonProperty("success") boolean success,
            @JsonProperty("message") String message,
            @JsonProperty("data") Object data
    ) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
