@Calabash_Tests @teste2
Feature: Catalog

	Background: 
	Given I call the variables
	
	@catalog
	Scenario: Catalogue view
		Open a category
		Catalogue is shown (as on desktop)
	And I select the country
	And I wait for 5 seconds
	And I open the navigation menu
	When I enter Categories
	And I enter a valid Category
	And I press list item number 1
	* I wait for 3 seconds
	Then I should see the Catalog
		
	@infinite_scroll
	Scenario: Scroll down to the end of the page
		Products should load after reaching the infinite scroll loader
	And I open the navigation menu
	When I enter Categories
	And I enter a valid Category
	And I press list item number 1
	* I wait for 3 seconds
	When I swipe up moving with 2 steps
	Then I should see the loading items message
	
	