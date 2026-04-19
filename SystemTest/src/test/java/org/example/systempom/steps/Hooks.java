package org.example.systempom.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Cucumber lifecycle hooks for the systempom test suite.
 * Centralises driver creation and teardown so that step definition classes
 * contain only business-level logic.
 */
public class Hooks {

    private final DriverContext context;

    public Hooks(DriverContext context) {
        this.context = context;
    }

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        context.setDriver(new ChromeDriver(options));
    }

    @After
    public void tearDown() {
        if (context.getDriver() != null) {
            context.getDriver().quit();
        }
    }
}
