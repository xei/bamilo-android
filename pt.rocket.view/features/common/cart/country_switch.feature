@Calabash_Tests
Feature: Country switch

	Background: 
	Given I call the variables
	And I select the country
	And I wait for 5 seconds
	And I Login
	And I add a product
	When I open the navigation menu
	
	@country_switch
	Scenario: Country switch
	And I press Choose Country
	And I press list item number 7
	Then I should see the clear the cart message
	And I press Yes
	* I wait for 5 seconds
	When I press the cart icon
	Then I should see the empty message
	