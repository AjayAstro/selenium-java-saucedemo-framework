@bdd @login
Feature: SauceDemo login
  As a SauceDemo user
  I want the login form to behave correctly
  So that only valid users can access the store

  Background:
    Given the login page is open

  @smoke
  Scenario: Standard user logs in successfully
    When I log in as the standard user
    Then I should land on the inventory page

  Scenario: Locked-out user is rejected
    When I log in as "locked_out_user" with password "secret_sauce"
    Then I should see the login error "Epic sadface: Sorry, this user has been locked out."

  Scenario Outline: Invalid credentials are rejected
    When I log in as "<username>" with password "<password>"
    Then I should see the login error "<message>"

    Examples:
      | username      | password      | message                                                                   |
      | invalid_user  | secret_sauce  | Epic sadface: Username and password do not match any user in this service |
      | standard_user | wrong_pass    | Epic sadface: Username and password do not match any user in this service |
