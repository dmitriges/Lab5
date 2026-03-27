package ru.itmo.network.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class RunAddData implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long experimentId;
    private final String runName;
    private final String operatorName;

    @JsonCreator
    public RunAddData(
            @JsonProperty("experimentId") long experimentId,
            @JsonProperty("runName") String runName,
            @JsonProperty("operatorName") String operatorName
    ) {
        this.experimentId = experimentId;
        this.runName = runName;
        this.operatorName = operatorName;
    }

    public long getExperimentId() {
        return experimentId;
    }

    public String getRunName() {
        return runName;
    }

    public String getOperatorName() {
        return operatorName;
    }
}