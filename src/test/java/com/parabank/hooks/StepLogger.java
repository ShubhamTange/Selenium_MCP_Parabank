package com.parabank.hooks;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;

public class StepLogger implements ConcurrentEventListener {
    private final PrintWriter out;

    public StepLogger() {
        PrintWriter w;
        try {
            Path dir = Paths.get("target", "logs");
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            Path file = dir.resolve("execution.log");
            w = new PrintWriter(Files.newBufferedWriter(file, java.nio.charset.StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND), true);
        } catch (IOException e) {
            e.printStackTrace();
            w = new PrintWriter(System.out, true);
        }
        out = w;
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseStarted.class, this::onTestCaseStarted);
        publisher.registerHandlerFor(TestStepStarted.class, this::onTestStepStarted);
        publisher.registerHandlerFor(TestStepFinished.class, this::onTestStepFinished);
        publisher.registerHandlerFor(TestCaseFinished.class, this::onTestCaseFinished);
    }

    private void write(String s) {
        String line = ZonedDateTime.now() + " " + s;
        out.println(line);
        System.out.println(line);
    }

    private void onTestCaseStarted(TestCaseStarted ev) {
        write("SCENARIO START: " + ev.getTestCase().getName());
    }

    private void onTestStepStarted(TestStepStarted ev) {
        TestStep ts = ev.getTestStep();
        if (ts instanceof PickleStepTestStep) {
            PickleStepTestStep pst = (PickleStepTestStep) ts;
            String kw = pst.getStep().getKeyword() != null ? pst.getStep().getKeyword() : "";
            write("  STEP START: " + kw + pst.getStep().getText());
        } else if (ts instanceof HookTestStep) {
            HookTestStep h = (HookTestStep) ts;
            write("  HOOK START: " + h.getHookType());
        } else {
            write("  STEP START: " + ts.getClass().getSimpleName());
        }
    }

    private void onTestStepFinished(TestStepFinished ev) {
        TestStep ts = ev.getTestStep();
        Result res = ev.getResult();
        String status = res.getStatus() != null ? res.getStatus().name() : "UNKNOWN";
        long dur = 0;
        if (res.getDuration() != null) {
            dur = res.getDuration().toMillis();
        }
        if (ts instanceof PickleStepTestStep) {
            PickleStepTestStep pst = (PickleStepTestStep) ts;
            write("  STEP FINISH: " + pst.getStep().getText() + " => " + status + " (" + dur + "ms)");
        } else if (ts instanceof HookTestStep) {
            HookTestStep h = (HookTestStep) ts;
            write("  HOOK FINISH: " + h.getHookType() + " => " + status);
        } else {
            write("  STEP FINISH: " + ts.getClass().getSimpleName() + " => " + status);
        }
        if (res.getError() != null) {
            write("    ERROR: " + res.getError().toString());
            String screenshot = latestScreenshot();
            if (screenshot != null) {
                write("    LATEST SCREENSHOT: " + screenshot);
            }
        }
    }

    private void onTestCaseFinished(TestCaseFinished ev) {
        write("SCENARIO FINISHED: " + ev.getTestCase().getName() + " => " + ev.getResult().getStatus());
        write("--------------------------------------------------");
    }

    private String latestScreenshot() {
        try {
            Path dir = Paths.get("target", "screenshots");
            if (!Files.exists(dir)) return null;
            return Files.list(dir)
                    .filter(p -> !Files.isDirectory(p))
                    .map(Path::toFile)
                    .max((a, b) -> Long.compare(a.lastModified(), b.lastModified()))
                    .map(f -> f.getAbsolutePath())
                    .orElse(null);
        } catch (IOException e) {
            return null;
        }
    }
}
