@Calabash_Tests @cart @f_pre_1.9
Feature: Cart

Background: 
* I call the variables

@add_to_cart
Scenario: Add product to cart
* I select the country
* I wait for 2 seconds
* I wait to see the home
* I wait for the overflow button
* I wait for 2 seconds
* I choose the Sign In option
* I wait for 5 seconds
* I should wait and see login screen
* I wait for 2 seconds
* I enter a valid username
* I wait for 2 seconds
* I enter the password
* I wait for 2 seconds
* I press Login Button
* I wait to see the home
* I wait for the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I enter a valid Category
* I wait for 5 seconds
* I press Got it
* I wait for 2 seconds
* I press grid item number 1
* I wait for 3 seconds
* I press Got it
* I add product to cart
* I should see the item was added to shopping cart message
* I wait for 2 seconds
* I go to cart
* I wait for 4 seconds
* I should not see the no items message

@change_quantity
Scenario: I increase the quantity of a product
* I wait to see the home
* I wait for 2 seconds
* I wait for the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I press grid item number 2
* I wait for 2 seconds
#* I wait for 5 seconds
#* I press Got it
* I add product to cart
* I should see the item was added to shopping cart message
* I wait for 2 seconds
* I go to cart
* I wait for 2 seconds
* I change the product quantity

@change_quantity_zero
Scenario: I change the quantity to zero
* I wait to see the home
* I wait for 2 seconds
* I wait for the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I press grid item number 1
* I wait for 2 seconds
* I add product to cart
* I should see the item was added to shopping cart message
* I wait for 2 seconds
* I go to cart
* I wait for 2 seconds
* I reset the product quantity
* I should see the empty cart message

@cart_delete
Scenario: Delete product from cart
* I wait to see the home
* I wait for the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I press grid item number 2
* I wait for 2 seconds
* I add product to cart
* I should see the item was added to shopping cart message
* I wait for 2 seconds
* I go to cart
* I wait for 15 seconds
* I press delete product
* I wait for 5 seconds
* I should see the empty cart message
 	
