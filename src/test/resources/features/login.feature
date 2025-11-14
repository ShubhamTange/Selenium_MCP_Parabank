Feature: Parabank Login
  Verify login flow using positive and negative scenarios

  Background:
    Given I am on the Parabank home page

  Scenario: Successful login with valid credentials
    When I attempt to login with username "abcd_xyz" and password "abcd@123"
    Then I should see a successful login message for "abcd_xyz"
    And I logout

  Scenario: Login fails with invalid credentials
    When I attempt to login with username "invalid_user" and password ""
    Then I should see a login error mentioning invalid credentials
    And I logout

  Scenario: Login fails when password is missing
    When I attempt to login with username "testuser" and password ""
    Then I should see a login validation error for "password"
    And I logout
