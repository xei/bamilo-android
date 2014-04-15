@cart_empty @Calabash_Tests
Feature: Test the empty cart

	Background: 
	Given I call the variables
	And I select the country
	And I wait for 5 seconds
	#Then I open the navigation menu
	
   	Scenario: See the empty cart
 	When I go to cart
 	Then I should see the empty cart message
 	When I open the navigation menu
 	And I go to home
 	