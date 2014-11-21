@f_2.1 @signup_gender
Feature: 2.1 Features - Choose Gender on Signup

Background: 
Given I call the variables

Scenario: Choose Country
Given I select the country

Scenario: Choose gender
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
* I touch the signup area
* I enter the a new email
* I touch the signup button
* I wait for 10 seconds
* I scroll down
* I touch the female button