package org.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for the OrangeHRM System Users (Admin) screen.
 * Covers search (by all four filter fields), add, delete, and validation interactions.
 */
public class AdminPage extends BasePage {

    private static final String URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/admin/viewSystemUsers";

    private final WebDriverWait autocompleteWait;

    // ── Search form – Username ────────────────────────────────────────────────
    private final By usernameSearchInput = By.xpath(
            "//div[contains(@class,'oxd-table-filter')]" +
            "//label[normalize-space()='Username']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]//input");

    // ── Search form – User Role dropdown ─────────────────────────────────────
    private final By userRoleSearchDropdown = By.xpath(
            "//div[contains(@class,'oxd-table-filter')]" +
            "//label[normalize-space()='User Role']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]" +
            "//div[contains(@class,'oxd-select-text')]");

    // ── Search form – Employee Name (autocomplete) ────────────────────────────
    private final By employeeNameSearchInput = By.xpath(
            "//div[contains(@class,'oxd-table-filter')]" +
            "//label[normalize-space()='Employee Name']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]//input");

    // ── Search form – Status dropdown ─────────────────────────────────────────
    private final By statusSearchDropdown = By.xpath(
            "//div[contains(@class,'oxd-table-filter')]" +
            "//label[normalize-space()='Status']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]" +
            "//div[contains(@class,'oxd-select-text')]");

    private final By searchButton = By.xpath(
            "//div[contains(@class,'oxd-table-filter')]//button[@type='submit']");

    // ── Results table ─────────────────────────────────────────────────────────
    private final By resultRows = By.xpath(
            "//div[contains(@class,'oxd-table-body')]" +
            "//div[contains(@class,'oxd-table-row')]");

    // ── Toolbar ───────────────────────────────────────────────────────────────
    private final By addButton = By.cssSelector(
            ".orangehrm-header-container button");

    // ── Add-user form (label-anchored XPath) ──────────────────────────────────
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
    private final By saveButton      = By.cssSelector("button[type='submit']");
    private final By requiredErrors  = By.cssSelector(".oxd-input-field-error-message");

    public AdminPage(WebDriver driver) {
        super(driver);
        this.autocompleteWait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void open() {
        driver.get(URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameSearchInput));
    }

    /** Alias kept so step definitions can call either name. */
    public void navigateToList() {
        open();
    }

    // ── Search by Username ────────────────────────────────────────────────────

    public void searchByUsername(String username) {
        WebElement input = wait.until(
                ExpectedConditions.elementToBeClickable(usernameSearchInput));
        input.click();
        input.clear();
        input.sendKeys(username);
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
        waitForSearchResults();
    }

    // ── Search by User Role ───────────────────────────────────────────────────

    public void searchByUserRole(String role) {
        wait.until(ExpectedConditions.elementToBeClickable(userRoleSearchDropdown)).click();
        By option = By.xpath("//div[@role='option']//span[text()='" + role + "']");
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
        waitForSearchResults();
    }

    // ── Search by Employee Name (autocomplete in filter bar) ──────────────────

    public void searchByEmployeeName(String name) {
        String searchTerm = name.split("\\s+")[0];
        WebElement input = wait.until(
                ExpectedConditions.elementToBeClickable(employeeNameSearchInput));
        input.click();
        input.clear();
        input.sendKeys(searchTerm);

        By searching = By.xpath(
                "//div[contains(@class,'oxd-autocomplete-dropdown')]" +
                "//span[normalize-space()='Searching....']");
        try {
            autocompleteWait.until(ExpectedConditions.invisibilityOfElementLocated(searching));
        } catch (TimeoutException ignored) {}

        By firstOption = By.xpath(
                "//div[contains(@class,'oxd-autocomplete-dropdown')]//div[@role='option']");
        try {
            WebElement option = autocompleteWait.until(
                    ExpectedConditions.presenceOfElementLocated(firstOption));
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({block:'center'});", option);
            new Actions(driver)
                    .moveToElement(option)
                    .pause(Duration.ofMillis(150))
                    .click()
                    .perform();
        } catch (TimeoutException ignored) {}

        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
        waitForSearchResults();
    }

    // ── Search by Status ──────────────────────────────────────────────────────

