package org.example.systempom.steps;

import org.openqa.selenium.WebDriver;

/**
 * Shared WebDriver holder injected by Cucumber-PicoContainer.
 * A single instance is created per scenario and passed to every step class
 * that declares it as a constructor parameter, guaranteeing all steps in
 * one scenario share the same browser session.
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
