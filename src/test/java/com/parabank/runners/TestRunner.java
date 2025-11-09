package com.parabank.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        // produce a JSON report both in target/ and at project root so IDE feature-runner can find it
        plugin = {"pretty", "json:target/cucumber.json", "json:cucumber.json", "html:target/cucumber-reports.html", "com.parabank.hooks.StepLogger"},
        features = "src/test/resources/features",
        glue = {"com.parabank.stepdefinitions"},
        monochrome = true
)
public class TestRunner {
}