    public void searchByStatus(String status) {
        wait.until(ExpectedConditions.elementToBeClickable(statusSearchDropdown)).click();
        By option = By.xpath("//div[@role='option']//span[text()='" + status + "']");
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
        waitForSearchResults();
    }

    // ── Results helpers ───────────────────────────────────────────────────────

    public boolean isUserInResults(String username) {
        // waitForSearchResults() has already waited for the table to settle —
        // just read whatever rows are present now.
        List<WebElement> rows = driver.findElements(resultRows);
        for (WebElement row : rows) {
            if (row.getText().contains(username)) return true;
        }
        return false;
    }

    public boolean hasResults() {
        return !driver.findElements(resultRows).isEmpty();
    }

    // ── Add-user form ─────────────────────────────────────────────────────────

    public void clickAdd() {
        wait.until(ExpectedConditions.elementToBeClickable(addButton)).click();
    }

    public void fillUserRole(String role) {
        wait.until(ExpectedConditions.elementToBeClickable(userRoleDropdown)).click();
        By option = By.xpath("//div[@role='option']//span[text()='" + role + "']");
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    public void fillEmployeeName(String name) {
        String searchTerm = name.split("\\s+")[0];
        WebElement input = wait.until(
                ExpectedConditions.elementToBeClickable(employeeNameInput));
        input.click();
        input.clear();
        input.sendKeys(searchTerm);

        By searching = By.xpath(
                "//div[contains(@class,'oxd-autocomplete-dropdown')]" +
                "//span[normalize-space()='Searching....']");
        try {
            autocompleteWait.until(ExpectedConditions.invisibilityOfElementLocated(searching));
        } catch (TimeoutException ignored) {}

        By realOption = By.xpath(
                "//div[contains(@class,'oxd-autocomplete-dropdown')]" +
                "//div[@role='option']" +
                "[.//span[contains(translate(.," +
                "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '" +
                searchTerm.toLowerCase() + "')]]");

        WebElement option = autocompleteWait.until(
                ExpectedConditions.presenceOfElementLocated(realOption));
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", option);
        new Actions(driver)
                .moveToElement(option)
                .pause(Duration.ofMillis(150))
                .click()
                .perform();

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
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
                input);
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

    // ── Delete user ───────────────────────────────────────────────────────────

    /**
     * Searches for the given username in the list, then clicks the trash icon
     * button on that row and confirms the deletion dialog.
     */
    public void deleteUser(String username) {
        searchByUsername(username);

        // Username text lives inside a <p> (oxd-text) not a <div>, so use .//*
        By deleteBtn = By.xpath(
                "//div[contains(@class,'oxd-table-row')]" +
                "[.//*[normalize-space()='" + username + "']]" +
                "//button[.//i[contains(@class,'bi-trash')]]");
        wait.until(ExpectedConditions.elementToBeClickable(deleteBtn)).click();

        // Confirmation dialog: "Yes, Delete" button carries the danger CSS class
        // and contains an <i> icon + text node – match by contains() to be safe.
        By confirmDelete = By.xpath(
                "//button[contains(@class,'oxd-button--label-danger') and " +
                "contains(normalize-space(),'Yes, Delete')]");
        wait.until(ExpectedConditions.elementToBeClickable(confirmDelete)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(confirmDelete));
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void waitForSearchResults() {
        By spinner = By.cssSelector(".oxd-loading-spinner");
        try {
            autocompleteWait.until(ExpectedConditions.visibilityOfElementLocated(spinner));
        } catch (TimeoutException ignored) {}
        // Wait for the spinner to clear — this is the definitive signal that the
        // XHR search request has finished.
        wait.until(ExpectedConditions.invisibilityOfElementLocated(spinner));

        // After the spinner is gone, try to wait for result cards to render.
        // If none appear within 5 s the query returned 0 results; that is not an
        // error — isUserInResults / hasResults will simply find an empty row list.
        // We deliberately do NOT rely on any "No Records Found" text because
        // OrangeHRM can surface that message inside a transient toast notification
        // (which disappears immediately) rather than in the table body.
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector(".oxd-table-card")));
        } catch (TimeoutException ignored) {
            // 0-result search — nothing more to wait for
        }
    }
}
