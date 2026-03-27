package ru.itmo.network.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.itmo.model.MeasurementParam;

import java.io.Serializable;

public class RunResultAddData implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long runId;
    private final MeasurementParam param;
    private final double value;
    private final String unit;
    private final String comment;

    @JsonCreator
    public RunResultAddData(
            @JsonProperty("runId") long runId,
            @JsonProperty("param") MeasurementParam param,
            @JsonProperty("value") double value,
            @JsonProperty("unit") String unit,
            @JsonProperty("comment") String comment
    ) {
        this.runId = runId;
        this.param = param;
        this.value = value;
        this.unit = unit;
        this.comment = comment;
    }

    public long getRunId() {
        return runId;
    }

    public MeasurementParam getParam() {
        return param;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public String getComment() {
        return comment;
    }
}