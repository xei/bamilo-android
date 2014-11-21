@Calabash_Tests @f_2.1 @cart_2.1
Feature: 2.1 Features - Cart

Background: 
Given I call the variables

Scenario: Choose Country
Given I select the country

Scenario: Delete button
Given I wait to see the home
When I open the navigation menu
* I wait for 1 seconds
And I enter a valid Category
* I wait for 1 seconds
And I enter a valid Category
* I wait for 3 seconds
And I press Got it
And I press gridview item number 1
* I wait for 5 seconds
Then I press Got it
And I add product to cart
* I wait for 10 seconds
Then I should see the item was added to shopping cart message
When I go to cart
Then I should not see the no items message
And I Press the delete button
* I wait for 1 seconds

Scenario: Call to order
Given I wait to see the home
When I open the navigation menu
* I wait for 1 seconds
And I enter a valid Category
* I wait for 1 seconds
And I enter a valid Category
* I wait for 3 seconds
And I press gridview item number 1
* I wait for 5 seconds
#Then I press Got it
And I add product to cart
* I wait for 10 seconds
Then I should see the item was added to shopping cart message
When I go to cart
Then I should not see the no items message
* I wait for 1 seconds
And I Press the call to order button
* I wait for 1 seconds