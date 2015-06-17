@f_2.1 @signup_gender
Feature: 2.1 Features - Choose Gender on Signup

Background: 
* I call the variables

Scenario: Choose Country
* I select the country

Scenario: Choose gender
* I wait to see the home
#Add product
* I open the navigation menu
* I should see the menu sections
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I press Got it
* I press gridview item number 1
* I wait for 5 seconds
* I press Got it
* I add product to cart
* I should see the item was added to shopping cart message
* I go to cart
* I should not see the no items message
#Checkout
* I wait for 5 seconds
* I press Proceed to Checkout
* I wait for 5 seconds
* I touch the signup area
* I enter the a new email
* I touch the signup button
* I wait for 10 seconds
* I scroll down
* I touch the female button