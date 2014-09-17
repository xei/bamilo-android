@Calabash_Tests @cart @f_pre_1.9
Feature: Cart

Background: 
Given I call the variables

@add_to_cart
Scenario: Add product to cart
Given I select the country
And I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen
When I enter a valid username
And I enter the password
And I press Login Button
And I wait to see the home
When I click on the overflow button
Then I should see sign out button
And I click on the overflow button

When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
* I wait for 3 seconds
And I press Got it
And I press list item number 2
* I wait for 5 seconds
Then I press Got it
And I add product to cart
* I wait for 10 seconds
Then I should see the item was added to shopping cart message
When I go to cart
Then I should not see the no items message

@change_quantity
Scenario: I increase the quatity of a product
Given I wait to see the home
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
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
Given I wait to see the home
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
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
Given I wait to see the home
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
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
 	
