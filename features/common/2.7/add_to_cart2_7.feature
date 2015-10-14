@Calabash_Tests @f_2.7 @add_to_cart_2.7
Feature: 2.7 Features - Add to Cart

Background:
* I call the variables

Scenario: Add and Delete from cart
* I select the country
* I wait to see the home
* I swipe right moving with 5 steps
* I wait for 2 seconds
* I should see the Categories
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I press Got it
* I press grid item number 2
* I wait for 2 seconds
* I press Got it
* I wait for 2 seconds
* I add product to cart
* I go to cart
* I wait for 2 seconds
* I should not see the no items message
* I Press the delete button
* I wait for 2 seconds
* I should see the no items message
* I wait for 5 seconds

Scenario: Add and Change Quantity
* I wait to see the home
* I swipe right moving with 5 steps
* I wait for 2 seconds
* I should see the Categories
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I press grid item number 2
* I wait for 2 seconds
* I add product to cart
* I go to cart
* I wait for 2 seconds
* I should not see the no items message
* I change the product quantity
* I wait for 5 seconds
