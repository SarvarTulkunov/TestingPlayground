package org.example.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.AdminPage;

import static org.testng.AssertJUnit.assertTrue;


public class AdminSteps {

    private static final String ADMIN_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/admin/viewSystemUsers";

    // Shared driver is provided by LoginSteps via the Cucumber context.
    // We use a simple holder so both step classes access the same instance.
    private final DriverHolder holder;
    private AdminPage adminPage;
    private String generatedUsername;

    public AdminSteps(DriverHolder holder) {
        this.holder = holder;
    }

    @Given("the admin navigates to the Admin page")
    public void navigateToAdminPage() {
        holder.getDriver().get(ADMIN_URL);
        adminPage = new AdminPage(holder.getDriver());
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
        // Navigate explicitly: after save the page may still be transitioning
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
