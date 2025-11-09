Feature: Open New Account
  Verify opening a new account after login

  Background:
    Given I am on the Parabank home page

  Scenario: Open a new account and verify success message
    When I login with username "abcd_xyz" and password "abcd@123"
    And I navigate to Open New Account page
    And I select account type "SAVINGS" and from existing account "22335"
    And I click Open New Account
    Then I should see a successful account opened message containing "Account Opened!" and the new account number
    And I logout