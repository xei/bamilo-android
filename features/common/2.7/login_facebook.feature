@login_facebook @Calabash_Tests @f_2.7
Feature: Facebook Login feature

Background:
* I call the variables

@login_e
Scenario: Login with facebook (the user must already be sing in to his facebook account)
* I select the country
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I choose the Sign In option
* I wait for 2 seconds
* I should wait and see login screen
* I press Facebook Login Button
* I wait for 10 seconds
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I should see sign out button
