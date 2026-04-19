package org.example.systempom.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Cucumber runner for the POM-based system test suite.
 * Points to the same feature files as the original runner but wires up
 * the new org.example.systempom.steps glue package so that all step
 * definitions use the Page Object Model classes.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue     = "org.example.systempom.steps",
        plugin   = {
                "pretty",
                "html:target/cucumber-reports/pom-report.html",
                "json:target/cucumber-reports/pom-report.json"
        },
        monochrome = true
)
public class PomCucumberRunner {
}
