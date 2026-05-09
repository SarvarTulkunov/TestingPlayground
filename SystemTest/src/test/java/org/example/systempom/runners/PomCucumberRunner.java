package org.example.systempom.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue     = "org.example.systempom.steps",
        plugin   = {
                "pretty",
                "html:target/cucumber-reports/pom-report.html",
                "json:target/cucumber-reports/pom-report.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true
)
public class PomCucumberRunner extends AbstractTestNGCucumberTests {
}
