@Calabash_Tests @f_2.1 @recently_2.1
Feature: 2.1 Features - Recently Searches

Background: 
* I call the variables

Scenario: Choose Country
* I select the country

Scenario: All items to cart
* I wait to see the home
* I open the navigation menu
* I wait for 1 seconds
* I enter a valid Category
* I wait for 1 seconds
* I enter a valid Category
* I wait for 5 seconds
* I press Got it
* I press gridview item number 1
#And I press list item number 2
#* I wait for 2 seconds
* I press Got it
* I wait for 2 seconds

* I click on the overflow button
* I wait for 1 seconds
* I enter recently viewed
* I wait for 1 seconds
* I should see the recently viewed
* I press add all items to cart
* I wait for 1 seconds
* I press the cart icon
* I wait for 1 seconds
* I should not see the no items message

Scenario: Continue Shopping
* I wait to see the home
* I wait for 1 seconds
* I click on the overflow button
* I wait for 1 seconds
* I enter recently viewed
* I wait for 1 seconds
* I should see the recently viewed
* I wait for 1 seconds
* I press continue shopping
* I wait for 1 seconds