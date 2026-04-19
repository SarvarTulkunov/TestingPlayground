package org.example.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.pages.LoginPage;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.Assert.*;

public class LoginSteps {

    private static final String BASE_URL =
            "https://opensource-demo.orangehrmlive.com/";

    private final DriverHolder holder;
    private LoginPage loginPage;

    // Cucumber-PicoContainer injects the shared DriverHolder
    public LoginSteps(DriverHolder holder) {
        this.holder = holder;
    }

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        holder.setDriver(new ChromeDriver(options));
    }

    @After
    public void tearDown() {
        if (holder.getDriver() != null) {
            holder.getDriver().quit();
        }
    }

    @Given("the user is on the OrangeHRM login page")
    public void theUserIsOnTheLoginPage() {
        holder.getDriver().get(BASE_URL + "web/index.php/auth/login");
        loginPage = new LoginPage(holder.getDriver());
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
        assertTrue("Expected to land on the dashboard page",
                loginPage.isDashboardVisible());
    }

    @Then("an error message {string} should be displayed")
    public void anErrorMessageShouldBeDisplayed(String expectedMessage) {
        assertEquals(expectedMessage, loginPage.getErrorMessage());
    }
}
