@Calabash_Tests @f_2.1 @favorites_2.1
Feature: 2.1 Features - Favorites

Background: 
* I call the variables

Scenario: Choose Country
* I select the country

Scenario: Favorites delete icon
* I wait to see the home
* I open the navigation menu
* I should see the menu sections
* I wait for 1 seconds
* I enter a valid Category
* I wait for 1 seconds
* I enter a valid Category
* I wait for 5 seconds
* I press Got it
* I wait for 1 seconds
* I press grid item number 2
* I wait for 1 seconds
#And I press list item number 2
#* I wait for 2 seconds
* I press Got it
* I wait for 2 seconds
* I click on the favorite icon
* I should see the item added message
* I wait for 1 seconds
* I click on the overflow button
* I wait for 1 seconds
* I enter my favorites
* I wait for 1 seconds
* I should see the my favorites

* I Press the delete button
* I wait for 1 seconds

Scenario: All items to cart
* I wait to see the home
* I open the navigation menu
* I should see the menu sections
* I wait for 1 seconds
* I enter a valid Category
* I wait for 1 seconds
* I enter a valid Category
* I wait for 5 seconds
* I press grid item number 2
#And I press list item number 2
#* I wait for 2 seconds
#And I press Got it
* I wait for 2 seconds
* I click on the favorite icon
* I should see the item added message
* I wait for 1 seconds
* I click on the overflow button
* I wait for 1 seconds
* I enter my favorites
* I wait for 1 seconds
* I should see the my favorites
* I press add all items to cart
* I wait for 5 seconds
* I press the cart icon
* I wait for 1 seconds
* I should not see the no items message

Scenario: Continue Shopping
* I wait to see the home
* I wait for 1 seconds
* I click on the overflow button
* I wait for 1 seconds
* I enter my favorites
* I wait for 1 seconds
* I press continue shopping
* I wait for 1 seconds