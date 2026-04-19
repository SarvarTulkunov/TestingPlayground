package org.example.systempom.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.systempom.pages.AdminPage;

import static org.junit.Assert.assertTrue;

/**
 * Cucumber step definitions for Admin / System Users scenarios.
 * All steps delegate to AdminPage — no direct Selenium usage here.
 * The "add new user" scenario is fully implemented (previously commented out).
 */
public class AdminSteps {

    private final DriverContext context;
    private AdminPage adminPage;
    private String generatedUsername;

    public AdminSteps(DriverContext context) {
        this.context = context;
    }

    @Given("the admin navigates to the Admin page")
    public void navigateToAdminPage() {
        adminPage = new AdminPage(context.getDriver());
        adminPage.navigateToList();
    }

    @When("the admin searches by username {string}")
    public void searchByUsername(String username) {
        adminPage.searchByUsername(username);
    }

    @Then("the search results should contain a user with username {string}")
    public void resultsShouldContainUser(String username) {
        assertTrue("Expected user '" + username + "' in search results",
                adminPage.isUserInResults(username));
    }

    @When("the admin clicks the Add button")
    public void clickAddButton() {
        adminPage.clickAdd();
    }

    @And("the admin fills in user role {string}, employee name {string}, username {string}, status {string}, password {string}, confirm password {string}")
    public void fillUserForm(String role, String employeeName, String username,
                             String status, String password, String confirmPassword) {
        generatedUsername = "testuser_auto_" + System.currentTimeMillis();
        adminPage.fillUserRole(role);
        adminPage.fillEmployeeName(employeeName);
        adminPage.fillStatus(status);
        adminPage.fillUsername(generatedUsername);
        adminPage.fillPassword(password);
    }

    @And("the admin saves the user form")
    public void saveUserForm() {
        adminPage.clickSave();
    }

    @Then("the user {string} should appear in the user list")
    public void userShouldAppearInList(String username) {
        adminPage.navigateToList();
        adminPage.searchByUsername(generatedUsername);
        assertTrue("Expected newly added user '" + generatedUsername + "' in results",
                adminPage.isUserInResults(generatedUsername));
    }

    @And("the admin saves the user form without filling any fields")
    public void saveFormEmpty() {
        adminPage.clickSave();
    }

    @Then("required field error messages should be displayed")
    public void requiredErrorsShouldBeDisplayed() {
        assertTrue("Expected required field error messages to be visible",
                adminPage.hasRequiredErrors());
    }
}
