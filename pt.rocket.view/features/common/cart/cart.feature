Feature: Cart features

  	Background: 
    Given I call the variables
    
    @add_to_cart
   	Scenario: Add product to cart
   	When I open the navigation menu
	And I enter Categories
	And I enter a valid Category
	And I press list item number 1
	And I press list item number 4
	* I wait for 3 seconds
	Then I press Got it
	And I add product to cart
	* I wait for 5 seconds
	Then I should see the item was added to shopping cart message
 	When I go to cart
 	Then I should not see the no items message
 	When I open the navigation menu
 	And I go to home
 	
 	@add_multiple
 	Scenario: Add multiple products to cart
 	When I add multiple products
 	
 	@cart_delete
 	Scenario: Delete product from cart
 	When I go to cart
 	* I wait for 5 seconds
 	And I press delete product
 	Then I should see the empty cart message
 	When I open the navigation menu
 	And I go to home
 	
 	@change_quantity
 	Scenario: I increase the quatity of a product
 	When I go to cart
 	And I change the product quantity
 	* I wait for 5 seconds
 	And I open the navigation menu
 	And I go to home
 	
 	@change_quantity_zero
 	Scenario: I change the quantity to zero
 	When I go to cart
 	And I change the product quantity to zero
 	* I wait for 5 seconds
 	Then I should see the empty cart message
 	And I open the navigation menu
 	And I go to home
 	