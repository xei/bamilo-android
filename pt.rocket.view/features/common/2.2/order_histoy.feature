@Calabash_Tests @f_2.2 @my_order_history
Feature: 2.2 Features - My Order

Background: 
* I call the variables

Scenario: Choose Country
* I select the country

Scenario: Login
* I wait to see the home
* I click on the overflow button
* I wait for 1 seconds
* I choose the Sign In option
* I wait for 1 seconds
* I should see login screen
* I enter a valid username
* I enter the password
* I press Login Button
* I wait to see the home

Scenario: Order History
* I wait to see the home
* I click on the overflow button
* I wait for 2 seconds
* I should see the overflow options
* I enter track my order
* I wait for 2 seconds
* I press my order history
* I should see my order history
