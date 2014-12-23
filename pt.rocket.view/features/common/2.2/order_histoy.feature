@Calabash_Tests @f_2.2 @my_order_history
Feature: 2.2 Features - My Order

Background: 
* I call the variables

Scenario: Choose Country
* I select the country

Scenario: Login
* I wait to see the home
* I wait for 2 seconds
* I click on the overflow button
* I wait for 2 seconds
* I choose the Sign In option
* I wait for 2 seconds
* I should wait and see login screen
* I wait for 2 seconds
* I enter a valid username
* I enter the password
* I wait for 2 seconds
* I press Login Button
* I wait to see the home

Scenario: Order History
* I wait to see the home
* I wait for 2 seconds
* I click on the overflow button
* I wait for 2 seconds
* I should see the overflow options
* I wait for 2 seconds
* I enter track my order
* I wait for 2 seconds
* I press my order history
#* I wait for 2 seconds
#* I should see my order history
