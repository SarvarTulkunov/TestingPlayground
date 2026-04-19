Feature: Login into Orange HRM

  Background:
    Given the user is on the OrangeHRM login page

  Scenario: Admin logs in with valid credentials
    When the user enters username "Admin" and password "admin123"
    And the user clicks the login button
    Then the user should be redirected to the dashboard page

  Scenario: Admin logs in with invalid credentials
    When the user enters username "Admin" and password "wrongpassword"
    And the user clicks the login button
    Then an error message "Invalid credentials" should be displayed
