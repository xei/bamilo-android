@Calabash_Tests @f_2.1 @cart_2.1
Feature: 2.1 Features - Cart

Background: 
* I call the variables

Scenario: Choose Country
* I select the country

Scenario: Delete button
* I wait to see the home
* I open the navigation menu
* I should see the menu sections
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I press Got it
* I press grid item number 2
* I wait for 5 seconds
* I press Got it
* I add product to cart
* I wait for 10 seconds
* I should see the item was added to shopping cart message
* I go to cart
* I should not see the no items message
* I Press the delete button
* I wait for 1 seconds

Scenario: Call to order
* I wait to see the home
* I open the navigation menu
* I should see the menu sections
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I press grid item number 2
* I wait for 5 seconds
#Then I press Got it
* I add product to cart
* I wait for 10 seconds
* I should see the item was added to shopping cart message
* I go to cart
* I should not see the no items message
* I wait for 1 seconds
* I Press the call to order button
* I wait for 1 seconds