Feature: Login into Orange HRM
  # Feature 1

  Background:
    Given the user is on the OrangeHRM login page

  # Task 1.1 – Valid credentials redirect to the dashboard
  Scenario: Admin logs in with valid credentials
    When the user enters username "Admin" and password "admin123"
    And the user clicks the login button
    Then the user should be redirected to the dashboard page

  # Task 1.2 – Invalid credentials show the "Invalid credentials" error
  Scenario: Admin logs in with invalid credentials
    When the user enters username "Admin" and password "wrongpassword"
    And the user clicks the login button
    Then an error message "Invalid credentials" should be displayed
