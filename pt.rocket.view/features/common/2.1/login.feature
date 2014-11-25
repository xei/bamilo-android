@Calabash_Tests @f_2.1 @login_checkbox
Feature: 2.1 Features - Login

Background: 
* I call the variables

Scenario: Choose Country
* I select the country

@login_2.1_1
Scenario: New Checkbox to remember user email
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should see login screen
* I should see the remember user email checkbox

@login_2.1_2
Scenario: Login with checkbox unchecked
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should see login screen
* I click on remember user email
* I enter a valid username
* I enter the password
* I press Login Button
* I wait to see the home
* I click on the overflow button
* I should see sign out button
* I press Logout Button
* I press Yes
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I should see the login button 
* I choose the Sign In option
* I wait for 1 seconds
* I should see login screen
* I should not see email

Scenario: Login with checkbox checked
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should see login screen
* I should see the remember user email checkbox
* I enter a valid username
* I enter the password
* I press Login Button
* I wait to see the home
* I click on the overflow button
* I should see sign out button
* I press Logout Button
* I press Yes
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I should see the login button 
* I choose the Sign In option
* I wait for 1 seconds
* I should see login screen
* I should see email

