@Calabash_Tests @f_2.7 @edit_address
Feature: 2.7 Features - My Account - Edit Address

Background:
* I call the variables

Scenario: Edit Address without any changes
* I select the country
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I choose the Sign In option
* I wait for 2 seconds
* I should wait and see login screen
* I wait for 2 seconds
* I enter a valid username
* I wait for 1 seconds
* I enter the password
* I swipe down moving with 2 steps
* I press Login Button
* I wait to see the home
* I wait for the overflow button
* I wait for 5 seconds
* I enter My Account
* I wait for 2 seconds
* I should see my account
* I wait for 2 seconds
* I enter My Addresses
* I wait for 5 seconds
* I press Edit Address Icon
* I wait for 2 seconds
* I press Save Changes
* I should see the Address Edited Message

Scenario: Edit Address with empty fields
* I wait to see the home
* I wait for the overflow button
* I wait for 5 seconds
* I enter My Account
* I wait for 2 seconds
* I should see my account
* I wait for 2 seconds
* I enter My Addresses
* I wait for 5 seconds
* I press Edit Address Icon
* I wait for 2 seconds
* I clean the Edit Address Form
* I press Save Changes
* I swipe down moving with 2 steps
* I should see the mandatory First Name error message
* I should see the mandatory Last Name error message
* I should see the mandatory Address error message

Scenario: Edit Address changing Address Field
* I wait to see the home
* I wait for the overflow button
* I wait for 5 seconds
* I enter My Account
* I wait for 2 seconds
* I should see my account
* I wait for 2 seconds
* I enter My Addresses
* I wait for 5 seconds
* I press Edit Address Icon
* I wait for 2 seconds
* I enter a new value in Address
* I press Save Changes
* I should see the Address Edited Message



