package org.example.systempom.tests;

import io.cucumber.java.Before;
import org.example.systempom.base.BaseTest;
import org.example.systempom.pages.AdminPage;
import org.example.systempom.pages.LoginPage;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertTrue;


/**
 * JUnit system tests for the OrangeHRM Admin / System Users screen.
 * A @Before method performs login before each test so that every scenario
 * starts from an authenticated session — mirroring the Cucumber Background.
 */
public class AdminTest extends BaseTest {

    private AdminPage adminPage;

    /** Logs in and opens the Admin page before every test. */
    @Before
    public void login() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.enterUsername("Admin");
        loginPage.enterPassword("admin123");
        loginPage.clickLogin();

        adminPage = new AdminPage(driver);
        adminPage.open();
    }

    @Test
    public void searchByUsernameShowsMatchingUser() {
        adminPage.searchByUsername("Admin");
        assertTrue("Expected 'Admin' to appear in search results",
                adminPage.isUserInResults("Admin"));
    }

    @Test
    public void savingEmptyAddFormShowsRequiredErrors() {
        adminPage.clickAdd();
        adminPage.clickSave();
        assertTrue("Expected required-field error messages to be visible",
                adminPage.hasRequiredErrors());
    }

    @Test
    public void addNewUserAppearsInUserList() {
        String username = "testuser_auto_" + System.currentTimeMillis();

        adminPage.clickAdd();
        adminPage.fillUserRole("ESS");
        adminPage.fillEmployeeName("Ranga");
        adminPage.fillStatus("Enabled");
        adminPage.fillUsername(username);
        adminPage.fillPassword("Test@1234");
        adminPage.clickSave();

        adminPage.open();
        adminPage.searchByUsername(username);
        assertTrue("Expected newly created user '" + username + "' in results",
                adminPage.isUserInResults(username));
    }
}
