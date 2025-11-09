Feature: Parabank Registration
  Verify registration flow using positive and negative scenarios

  Background:
    Given I am on the Parabank home page

  Scenario: Successful registration with a unique username
    When I open the registration form
    When I register with username "unique" and password "Password123!"
    Then I should see a successful registration message for "testuser"
    And I logout

  Scenario: Registration fails when username already exists
    When I open the registration form
    # use a username likely to already exist (created earlier in a manual session)
    When I register with username "abcd_xyz" and password "abcd@123"
    Then I should see an error mentioning username exists
    And I logout

  Scenario: Registration fails when password is missing
    When I open the registration form
    When I register with username "unique" and password ""
    Then I should see a password validation error
    And I logout

  Scenario: Registration fails when password confirmation is missing
    When I open the registration form
    When I register but clear the confirmation field
    Then I should see a password validation error
    And I logout

  Scenario: Registration fails when a required field is missing (First Name)
    When I open the registration form
    When I register but clear the "first name" field
    Then I should see a required field error for "First Name"
    And I logout

  Scenario Outline: Registration cases driven by examples
    When I open the registration form
    When I register with username "<username>" and password "<password>"
    Then I should see outcome "<outcome>" for username "<username>" and field "<field>"
    And I logout
  Examples:
    | username | password       | outcome          | field      |
    # | unique   | Password123!   | success          | -          |
    | abcd_xyz | abcd@123       | username_exists  | -          |
    | unique   |                | password_error   | -          |
    # | unique   | Password123!   | required_field   | first name |