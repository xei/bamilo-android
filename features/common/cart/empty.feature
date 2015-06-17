@cart_empty @Calabash_Tests @f_pre_1.9
Feature: Cart - Empty Cart

Background: 
* I call the variables
* I select the country

Scenario: See the empty cart
* I wait for 2 seconds
* I wait to see the home
* I wait for 2 seconds
* I go to cart
* I wait for 2 seconds
* I should see the empty cart message
