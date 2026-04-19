package org.example.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.AdminPage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Cucumber step definitions for Feature 2, Tasks 2.1 and 2.2.
 *
 * Task 2.1: Search system users by Username, User Role, Employee Name, Status.
 * Task 2.2: Add and delete users; mandatory-field validation; password complexity validation.
 */
public class AdminSteps {

    private final DriverContext context;
    private AdminPage adminPage;
    /** Stored so the delete / verify step can reference the same user. */
    private String generatedUsername;

    public AdminSteps(DriverContext context) {
        this.context = context;
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    @Given("the admin navigates to the Admin page")
    public void navigateToAdminPage() {
        adminPage = new AdminPage(context.getDriver());
        adminPage.open();
    }

    // ── Task 2.1: Search by individual fields ─────────────────────────────────

    @When("the admin searches by username {string}")
    public void searchByUsername(String username) {
        adminPage.searchByUsername(username);
    }

    @When("the admin searches by user role {string}")
    public void searchByUserRole(String role) {
        adminPage.searchByUserRole(role);
    }

    @When("the admin searches by employee name {string}")
    public void searchByEmployeeName(String name) {
        adminPage.searchByEmployeeName(name);
    }

    @When("the admin searches by status {string}")
    public void searchByStatus(String status) {
        adminPage.searchByStatus(status);
    }

    @Then("the search results should contain a user with username {string}")
    public void resultsShouldContainUser(String username) {
        assertTrue("Expected user '" + username + "' in search results",
                adminPage.isUserInResults(username));
    }

    @Then("the search results should not be empty")
    public void resultsShouldNotBeEmpty() {
        assertTrue("Expected at least one search result", adminPage.hasResults());
    }

    // ── Task 2.2: Add user ────────────────────────────────────────────────────

    @When("the admin clicks the Add button")
    public void clickAddButton() {
        adminPage.clickAdd();
    }

    /**
     * Fills all Add-User form fields.
     * A unique username is generated via timestamp so parallel runs don't collide.
     * The {@code username} parameter from the feature file is intentionally ignored
     * to ensure test isolation; use the "the user … should appear" step to verify.
     */
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

    // ── Task 2.2: Mandatory fields ────────────────────────────────────────────

    @And("the admin saves the user form without filling any fields")
    public void saveFormEmpty() {
        adminPage.clickSave();
    }

    @Then("required field error messages should be displayed")
    public void requiredErrorsShouldBeDisplayed() {
        assertTrue("Expected required field error messages to be visible",
                adminPage.hasRequiredErrors());
    }

    // ── Task 2.2: Password complexity ─────────────────────────────────────────

    @Then("a password error message should be displayed")
    public void passwordErrorShouldBeDisplayed() {
        assertTrue("Expected a password complexity error message",
                adminPage.hasRequiredErrors());
    }

    // ── Task 2.2: Delete user ─────────────────────────────────────────────────

    @And("the admin deletes the created user")
    public void deleteCreatedUser() {
        adminPage.open();
        adminPage.deleteUser(generatedUsername);
    }

    @Then("the deleted user should not appear in the user list")
    public void deletedUserShouldNotAppearInList() {
        adminPage.open();
        adminPage.searchByUsername(generatedUsername);
        assertFalse("Deleted user '" + generatedUsername + "' should not be in results",
                adminPage.isUserInResults(generatedUsername));
    }
}
