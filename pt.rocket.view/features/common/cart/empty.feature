@cart_empty @Calabash_Tests
Feature: Test the empty cart

Background: 
Given I call the variables
And I select the country

Scenario: See the empty cart
Given I wait to see the home
When I go to cart
Then I should see the empty cart message
