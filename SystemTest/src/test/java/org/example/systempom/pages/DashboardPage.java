package org.example.systempom.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object for the OrangeHRM dashboard (the landing page after login).
 * Separates dashboard-specific checks from the LoginPage, keeping each
 * page object focused on a single screen.
 */
public class DashboardPage extends BasePage {

    private final By dashboardHeader = By.cssSelector(".oxd-topbar-header-title");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Returns true once the browser has landed on the dashboard URL and
     * the header element is visible.
     */
    public boolean isLoaded() {
        try {
            wait.until(ExpectedConditions.urlContains("/dashboard"));
            wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardHeader));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
