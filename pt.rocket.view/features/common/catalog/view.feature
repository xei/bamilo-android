@Calabash_Tests @teste2
Feature: Catalog

	Background: 
	Given I call the variables
	
	@catalog
	Scenario: Catalogue view
	And I select the country
	And I wait for 5 seconds
	And I open the navigation menu
	When I enter Categories
	* I wait for 3 seconds
	And I enter a valid Category
	And I press list item number 1
	* I wait for 3 seconds
	Then I should see the Catalog
		
	@infinite_scroll
	Scenario: Scroll down to the end of the page
	* I wait for 3 seconds
	And I open the navigation menu
	* I wait for 3 seconds
	When I enter Categories
	* I wait for 3 seconds
	And I enter a valid Category
	And I press list item number 1
	* I wait for 3 seconds
	When I swipe up moving with 1 steps
	When I swipe up moving with 1 steps
	When I swipe up moving with 1 steps
	When I swipe up moving with 1 steps
	#Then I should see the loading items message
	
	