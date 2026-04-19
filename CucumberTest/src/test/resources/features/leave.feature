Feature: Leave Management
  # Feature 2 – Task 2.3

  Background:
    Given the user is on the OrangeHRM login page
    When the user enters username "Admin" and password "admin123"
    And the user clicks the login button

  # Verify that the leave can be assigned and found in the leave list
  Scenario: Admin can assign leave to an employee
    Given the admin navigates to the Assign Leave page
    When the admin assigns leave with type "US - Vacation", employee "Ranga", from date "tomorrow", to date "tomorrow", duration "Full Day"
    Then the assigned leave should appear in the leave list

  # Verify that all fields except Comments are mandatory
  Scenario: All mandatory fields are required when assigning leave
    Given the admin navigates to the Assign Leave page
    When the admin submits the assign leave form without filling mandatory fields
    Then required field error messages should be displayed on the leave form

  # Verify cancel: leave is marked "Cancelled" (removed from active list, shown as Cancelled)
  Scenario: Cancelled leave is marked as Cancelled and removed from the active leave list
    Given the admin navigates to the Assign Leave page
    And the admin assigns leave with type "US - Vacation", employee "Ranga", from date "tomorrow", to date "tomorrow", duration "Full Day"
    When the admin cancels the assigned leave from the leave list
    Then the cancelled leave should be marked as "Cancelled" in the leave list
    And the cancelled leave should not appear as active in the leave list
