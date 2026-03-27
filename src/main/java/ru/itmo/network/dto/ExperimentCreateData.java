package ru.itmo.network.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ExperimentCreateData implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String description;
    private final String ownerUsername;

    @JsonCreator
    public ExperimentCreateData(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("ownerUsername") String ownerUsername
    ) {
        this.name = name;
        this.description = description;
        this.ownerUsername = ownerUsername;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }
}
