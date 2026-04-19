package org.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class LeavePage extends BasePage {

    private static final String ASSIGN_LEAVE_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/leave/assignLeave";
    private static final String LEAVE_LIST_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/leave/viewLeaveList";

    private final WebDriverWait autocompleteWait;

    // ── Assign Leave form ─────────────────────────────────────────────────────

    // Employee Name autocomplete — confirmed from inspected HTML:
    // label "Employee Name" → oxd-input-group → oxd-autocomplete-wrapper → input[placeholder="Type for hints..."]
    private final By employeeNameInput = By.xpath(
            "//label[normalize-space()='Employee Name']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]//input");

    // Leave Type dropdown — rendered AFTER employee is selected (async load).
    // label "Leave Type" → oxd-input-group → oxd-select-text
    private final By leaveTypeDropdown = By.xpath(
            "//label[normalize-space()='Leave Type']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]" +
            "//div[contains(@class,'oxd-select-text')]");

    // Date inputs — use positional fallback if label text differs between versions
    private final By fromDateInput = By.xpath(
            "//label[normalize-space()='From Date']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]//input");

    private final By toDateInput = By.xpath(
            "//label[normalize-space()='To Date']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]//input");

    // Duration dropdown — appears after dates are selected (for single-day leaves)
    private final By durationDropdown = By.xpath(
            "//label[normalize-space()='Duration']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]" +
            "//div[contains(@class,'oxd-select-text')]");

    private final By assignButton   = By.cssSelector("button[type='submit']");
    private final By requiredErrors = By.cssSelector(".oxd-input-field-error-message");

    // ── Leave List ────────────────────────────────────────────────────────────

    private final By leaveListRows = By.xpath(
            "//div[contains(@class,'oxd-table-body')]" +
            "//div[contains(@class,'oxd-table-card')]");

    public LeavePage(WebDriver driver) {
        super(driver);
        this.autocompleteWait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    public void openAssignLeave() {
        driver.get(ASSIGN_LEAVE_URL);
        // Wait for the Employee Name input — it is always present on load.
        // Do NOT wait for leaveTypeDropdown here: it may be empty until an
        // employee is chosen (populated asynchronously by the backend).
        wait.until(ExpectedConditions.visibilityOfElementLocated(employeeNameInput));
    }

    public void openLeaveList() {
        driver.get(LEAVE_LIST_URL);
        // Wait for either data rows or an empty table — same pattern as AdminPage
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(leaveListRows));
        } catch (TimeoutException ignored) {
            // 0 rows is a valid state (e.g. date filter returned nothing)
        }
    }

    // ── Assign Leave form — fill in ORDER: Employee → Leave Type → Dates ──────

    /**
     * Types the first word of {@code name} into the Employee Name autocomplete
     * and clicks the first matching suggestion.
     * Must be called BEFORE {@link #selectLeaveType} because the Leave Type
     * dropdown is populated asynchronously after an employee is chosen.
     */
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

        // Confirm the full employee name has been accepted into the input
        wait.until(d -> {
            String v = input.getAttribute("value");
            return v != null && !v.equalsIgnoreCase(searchTerm) && !v.isEmpty();
        });
    }

    /**
     * Opens the Leave Type dropdown and selects the given option.
     * Call this AFTER {@link #fillEmployeeName}: the options are loaded
     * from the server once an employee is selected.
     */
    public void selectLeaveType(String leaveType) {
        // Click to open the dropdown
        wait.until(ExpectedConditions.elementToBeClickable(leaveTypeDropdown)).click();
        // Wait for the specific option — this naturally handles the async load
        By option = By.xpath("//div[@role='option']//span[text()='" + leaveType + "']");
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    /**
     * Sets a date field. Dates must be supplied in yyyy-MM-dd format.
     *
     * Interaction order matters for OrangeHRM's Vue datepicker:
     *  1. Click to open / focus
     *  2. Ctrl+A to select existing text
     *  3. Type new date (replaces selection)
     *  4. Dispatch Vue 'input' event BEFORE moving focus (while element still active)
     *  5. Tab to commit value and close the calendar popup
     */
    public void setFromDate(String date) {
        setDate(fromDateInput, date);
    }

    public void setToDate(String date) {
        setDate(toDateInput, date);
    }

    /**
     * Selects the Duration option (e.g. "Full Day"). The Duration dropdown only
     * appears on the form after both From Date and To Date are the same day.
     */
    public void selectDuration(String duration) {
        wait.until(ExpectedConditions.elementToBeClickable(durationDropdown)).click();
        By option = By.xpath("//div[@role='option']//span[text()='" + duration + "']");
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    public void clickAssign() {
        String formUrl = driver.getCurrentUrl();
        wait.until(ExpectedConditions.elementToBeClickable(assignButton)).click();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.not(ExpectedConditions.urlToBe(formUrl)),
                ExpectedConditions.visibilityOfElementLocated(requiredErrors)
        ));
    }

    public boolean hasRequiredErrors() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(requiredErrors));
        return !driver.findElements(requiredErrors).isEmpty();
    }

    // ── Leave List helpers ────────────────────────────────────────────────────

    public boolean isLeaveInList(String employeeName) {
        // openLeaveList() already waited; just read current rows
        List<WebElement> rows = driver.findElements(leaveListRows);
        for (WebElement row : rows) {
            if (row.getText().contains(employeeName)) return true;
        }
        return false;
    }

    /**
     * Finds the leave row for the given employee and clicks the Cancel action.
     * Handles confirmation dialogs that may appear after clicking Cancel.
     */
    public void cancelLeaveForEmployee(String employeeName) {
        By cancelBtn = By.xpath(
                "//div[contains(@class,'oxd-table-card')]" +
                "[.//*[contains(normalize-space(.),'" + employeeName + "')]]" +
                "//button[.//i[contains(@class,'bi-x')] or " +
                "@title='Cancel' or " +
                ".//span[normalize-space()='Cancel']]");

        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(cancelBtn));
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
            btn.click();
        } catch (TimeoutException e) {
            By anyCancel = By.xpath(
                    "//div[contains(@class,'oxd-table-card')]" +
                    "//button[.//i[contains(@class,'bi-x')] or @title='Cancel']");
            wait.until(ExpectedConditions.elementToBeClickable(anyCancel)).click();
        }

        By confirmBtn = By.xpath(
                "//button[normalize-space()='Yes, Cancel' or " +
                ".//span[normalize-space()='Yes, Cancel'] or " +
                "normalize-space()='Yes']");
        try {
            wait.until(ExpectedConditions.elementToBeClickable(confirmBtn)).click();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(confirmBtn));
        } catch (TimeoutException ignored) {}

        // Wait for table to reflect the updated state
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(leaveListRows));
        } catch (TimeoutException ignored) {}
    }

    public boolean isLeaveMarkedCancelled(String employeeName) {
        By cancelledRow = By.xpath(
                "//div[contains(@class,'oxd-table-card')]" +
                "[.//*[contains(normalize-space(.),'" + employeeName + "')]]" +
                "[.//*[normalize-space()='Cancelled']]");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(cancelledRow));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isLeaveRemovedFromActiveList(String employeeName) {
        By activeRow = By.xpath(
                "//div[contains(@class,'oxd-table-card')]" +
                "[.//*[contains(normalize-space(.),'" + employeeName + "')]]" +
                "[.//*[normalize-space()='Approved' or " +
                "normalize-space()='Pending' or " +
                "normalize-space()='Scheduled']]");
        return driver.findElements(activeRow).isEmpty();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void setDate(By locator, String date) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(locator));
        input.click();
        // Select all existing text, then type the new date
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(date);
        // Dispatch Vue's reactive event BEFORE pressing Tab (input must still have focus)
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));", input);
        // Tab commits the value and closes the calendar popup
        input.sendKeys(Keys.TAB);
    }
}
