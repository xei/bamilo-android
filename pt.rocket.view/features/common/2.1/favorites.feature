@Calabash_Tests @f_2.1 @favorites_2.1
Feature: 2.1 Features - Favorites

Background: 
Given I call the variables

Scenario: Choose Country
Given I select the country

Scenario: Favorites delete icon
Given I wait to see the home
When I open the navigation menu
* I wait for 1 seconds
And I enter a valid Category
* I wait for 1 seconds
And I enter a valid Category
* I wait for 5 seconds
And I press Got it
* I wait for 1 seconds
And I press gridview item number 1
* I wait for 1 seconds
#And I press list item number 2
#* I wait for 2 seconds
And I press Got it
* I wait for 2 seconds
And I click on the favorite icon
Then I should see the item added message
* I wait for 1 seconds
When I click on the overflow button
* I wait for 1 seconds
And I enter my favorites
* I wait for 1 seconds
Then I should see the my favorites

And I Press the delete button
* I wait for 1 seconds

Scenario: All items to cart
Given I wait to see the home
When I open the navigation menu
* I wait for 1 seconds
And I enter a valid Category
* I wait for 1 seconds
And I enter a valid Category
* I wait for 5 seconds
And I press gridview item number 1
#And I press list item number 2
#* I wait for 2 seconds
#And I press Got it
* I wait for 2 seconds
And I click on the favorite icon
Then I should see the item added message
* I wait for 1 seconds
When I click on the overflow button
* I wait for 1 seconds
And I enter my favorites
* I wait for 1 seconds
Then I should see the my favorites
And I press add all items to cart
* I wait for 5 seconds
And I press the cart icon
* I wait for 1 seconds
Then I should not see the no items message

Scenario: Continue Shopping
Given I wait to see the home
* I wait for 1 seconds
When I click on the overflow button
* I wait for 1 seconds
And I enter my favorites
* I wait for 1 seconds
And I press continue shopping
* I wait for 1 seconds