@Calabash_Tests @cart
Feature: Cart features

  	Background: 
    Given I call the variables
	
    @add_to_cart
   	Scenario: Add product to cart
   	And I select the country
	And I wait for 5 seconds
	
#Login
	And I open the navigation menu
	* I wait for 2 seconds
	Then I choose the Sign In option
	* I wait for 5 seconds
    When I enter a valid username
  	And I enter the password
  	And I press Login Button
  	* I wait for 10 seconds
    Then I open the navigation menu
    * I wait for 10 seconds
	And I should see sign out button
	
   	#When I open the navigation menu
   	* I wait for 2 seconds
	And I enter Categories
	* I wait for 5 seconds
	And I enter a valid Category
	* I wait for 3 seconds
	And I press list item number 1
	* I wait for 3 seconds
	And I press list item number 2
	* I wait for 5 seconds
	Then I press Got it
	And I add product to cart
	* I wait for 8 seconds
	Then I should see the item was added to shopping cart message
 	When I go to cart
 	Then I should not see the no items message
 	
 	@change_quantity
 	Scenario: I increase the quatity of a product
   	When I open the navigation menu
	And I enter Categories
	* I wait for 5 seconds
	And I enter a valid Category
	* I wait for 3 seconds
	And I press list item number 1
	* I wait for 3 seconds
	And I press list item number 2
	* I wait for 5 seconds
	And I add product to cart
	* I wait for 15 seconds
	Then I should see the item was added to shopping cart message
 	When I go to cart
 	And I change the product quantity
 	* I wait for 5 seconds
 	
 	@change_quantity_zero
 	Scenario: I change the quantity to zero
 	* I wait for 5 seconds
   	When I open the navigation menu
	* I wait for 5 seconds
	And I enter Categories
	* I wait for 3 seconds
	And I enter a valid Category
	* I wait for 3 seconds
	And I press list item number 1
	* I wait for 3 seconds
	And I press list item number 2
	* I wait for 5 seconds
	And I add product to cart
	* I wait for 15 seconds
	Then I should see the item was added to shopping cart message
 	When I go to cart
 	And I change the product quantity to zero
 	* I wait for 5 seconds
 	Then I should see the empty cart message
 	
 	 	@cart_delete
 	Scenario: Delete product from cart
   	When I open the navigation menu
   	* I wait for 5 seconds
	And I enter Categories
	* I wait for 3 seconds
	And I enter a valid Category
	* I wait for 3 seconds
	And I press list item number 1
	* I wait for 3 seconds
	And I press list item number 2
	* I wait for 5 seconds
	And I add product to cart
	* I wait for 15 seconds
	Then I should see the item was added to shopping cart message
 	When I go to cart
 	* I wait for 5 seconds
 	And I press delete product
 	Then I should see the empty cart message
 	
