@logout @Calabash_Tests
Feature: Logout feature
	
	Background:
	Given I call the variables
	And I select the country
	And I wait for 5 seconds
	And I open the navigation menu
	Then I choose the Sign In option
	* I wait for 5 seconds
	When I enter a valid username
  	And I enter the password
  	And I press Login Button
  	* I wait for 5 seconds
    Then I open the navigation menu
    * I wait for 5 seconds
	And I should see sign out button
	
	@logout
	Scenario: Logout successful
	When I press Logout Button
  	And I open the navigation menu
  	Then I should see the login button 