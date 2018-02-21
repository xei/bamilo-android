@Calabash_Tests @f_2.7 @create_address
Feature: 2.7 Features - My Account - Create Address

Background:
* I call the variables

Scenario: Create Address with empty fields
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
* I wait for 4 seconds
* I should see my account
* I wait for 4 seconds
* I enter My Addresses
* I wait for 5 seconds
* I press Add New Address
* I wait for 4 seconds
* I press Save
* I wait for 5 seconds
* I should see the mandatory First Name error message
* I should see the mandatory Last Name error message
* I should see the mandatory Address error message

Scenario: Create Address with success
* I wait to see the home
* I wait for the overflow button
* I enter My Account
* I wait for 2 seconds
* I should see my account
* I wait for 2 seconds
* I enter My Addresses
* I wait for 5 seconds
* I press Add New Address
* I wait for 2 seconds
* I fill the new billingAddress form
* I wait for 2 seconds
* I press Save
* I wait for 5 seconds
* I should see other addresses



