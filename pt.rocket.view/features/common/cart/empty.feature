@cart_empty
Feature: Test the empty cart

	Background: 
	Given I call the variables
	#Then I open the navigation menu
	
   	Scenario: See the empty cart
 	When I go to cart
 	Then I should see the empty cart message
 	When I open the navigation menu
 	And I go to home
 	