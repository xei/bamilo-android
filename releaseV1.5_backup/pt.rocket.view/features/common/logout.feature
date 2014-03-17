@logout
Feature: Login feature
	
	Background:
	Given I call the variables
	And I open the navigation menu
	
	@logout
	Scenario: Logout successful
	When I press Logout Button
  	And I open the navigation menu
  	Then I should see the login button 
  	And I close the navigation menu