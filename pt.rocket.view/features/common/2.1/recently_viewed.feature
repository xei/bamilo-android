@Calabash_Tests @f_2.1 @recently_2.1
Feature: 2.1 Features - Recently Searches

Background: 
Given I call the variables

Scenario: Choose Country
Given I select the country

Scenario: All items to cart
Given I wait to see the home
When I open the navigation menu
* I wait for 1 seconds
And I enter a valid Category
* I wait for 1 seconds
And I enter a valid Category
* I wait for 5 seconds
And I press Got it
And I press gridview item number 1
#And I press list item number 2
#* I wait for 2 seconds
And I press Got it
* I wait for 2 seconds

When I click on the overflow button
* I wait for 1 seconds
And I enter recently viewed
* I wait for 1 seconds
Then I should see the recently viewed
And I press add all items to cart
* I wait for 1 seconds
And I press the cart icon
* I wait for 1 seconds
Then I should not see the no items message

Scenario: Continue Shopping
Given I wait to see the home
* I wait for 1 seconds
When I click on the overflow button
* I wait for 1 seconds
And I enter recently viewed
* I wait for 1 seconds
Then I should see the recently viewed
* I wait for 1 seconds
And I press continue shopping
* I wait for 1 seconds