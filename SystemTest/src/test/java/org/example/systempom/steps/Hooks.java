package org.example.systempom.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

public class Hooks {

    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    private final DriverContext context;

    public Hooks(DriverContext context) {
        this.context = context;
    }

    @Before
    public void setUp(Scenario scenario) {
        log.info("Starting scenario: {}", scenario.getName());
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        context.setDriver(new ChromeDriver(options));
        log.debug("ChromeDriver initialised");
    }

    @After
    public void tearDown(Scenario scenario) {
        log.info("Finishing scenario: {} — status: {}", scenario.getName(), scenario.getStatus());
        if (scenario.isFailed() && context.getDriver() != null) {
            byte[] screenshot = ((TakesScreenshot) context.getDriver()).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Screenshot on failure", "image/png", new ByteArrayInputStream(screenshot), "png");
            log.warn("Screenshot captured for failed scenario: {}", scenario.getName());
        }
        if (context.getDriver() != null) {
            context.getDriver().quit();
            log.debug("ChromeDriver closed");
        }
    }
}
