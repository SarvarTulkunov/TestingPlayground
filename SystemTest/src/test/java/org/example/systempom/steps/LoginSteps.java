package org.example.systempom.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.systempom.pages.DashboardPage;
import org.example.systempom.pages.LoginPage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Cucumber step definitions for login scenarios.
 * Each method is a thin wrapper: it delegates entirely to LoginPage or
 * DashboardPage and contains no raw Selenium calls.
 */
public class LoginSteps {

    private final DriverContext context;
    private LoginPage loginPage;

    public LoginSteps(DriverContext context) {
        this.context = context;
    }

    @Given("the user is on the OrangeHRM login page")
    public void theUserIsOnTheLoginPage() {
        loginPage = new LoginPage(context.getDriver());
        loginPage.open();
    }

    @When("the user enters username {string} and password {string}")
    public void theUserEntersCredentials(String username, String password) {
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
    }

    @And("the user clicks the login button")
    public void theUserClicksLoginButton() {
        loginPage.clickLogin();
    }

    @Then("the user should be redirected to the dashboard page")
    public void theUserShouldBeRedirectedToDashboard() {
        DashboardPage dashboardPage = new DashboardPage(context.getDriver());
        assertTrue("Expected to land on the dashboard page",
                dashboardPage.isLoaded());
    }

    @Then("an error message {string} should be displayed")
    public void anErrorMessageShouldBeDisplayed(String expectedMessage) {
        assertEquals(expectedMessage, loginPage.getErrorMessage());
    }
}
