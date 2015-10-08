@logout @Calabash_Tests @f_pre_1.9  @f_2.7
Feature: Logout feature
	
Background:
* I call the variables
* I wait for 2 seconds
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
* I wait for 2 seconds
* I press Login Button
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I should see sign out button

@logout
Scenario: Logout successful
* I press Logout Button
* I wait for 2 seconds
* I press Yes
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I should see the login button 