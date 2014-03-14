Feature: Check the product detail view

	Background: 
	Given I call the variables
    When I open the navigation menu
    And I enter Categories
	And I enter a valid Category
	And I press list item number 1
	And I press list item number 2
	* I wait for 3 seconds
	And I press Got it
	
	@product_full
	Scenario: Image Fullscreen
	When I click in the image
	Then I should not see the currency
	Then I go back
	
	@product_zoom
	Scenario: Zoom Image
	* I wait for 1 seconds
	When I click in the image
	When I click in the image
	#When I click in the image
	#And I pinch to zoom in
	#Then I go back
	
	@product_details
	Scenario: Details
	Then I should see the currency
	And I should see the specifications
	
	@product_zoom_in_out
	Scenario: Zoom the image then zoom out
	When I click in the image
	Then I should not see the currency
	When I go back
	Then I should see the specifications
	
	
	
	
	