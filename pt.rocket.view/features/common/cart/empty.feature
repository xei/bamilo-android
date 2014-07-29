@cart_empty @Calabash_Tests @f_pre_1.9
Feature: Cart - Empty Cart

Background: 
Given I call the variables
And I select the country

Scenario: See the empty cart
Given I wait to see the home
When I go to cart
Then I should see the empty cart message
