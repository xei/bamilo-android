@Calabash_Tests @teste1
Feature: Check the product detail view

	Background: 
	Given I call the variables
	
	@product_full
	Scenario: Image Fullscreen
	And I select the country
	And I wait for 5 seconds
    When I open the navigation menu
    And I enter Categories
	And I enter a valid Category
	And I press list item number 1
	And I press list item number 2
	* I wait for 5 seconds
	And I press Got it
	When I click in the image
	Then I should not see the currency
	Then I go back
	
	@product_zoom
	Scenario: Zoom Image
    When I open the navigation menu
    And I enter Categories
	And I enter a valid Category
	And I press list item number 1
	And I press list item number 2
	* I wait for 3 seconds
	When I click in the image
	When I click in the image
	#When I click in the image
	#And I pinch to zoom in
	#Then I go back
	
	@product_details
	Scenario: Details
    When I open the navigation menu
    And I enter Categories
	And I enter a valid Category
	And I press list item number 1
	And I press list item number 2
	* I wait for 3 seconds
	Then I should see the currency
	And I should see the specifications
	
	
	
	