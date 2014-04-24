#@Calabash_Tests
Feature: Add Multiple produts features

  	Background: 
    Given I call the variables
	
 	@add_multiple
 	Scenario: Add multiple products to cart
   	And I select the country
	And I wait for 5 seconds
	* I Login
	#1
   	When I open the navigation menu
	And I enter Categories
	And I enter a valid Category
	And I press list item number 1
	* I wait for 3 seconds
	And I press list item number 1
	* I wait for 5 seconds
	Then I press Got it
	And I add product to cart
	* I wait for 5 seconds
	When I go to cart
 	#2
   	When I open the navigation menu
	And I enter Categories
	And I enter a valid Category
	And I press list item number 1
	* I wait for 3 seconds
	And I press list item number 2
	* I wait for 5 seconds
	And I add product to cart
	* I wait for 5 seconds
	When I go to cart
 	#3
   	When I open the navigation menu
	And I enter Categories
	And I enter a valid Category
	And I press list item number 1
	* I wait for 3 seconds
	And I press list item number 3
	* I wait for 5 seconds
	And I add product to cart
	* I wait for 5 seconds
	When I go to cart
	#4
   	When I open the navigation menu
	And I enter Categories
	And I enter a valid Category
	And I press list item number 1
	* I wait for 3 seconds
	And I press list item number 4
	* I wait for 5 seconds
	And I add product to cart
	* I wait for 5 seconds
	When I go to cart
	#5
   	When I open the navigation menu
	And I enter Categories
	And I enter a valid Category
	And I press list item number 1
	* I wait for 3 seconds
	And I press list item number 5
	* I wait for 5 seconds
	And I add product to cart
	* I wait for 5 seconds
	When I go to cart
 	
