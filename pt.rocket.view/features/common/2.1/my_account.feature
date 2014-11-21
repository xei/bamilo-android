@Calabash_Tests @f_2.1 @my_account_2.1
Feature: 2.1 Features - My Account

Background: 
Given I call the variables

Scenario: Choose Country
Given I select the country

Scenario: Notification Settings
#Login
Given I wait to see the home
When I click on the overflow button
* I wait for 1 seconds
And I choose the Sign In option
* I wait for 1 seconds
Then I should see login screen
And I enter a valid username
And I enter the password
And I press Login Button
Then I wait to see the home

When I click on the overflow button
* I wait for 1 seconds
And I enter My Account
* I wait for 1 seconds
Then I should see my account
* I wait for 1 seconds
And I should not see the vibrate option
And I should not see the sound option
