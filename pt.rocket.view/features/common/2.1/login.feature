@Calabash_Tests @f_2.1 @login_checkbox
Feature: 2.1 Features - Login

Background: 
* I call the variables

Scenario: Choose Country
* I select the country

@login_2.1_1
Scenario: New Checkbox to remember user email
* I wait to see the home
* I wait for 2 seconds
* I wait for the overflow button
* I wait for 2 seconds
* I choose the Sign In option
* I wait for 2 seconds
* I should wait and see login screen
* I wait for 2 seconds
* I should see the remember user email checkbox

@login_2.1_2
Scenario: Login with checkbox unchecked
* I wait to see the home
* I wait for 2 seconds
* I wait for the overflow button
* I wait for 2 seconds
* I choose the Sign In option
* I wait for 2 seconds
* I should wait and see login screen
* I wait for 2 seconds
* I click on remember user email
* I enter a valid username
* I wait for 1 seconds
* I enter the password
* I wait for 4 seconds
* I press Login Button
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I should see sign out button
* I press Logout Button
* I wait for 2 seconds
* I press Yes
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I should see the login button 
* I wait for 2 seconds
* I choose the Sign In option
* I wait for 2 seconds
* I should wait and see login screen
#* I wait for 2 seconds
#* I should not see email

Scenario: Login with checkbox checked
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I choose the Sign In option
* I wait for 2 seconds
* I should wait and see login screen
* I wait for 2 seconds
* I should see the remember user email checkbox
* I wait for 2 seconds
* I enter a valid username
* I wait for 1 seconds
* I enter the password
* I wait for 4 seconds
* I press Login Button
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I should see sign out button
* I wait for 2 seconds
* I press Logout Button
* I wait for 2 seconds
* I press Yes
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I should see the login button 
* I wait for 2 seconds
* I choose the Sign In option
* I wait for 2 seconds
* I should wait and see login screen
* I wait for 2 seconds
* I should see email

