@Calabash_Tests @price @f_1.9
Feature: 1.9 Features - Price parses

Background: 
Given I call the variables

Scenario: Validate the prices
Given I select the country
And I wait to see the home
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
* I wait for 3 seconds
And I press list item number 2
* I wait for 5 seconds
And I press Got it
Then I should see the price

	
