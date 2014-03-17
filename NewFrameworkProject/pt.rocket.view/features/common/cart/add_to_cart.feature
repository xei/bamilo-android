@cart
Feature: Add To Cart feature

  	Background: 
    Given I call the variables
    When I open the navigation menu
        
    @add_to_cart
   	Scenario: Add product to cart
	When I enter Categories
	And I enter a valid Category
	And I press list item number 1
	And I press list item number 4
	* I wait for 3 seconds
	Then I press Got it
	And I add product to cart
	* I wait for 8 seconds
	Then I should see the item was added to shopping cart message
 	When I go to cart
 	Then I should not see the no items message
 	When I open the navigation menu
 	And I go to home
 	
 	