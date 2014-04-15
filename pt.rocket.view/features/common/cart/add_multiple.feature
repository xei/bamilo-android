@Calabash_Tests
Feature: Cart features

  	Background: 
    Given I call the variables
	
 	@add_multiple
 	Scenario: Add multiple products to cart
   	And I select the country
	And I wait for 5 seconds
	* I Login
 	When I add multiple products
 	
