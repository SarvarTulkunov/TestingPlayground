package org.example.systempom.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for the OrangeHRM System Users (Admin) screen.
 * Covers search, add, and validation interactions.
 * Extends BasePage for shared driver and default wait; adds a longer
 * autocomplete wait for the employee-name field that triggers an API call.
 */
public class AdminPage extends BasePage {

    private static final String URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/admin/viewSystemUsers";

    // Autocomplete responses require more time than a normal DOM update
    private final WebDriverWait autocompleteWait;

    // ── Search form (scoped to the filter card) ──────────────────────────────
    private final By usernameSearchInput = By.xpath(
            "//div[contains(@class,'oxd-table-filter')]" +
            "//label[normalize-space()='Username']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]//input");
    private final By searchButton = By.xpath(
            "//div[contains(@class,'oxd-table-filter')]//button[@type='submit']");

    // ── Results table ─────────────────────────────────────────────────────────
    private final By resultRows = By.xpath(
            "//div[contains(@class,'oxd-table-body')]" +
            "//div[contains(@class,'oxd-table-row')]");
    private final By noRecordsNotice = By.xpath(
            "//*[contains(text(),'No Records Found')]");

    // ── Toolbar ───────────────────────────────────────────────────────────────
    private final By addButton = By.cssSelector(
            ".orangehrm-header-container button");

    // ── Add-user form (label-anchored XPath) ─────────────────────────────────
    private final By userRoleDropdown = By.xpath(
            "//label[normalize-space()='User Role']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]" +
            "//div[contains(@class,'oxd-select-text')]");
    private final By employeeNameInput = By.cssSelector(
            ".oxd-autocomplete-wrapper input");
    private final By statusDropdown = By.xpath(
            "//label[normalize-space()='Status']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]" +
            "//div[contains(@class,'oxd-select-text')]");
    private final By usernameInput = By.xpath(
            "//label[normalize-space()='Username']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]//input");
    private final By passwordField = By.xpath(
            "//label[normalize-space()='Password']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]" +
            "//input[@type='password']");
    private final By confirmPasswordField = By.xpath(
            "//label[normalize-space()='Confirm Password']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]" +
            "//input[@type='password']");
    private final By saveButton = By.cssSelector("button[type='submit']");
    private final By requiredErrors = By.cssSelector(".oxd-input-field-error-message");

    public AdminPage(WebDriver driver) {
        super(driver);
        this.autocompleteWait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    /** Navigates to the System Users list and waits for the search form. */
    public void open() {
        driver.get(URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameSearchInput));
    }

    /** @deprecated Use {@link #open()} instead. Kept for Cucumber step compatibility. */
    public void navigateToList() {
        open();
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
    }

    public void fillUserRole(String role) {
        wait.until(ExpectedConditions.elementToBeClickable(userRoleDropdown)).click();
        By option = By.xpath("//div[@role='option']//span[text()='" + role + "']");
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    /**
     * Types the first word of {@code name} into the autocomplete field,
     * waits for the suggestion list to appear, then clicks the first option.
     * Clicking the option is more reliable than keyboard navigation because
     * the Vue combobox may not process ARROW_DOWN/ENTER consistently.
     * An extra-long wait is used because the field triggers an API call.
     */
    public void fillEmployeeName(String name) {
        String searchTerm = name.split("\\s+")[0];

        // Make sure this locator is specific to the Employee Name field, e.g.:
        // By employeeNameInput = By.cssSelector(
        //     ".oxd-autocomplete-text-input input[placeholder='Type for hints...']");
        WebElement input = wait.until(
                ExpectedConditions.elementToBeClickable(employeeNameInput));
        input.click();
        input.clear();
        input.sendKeys(searchTerm);

        // Wait for "Searching...." to disappear (it renders inside an option slot)
        By searching = By.xpath(
                "//div[contains(@class,'oxd-autocomplete-dropdown')]" +
                        "//span[normalize-space()='Searching....']");
        try {
            autocompleteWait.until(ExpectedConditions.invisibilityOfElementLocated(searching));
        } catch (TimeoutException ignored) { }

        // Real options are <div role="option"> inside .oxd-autocomplete-dropdown
        By realOption = By.xpath(
                "//div[contains(@class,'oxd-autocomplete-dropdown')]" +
                        "//div[@role='option']" +
                        "[.//span[contains(translate(., " +
                        "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '" +
                        searchTerm.toLowerCase() + "')]]");

        WebElement option = autocompleteWait.until(
                ExpectedConditions.presenceOfElementLocated(realOption));

        // Scroll into view, then use Actions — drives a real mouse,
        // firing mousedown/mouseup/click in order, which Vue's handler needs.
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", option);

        new Actions(driver)
                .moveToElement(option)
                .pause(Duration.ofMillis(150))
                .click()
                .perform();

        // Sanity-check: the input's value should now be the full employee name.
        // If it's still the search term, the click missed.
        wait.until(d -> {
            String v = input.getAttribute("value");
            return v != null && !v.equalsIgnoreCase(searchTerm) && !v.isEmpty();
        });
    }

    public void fillStatus(String status) {
        wait.until(ExpectedConditions.elementToBeClickable(statusDropdown)).click();
        By option = By.xpath("//div[@role='option']//span[text()='" + status + "']");
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
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField))
                .sendKeys(password);
        wait.until(ExpectedConditions.visibilityOfElementLocated(confirmPasswordField))
                .sendKeys(password);
    }

    public void clickSave() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        String formUrl = driver.getCurrentUrl();
        btn.click();

        // Either the form submits and we navigate away, OR validation errors render.
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
