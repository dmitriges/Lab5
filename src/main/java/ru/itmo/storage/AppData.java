/*
package ru.itmo.storage;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import ru.itmo.model.Experiment;
import ru.itmo.model.Run;
import ru.itmo.model.RunResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//контейнер для всех данных
// обертка для сериализации данных в файл и обратно
@JacksonXmlRootElement(localName = "appData")
// теперь читаемость файла не зависит от названия файла, без этого могут возникнуть проблемы с читаемостью файла,
// если изменить название
public class AppData {

    // jaсkson лучше сериализует листы
    private List<Experiment> experiments;
    private List<Run> runs;
    private List<RunResult> results;

    //
    public AppData() {
        this.experiments = new ArrayList<>();
        this.runs = new ArrayList<>();
        this.results = new ArrayList<>();
    }

    public AppData(Map<Long, Experiment> experiments,
                   Map<Long, Run> runs,
                   Map<Long, RunResult> results) {
        // пустой список вместо null
        this.experiments = experiments != null ? new ArrayList<>(experiments.values()) : new ArrayList<>();
        this.runs = runs != null ? new ArrayList<>(runs.values()) : new ArrayList<>();
        this.results = results != null ? new ArrayList<>(results.values()) : new ArrayList<>();
    }

    public List<Experiment> getExperiments() {
        return experiments;
    }

    public void setExperiments(List<Experiment> experiments) {
        this.experiments = experiments != null ? experiments : new ArrayList<>();
    }

    public List<Run> getRuns() {
        return runs;
    }

    public void setRuns(List<Run> runs) {
        this.runs = runs != null ? runs : new ArrayList<>();
    }

    public List<RunResult> getResults() {
        return results;
    }

    public void setResults(List<RunResult> results) {
        this.results = results != null ? results : new ArrayList<>();
    }
}*/
