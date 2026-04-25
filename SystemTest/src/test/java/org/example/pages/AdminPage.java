package org.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class AdminPage {

    private static final String ADMIN_LIST_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/admin/viewSystemUsers";

    private final WebDriver driver;
    private final WebDriverWait wait;
    // Longer wait for autocomplete (triggers an API call)
    private final WebDriverWait autocompleteWait;

    // Search form — scoped inside the filter card, not the add/edit form
    private final By usernameSearchInput = By.xpath(
            "//div[contains(@class,'oxd-table-filter')]//label[normalize-space()='Username']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]//input");
    private final By searchButton = By.xpath(
            "//div[contains(@class,'oxd-table-filter')]//button[@type='submit']");
    private final By addButton = By.cssSelector(
            ".orangehrm-header-container button");

    // Results table
    private final By resultRows = By.xpath(
            "//div[contains(@class,'oxd-table-body')]//div[contains(@class,'oxd-table-row')]");
    private final By noRecordsNotice = By.xpath(
            "//*[contains(text(),'No Records Found')]");

    // Add-user form — label-based XPath
    private final By userRoleDropdown = By.xpath(
            "//label[normalize-space()='User Role']/ancestor::div[contains(@class,'oxd-input-group')]" +
            "//div[contains(@class,'oxd-select-text')]");
    private final By employeeNameInput = By.cssSelector(
            ".oxd-autocomplete-wrapper input");
    private final By statusDropdown = By.xpath(
            "//label[normalize-space()='Status']/ancestor::div[contains(@class,'oxd-input-group')]" +
            "//div[contains(@class,'oxd-select-text')]");
    private final By usernameInput = By.xpath(
            "//label[normalize-space()='Username']/ancestor::div[contains(@class,'oxd-input-group')]//input");
    private final By saveButton = By.cssSelector(
            "button[type='submit']");
    private final By requiredErrors = By.cssSelector(
            ".oxd-input-field-error-message");

    public AdminPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.autocompleteWait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void navigateToList() {
        driver.get(ADMIN_LIST_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameSearchInput));
    }

    public void searchByUsername(String username) {
        WebElement input = wait.until(
                ExpectedConditions.elementToBeClickable(usernameSearchInput));
        input.click();
        input.clear();
        input.sendKeys(username);

        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();

        // Wait for the loading spinner to appear and then disappear
        By spinner = By.cssSelector(".oxd-loading-spinner");
        try {
            autocompleteWait.until(ExpectedConditions.visibilityOfElementLocated(spinner));
        } catch (TimeoutException ignored) { /* spinner may flash too fast to catch */ }
        wait.until(ExpectedConditions.invisibilityOfElementLocated(spinner));

        // Wait until either a result row OR the "No Records Found" message renders
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".oxd-table-card")),
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//span[contains(@class,'oxd-text') and " +
                                "contains(text(),'No Records Found')]"))
        ));
    }

    public boolean isUserInResults(String username) {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(resultRows),
                ExpectedConditions.visibilityOfElementLocated(noRecordsNotice)
        ));
        List<WebElement> rows = driver.findElements(resultRows);
        for (WebElement row : rows) {
            if (row.getText().contains(username)) {
                return true;
            }
        }
        return false;
    }

    public void clickAdd() {
        wait.until(ExpectedConditions.elementToBeClickable(addButton)).click();
        // passwordField only exists on the Add User form, never on the list page
        By passwordField = By.xpath(
                "//label[normalize-space()='Password']/ancestor::div[contains(@class,'oxd-input-group')]//input[@type='password']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
    }

    public void fillUserRole(String role) {
        wait.until(ExpectedConditions.elementToBeClickable(userRoleDropdown)).click();
        By option = By.xpath(
                "//div[@role='option']//span[text()='" + role + "']");
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    public void fillEmployeeName(String name) {
        String searchTerm = name.split("\\s+")[0];

        WebElement input = wait.until(
                ExpectedConditions.elementToBeClickable(employeeNameInput));
        input.click();
        input.clear();
        input.sendKeys(searchTerm);

        // Wait for "Searching...." indicator to disappear before reading results
        By searching = By.xpath(
                "//div[contains(@class,'oxd-autocomplete-dropdown')]" +
                "//span[normalize-space()='Searching....']");
        try {
            autocompleteWait.until(ExpectedConditions.invisibilityOfElementLocated(searching));
        } catch (TimeoutException ignored) { }

        // Target real option rows inside the dropdown (case-insensitive name match)
        By realOption = By.xpath(
                "//div[contains(@class,'oxd-autocomplete-dropdown')]" +
                "//div[@role='option']" +
                "[.//span[contains(translate(.," +
                "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" +
                searchTerm.toLowerCase() + "')]]");

        WebElement option = autocompleteWait.until(
                ExpectedConditions.presenceOfElementLocated(realOption));

        // Scroll into view, then fire a real mouse sequence that Vue's handler needs
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", option);

        new Actions(driver)
                .moveToElement(option)
                .pause(Duration.ofMillis(150))
                .click()
                .perform();

        // Confirm the field value changed from the raw search term to the full name
        wait.until(d -> {
            String v = input.getAttribute("value");
            return v != null && !v.equalsIgnoreCase(searchTerm) && !v.isEmpty();
        });
    }

    public void fillStatus(String status) {
        wait.until(ExpectedConditions.elementToBeClickable(statusDropdown)).click();
        By option = By.xpath(
                "//div[@role='option']//span[text()='" + status + "']");
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    public void fillUsername(String username) {
        WebElement input = wait.until(
                ExpectedConditions.elementToBeClickable(usernameInput));
        input.click();
        input.clear();
        input.sendKeys(username);

        // Nudge Vue in case sendKeys didn't trigger the reactive update
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
                input);

        // Wait for the async "username already exists" check to clear the error state
        wait.until(d -> {
            String cls = input.getAttribute("class");
            String val = input.getAttribute("value");
            return username.equals(val) && cls != null && !cls.contains("oxd-input--error");
        });
    }

    public void fillPassword(String password) {
        By passwordField = By.xpath(
                "//label[normalize-space()='Password']/ancestor::div[contains(@class,'oxd-input-group')]//input[@type='password']");
        By confirmPasswordField = By.xpath(
                "//label[normalize-space()='Confirm Password']/ancestor::div[contains(@class,'oxd-input-group')]//input[@type='password']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField)).sendKeys(password);
        wait.until(ExpectedConditions.visibilityOfElementLocated(confirmPasswordField)).sendKeys(password);
    }

    public void clickSave() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        String formUrl = driver.getCurrentUrl();
        btn.click();

        // Either the form submits (URL changes) or validation errors appear
        By errorMsg = By.cssSelector(".oxd-input-field-error-message");
        wait.until(ExpectedConditions.or(
                ExpectedConditions.not(ExpectedConditions.urlToBe(formUrl)),
                ExpectedConditions.visibilityOfElementLocated(errorMsg)
        ));
    }

    public boolean hasRequiredErrors() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(requiredErrors));
        return !driver.findElements(requiredErrors).isEmpty();
    }
}
