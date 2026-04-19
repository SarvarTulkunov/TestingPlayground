package org.example.steps;

import org.openqa.selenium.WebDriver;

/**
 * Shared context object injected by Cucumber-PicoContainer into every step
 * class that needs the same WebDriver instance within one scenario.
 */
public class DriverHolder {

    private WebDriver driver;

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }
}
