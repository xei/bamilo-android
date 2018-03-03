@f_2.1 @address_ug
Feature: 2.1 Features - Native Checkout

Background: 
Given I call the variables

Scenario: Choose Country
Given I select the country

Scenario: In UG create an billingAddress, with city manualy
#Login
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should wait and see login screen
* I enter a valid username
* I enter the password
* I press Login Button
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
* I press grid item number 2
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
* I press Add new billingAddress
* I fill the new billingAddress form
* I press Next

