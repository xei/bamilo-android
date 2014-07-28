@native_checkout @Calabash_Tests
Feature: Native Checkout

Background: 
Given I call the variables
And I select the country
 
#Login    
And I wait to see the home
When I click on the overflow button
And I choose the Sign In option
Then I should see login screen
When I enter a valid username
And I enter the password
And I press Login Button
And  I wait to see the home
And I click on the overflow button
Then I should see sign out button
And I click on the overflow button
    
#Add product to cart
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
* I wait for 3 seconds
And I press list item number 1
* I wait for 3 seconds
Then I press Got it
And I add product to cart
* I wait for 5 seconds
Then I should see the item was added to shopping cart message
When I go to cart
Then I should not see the no items message

Scenario: Valid Checkout
And I press Proceed to Checkout
And I wait for 15 seconds
And I press Next
And I wait for 7 seconds
And I press Next
And I wait for 5 seconds
And I press Pay on Delivery
And I press Next
And I wait for the confirm order
And I press Confirm Order
Then I should see the thank you screen
