@f_2.1 @address_ug
Feature: 2.1 Features - Native Checkout

Background: 
Given I call the variables

Scenario: Choose Country
Given I select the country

Scenario: In UG create an address, with city manualy
#Login
Given I wait to see the home
When I click on the overflow button
* I wait for 1 seconds
And I choose the Sign In option
* I wait for 1 seconds
Then I should see login screen
And I enter a valid username
And I enter the password
And I press Login Button
Then I wait to see the home
#Add product
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
#Checkout
* I wait for 5 seconds
And I press Proceed to Checkout
* I wait for 5 seconds
And I press Add new address
And I fill the new address form
And I press Next

