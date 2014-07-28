@logout @Calabash_Tests
Feature: Logout feature
	
Background:
Given I call the variables
And I select the country
And I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen
When I enter a valid username
And I enter the password
And I press Login Button
And I wait to see the home
And I click on the overflow button
Then I should see sign out button

@logout
Scenario: Logout successful
When I press Logout Button
And I press Yes
And I wait to see the home
And I click on the overflow button
Then I should see the login button 