Feature: Admin User Management
  # Feature 2 – Tasks 2.1 and 2.2

  Background:
    Given the user is on the OrangeHRM login page
    When the user enters username "Admin" and password "admin123"
    And the user clicks the login button

  # ── Task 2.1: Search by all available filter fields ──────────────────────────

  Scenario: Search a system user by username
    Given the admin navigates to the Admin page
    When the admin searches by username "Admin"
    Then the search results should contain a user with username "Admin"

  Scenario: Search a system user by user role
    Given the admin navigates to the Admin page
    When the admin searches by user role "Admin"
    Then the search results should not be empty

  Scenario: Search a system user by employee name
    Given the admin navigates to the Admin page
    When the admin searches by employee name "Ranga"
    Then the search results should not be empty

  Scenario: Search a system user by status
    Given the admin navigates to the Admin page
    When the admin searches by status "Enabled"
    Then the search results should not be empty

  # ── Task 2.2: Add and delete a user ──────────────────────────────────────────

  Scenario: Add a new system user successfully
    Given the admin navigates to the Admin page
    When the admin clicks the Add button
    And the admin fills in user role "ESS", employee name "Ranga", username "testuser_auto1", status "Enabled", password "Test@1234", confirm password "Test@1234"
    And the admin saves the user form
    Then the user "testuser_auto1" should appear in the user list

  Scenario: A newly added user can be deleted
    Given the admin navigates to the Admin page
    When the admin clicks the Add button
    And the admin fills in user role "ESS", employee name "Ranga", username "testdelete_auto", status "Enabled", password "Test@1234", confirm password "Test@1234"
    And the admin saves the user form
    And the admin deletes the created user
    Then the deleted user should not appear in the user list

  # ── Task 2.2: All fields are mandatory ───────────────────────────────────────

  Scenario: Mandatory fields are enforced when adding a user
    Given the admin navigates to the Admin page
    When the admin clicks the Add button
    And the admin saves the user form without filling any fields
    Then required field error messages should be displayed

  # ── Task 2.2: Password must meet complexity requirements ──────────────────────
  # OrangeHRM enforces password LENGTH (≥ 8 chars) as a hard validation error.
  # Complexity advice ("For a strong password…") is an advisory hint shown near
  # the field – it does NOT block form submission on its own.
  # A valid password example: "Test@1234" (upper + lower + digit + symbol, 9 chars).

  Scenario Outline: Password is rejected when it is too short
    Given the admin navigates to the Admin page
    When the admin clicks the Add button
    And the admin fills in user role "ESS", employee name "Ranga", username "pwdtest", status "Enabled", password "<password>", confirm password "<password>"
    And the admin saves the user form
    Then a password error message should be displayed

    # All passwords below are < 8 characters → OrangeHRM blocks form submission
    # with "Should have at least 8 characters" (oxd-input-field-error-message).
    Examples:
      | password |
      | admin    |
      | Ab1@     |
      | Ts@1     |
