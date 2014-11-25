@logout @Calabash_Tests @f_pre_1.9
Feature: Logout feature
	
Background:
* I call the variables
* I select the country
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
* I should see sign out button

@logout
Scenario: Logout successful
* I press Logout Button
* I press Yes
* I wait to see the home
* I click on the overflow button
* I should see the login button 