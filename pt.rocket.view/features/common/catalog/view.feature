Feature: 

	Background: 
	Given I call the variables
	And I open the navigation menu
	When I enter Categories
	And I enter a valid Category
	And I press list item number 1
	* I wait for 3 seconds	
	
	@catalog
	Scenario: Catalogue view
		Open a category
		Catalogue is shown (as on desktop)
	Then I should see the Catalog
		
	@infinite_scroll
	Scenario: Scroll down to the end of the page
		Products should load after reaching the infinite scroll loader
	When I swipe up moving with 10 steps
	Then I should see the loading items message
	
	@categorie_item_number
	Scenario: See the number of items
	Then I should see the number of items
	
	