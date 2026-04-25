package org.example.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.LeavePage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.testng.AssertJUnit.assertTrue;

/**
 * Cucumber step definitions for Feature 2, Task 2.3: Leave Management.
 *
 * Task 2.3 requirements:
 *  – Assign Leave and Cancel Leave are possible.
 *  – All fields except Comments are mandatory (validated on empty-submit).
 *  – The assigned leave can be found in the leave list.
 *  – The cancelled leave is removed from the active leave list and is marked
 *    "Cancelled" in the leave list.
 */
public class LeaveSteps {

    private final DriverContext context;
    private LeavePage leavePage;

    /** Remembered across steps so cancel/verify can reference the same record. */
    private String lastEmployeeName;

    public LeaveSteps(DriverContext context) {
        this.context = context;
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    @Given("the admin navigates to the Assign Leave page")
    public void navigateToAssignLeavePage() {
        leavePage = new LeavePage(context.getDriver());
        leavePage.openAssignLeave();
    }

    // ── Assign Leave ──────────────────────────────────────────────────────────

    /**
     * Fills the Assign Leave form and submits. Dates in yyyy-MM-dd format.
     * A single @When annotation matches both "When" and "And" keywords in Gherkin.
     */
    @When("the admin assigns leave with type {string}, employee {string}, from date {string}, to date {string}, duration {string}")
    public void assignLeave(String leaveType, String employeeName,
                            String fromDate, String toDate, String duration) {
        lastEmployeeName = employeeName;
        // Employee Name MUST come before Leave Type: the Leave Type dropdown is
        // populated asynchronously by the server only after an employee is chosen.
        leavePage.fillEmployeeName(employeeName);
        leavePage.selectLeaveType(leaveType);
        leavePage.setFromDate(resolveDate(fromDate));
        leavePage.setToDate(resolveDate(toDate));
        leavePage.selectDuration(duration);
        leavePage.clickAssign();
    }

    private String resolveDate(String dateStr) {
        if ("tomorrow".equalsIgnoreCase(dateStr)) {
            return LocalDate.now().plusDays(1)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        return dateStr;
    }

    // ── Mandatory fields validation ───────────────────────────────────────────

    @When("the admin submits the assign leave form without filling mandatory fields")
    public void submitLeaveFormEmpty() {
        leavePage.clickAssign();
    }

    @Then("required field error messages should be displayed on the leave form")
    public void leaveRequiredErrorsShouldBeDisplayed() {
        assertTrue("Expected required field error messages on the Assign Leave form",
                leavePage.hasRequiredErrors());
    }

    // ── Leave List verification ───────────────────────────────────────────────

    @Then("the assigned leave should appear in the leave list")
    public void assignedLeaveShouldAppearInList() {
        leavePage.openLeaveList();
        assertTrue("Expected assigned leave for '" + lastEmployeeName + "' in the leave list",
                leavePage.isLeaveInList(lastEmployeeName));
    }

    // ── Cancel Leave ──────────────────────────────────────────────────────────

    @When("the admin cancels the assigned leave from the leave list")
    public void cancelAssignedLeave() {
        leavePage.openLeaveList();
        leavePage.cancelLeaveForEmployee(lastEmployeeName);
    }

    @Then("the cancelled leave should be marked as {string} in the leave list")
    public void cancelledLeaveShouldBeMarked(String expectedStatus) {
        leavePage.openLeaveList();
        assertTrue(
                "Expected leave for '" + lastEmployeeName + "' to show '" + expectedStatus + "' status",
                leavePage.isLeaveMarkedCancelled(lastEmployeeName));
    }

    @And("the cancelled leave should not appear as active in the leave list")
    public void cancelledLeaveShouldNotBeActive() {
        assertTrue(
                "Expected no active (Approved/Pending/Scheduled) leave for '"
                        + lastEmployeeName + "' after cancellation",
                leavePage.isLeaveRemovedFromActiveList(lastEmployeeName));
    }
}
