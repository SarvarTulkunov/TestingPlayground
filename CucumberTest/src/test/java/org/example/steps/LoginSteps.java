package org.example.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.DashboardPage;
import org.example.pages.LoginPage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Cucumber step definitions for Feature 1: Login.
 *
 * Task 1.1: Valid credentials → dashboard visible.
 * Task 1.2: Invalid credentials → "Invalid credentials" error message.
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
        assertTrue("Dashboard should be visible after valid login",
                dashboardPage.isLoaded());
    }

    @Then("an error message {string} should be displayed")
    public void anErrorMessageShouldBeDisplayed(String expectedMessage) {
        String actual = loginPage.getErrorMessage();
        assertEquals("Login error message mismatch", expectedMessage, actual);
    }
}
