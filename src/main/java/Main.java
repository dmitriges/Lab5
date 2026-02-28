import ru.itmo.model.Experiment;
import ru.itmo.model.MeasurementParam;
import ru.itmo.model.Run;
import ru.itmo.model.RunResult;
import ru.itmo.services.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class Main {

    public static void main(String[] args) {
        ExperimentManager experimentManager = new ExperimentManager();
        RunManager runManager = new RunManager(experimentManager);
        RunResultManager runResultManager = new RunResultManager(runManager);
        SummaryManager summaryManager = new SummaryManager(experimentManager, runManager, runResultManager);

        Experiment experiment = new Experiment(0L, Instant.now());
        long experimentId = experimentManager.getExperimentId();



/*
        long expId = runAction("exp_create", () ->
                experimentManager.addExperiment("Nitrate removal test", "batch adsorption series", "SYSTEM").getId()
        );


        runAction("exp_list", () -> {
            System.out.println("ID\tName");
            experimentManager.getAll().forEach(e -> System.out.println(e.getId() + "\t" + e.getName()));
        });

        runAction("exp_show " + expId, () -> {
            var exp = experimentManager.getById(expId);
            int runsCount = runManager.listByExperiment(expId).size();
            System.out.println("Experiment #" + exp.getId());
            System.out.println("name: " + exp.getName());
            System.out.println("runs: " + runsCount);
        });

        runAction("exp_update " + expId + " name=...", () -> {
            experimentManager.update(expId, "Nitrate removal test (v2)", null);
            System.out.println("OK");
        });

        long runId1 = runAction("run_add " + expId, () ->
                runManager.add(expId, "Run-2026-02-03-A", "yarus").getId()
        );
        long runId2 = runAction("run_add " + expId, () ->
                runManager.add(expId, "Run-2026-02-02-B", "yarus").getId()
        );

        runAction("run_list " + expId, () -> {
            List<Run> runs = runManager.listByExperiment(expId);
            System.out.println("ID\tRun name\t\tOperator\tTime");
            for (Run r : runs) {
                System.out.printf("%d\t%s\t%s\t%s%n",
                        r.getId(), r.getName(), r.getOperatorName(), r.getCreatedAt());
            }
        });

        long resId1 = runAction("res_add " + runId1, () ->
                runResultManager.add(runId1, MeasurementParam.NITRATE, 12.4, "mg/L", "after 60 min").getId()
        );
        long resId2 = runAction("res_add " + runId2, () ->
                runResultManager.add(runId2, MeasurementParam.NITRATE, 8.1, "mg/L", "").getId()
        );
        long resId3 = runAction("res_add " + runId2, () ->
                runResultManager.add(runId2, MeasurementParam.PH, 7.0, "pH", "").getId()
        );

        runAction("res_list " + runId2, () -> {
            List<RunResult> results = runResultManager.listByRun(runId2);
            System.out.println("ID\tParam\tValue\tUnit\tComment");
            for (RunResult rr : results) {
                System.out.printf("%d\t%s\t%.3f\t%s\t%s%n",
                        rr.getId(), rr.getParam(), rr.getValue(), rr.getUnit(),
                        rrComment(rr));
            }
        });

        runAction("exp_summary " + expId, () -> {
            Map<MeasurementParam, ParamStats> summary = summaryManager.expSummary(expId);
            if (summary.isEmpty()) {
                System.out.println("(no data)");
                return;
            }
            summary.forEach((param, stats) -> {
                System.out.printf("%s: count=%d min=%.3f max=%.3f avg=%.3f%n",
                        param, stats.count(), stats.min(), stats.max(), stats.avg());
            });
        });

        runAction("exp_create (empty name)", () -> experimentManager.addExperiment("   ", "", "SYSTEM"));
        runAction("run_add (bad experimentId)", () -> runManager.add(9999, "Run-X", "yarus"));
        runAction("res_add (bad unit)", () -> runResultManager.add(runId1, MeasurementParam.NITRATE, 1.0, "   ", ""));
    }

    private static String rrComment(RunResult rr) {
        // if your domain keeps null comments, handle it safely
        try {
            var field = rr.getClass().getDeclaredField("comment");
            field.setAccessible(true);
            Object v = field.get(rr);
            return v == null ? "" : v.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /** Run action that doesn't return a value */
        /*
    private static void runAction(String label, Runnable action) {
        System.out.println("\n> " + label);
        try {
            action.run();
        } catch (IllegalArgumentException | NoSuchElementException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /** Run action that returns a long id (for created entities) */
        /*    private static long runAction(String label, java.util.function.LongSupplier action) {
        System.out.println("\n> " + label);
        try {
            long id = action.getAsLong();
            System.out.println("OK id=" + id);
            return id;
        } catch (IllegalArgumentException | NoSuchElementException e) {
            System.out.println("Ошибка: " + e.getMessage());
            return -1;
        }

    }


         */
    }
}