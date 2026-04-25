package org.example.systempom.tests;

import org.example.systempom.base.BaseTest;
import org.example.systempom.pages.DashboardPage;
import org.example.systempom.pages.LoginPage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;


/**
 * JUnit system tests for the OrangeHRM login screen.
 * Each test creates page objects with the shared driver from BaseTest,
 * calls page-level methods only, and asserts outcomes — no raw Selenium here.
 */
public class LoginTest extends BaseTest {

    @Test
    public void validCredentialsRedirectToDashboard() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.enterUsername("Admin");
        loginPage.enterPassword("admin123");
        loginPage.clickLogin();

        DashboardPage dashboardPage = new DashboardPage(driver);
        assertTrue("Expected dashboard to be loaded after valid login",
                dashboardPage.isLoaded());
    }

    @Test
    public void invalidCredentialsShowErrorMessage() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.enterUsername("Admin");
        loginPage.enterPassword("wrongpassword");
        loginPage.clickLogin();

        assertEquals("Invalid credentials", loginPage.getErrorMessage());
    }
}
