@Calabash_Tests 
Feature: Country switch

	Background: 
	Given I call the variables
	And I select the country
	And I wait for 5 seconds
	And I Login
	When I open the navigation menu
	And I enter Categories
	And I enter a valid Category
	And I press list item number 1
	* I wait for 3 seconds
	And I press list item number 1
	* I wait for 3 seconds
	Then I press Got it
	And I add product to cart
	* I wait for 5 seconds
	Then I should see the item was added to shopping cart message
 	When I go to cart
	When I open the navigation menu
	
	@country_switch
	Scenario: Country switch
	And I press Choose Country
	* I wait for 2 seconds
	And I press list item number 7
	* I wait for 1 seconds
	Then I should see the clear the cart message
	And I press Yes
	* I wait for 5 seconds
	When I press the cart icon
	Then I should see the empty message
	