package org.example.steps;

import org.openqa.selenium.WebDriver;

/**
 * Shared holder for the WebDriver instance.
 * PicoContainer creates one instance per scenario and injects it into every
 * step-definition class that declares it as a constructor parameter.
 */
public class DriverContext {

    private WebDriver driver;

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }
}
