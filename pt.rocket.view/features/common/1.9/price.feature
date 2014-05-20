@Calabash_Tests @price
Feature: Price parses

  	Background: 
    Given I call the variables
	
   	Scenario: Validate the prices
   	When I select the country
	* I wait for 5 seconds
	
   	And I open the navigation menu
	And I enter Categories
	* I wait for 1 seconds
	And I enter a valid Category
	And I press list item number 1
	* I wait for 3 seconds
	And I press list item number 2
	* I wait for 5 seconds
	And I press Got it
	Then I should see the price

	
