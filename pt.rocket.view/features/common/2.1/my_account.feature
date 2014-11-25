@Calabash_Tests @f_2.1 @my_account_2.1
Feature: 2.1 Features - My Account

Background: 
* I call the variables

Scenario: Choose Country
* I select the country

Scenario: Notification Settings
#Login
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should see login screen
* I enter a valid username
* I enter the password
* I press Login Button
* I wait to see the home

* I click on the overflow button
* I wait for 1 seconds
* I enter My Account
* I wait for 1 seconds
* I should see my account
* I wait for 1 seconds
* I should not see the vibrate option
* I should not see the sound option
