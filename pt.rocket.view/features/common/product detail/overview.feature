@Calabash_Tests @overview
Feature: Check the product detail view

Background: 
Given I call the variables

@product_full
Scenario: Image Fullscreen
Given I select the country
And I wait to see the home
When I open the navigation menu
* I wait for 10 seconds
And I enter a valid Category
And I enter a valid Category
* I wait for 5 seconds
And I press list item number 2
* I wait for 5 seconds
And I press Got it
When I click in the image
Then I should not see the currency
Then I go back
	
@product_zoom
Scenario: Zoom Image
Given I wait to see the home
When I open the navigation menu
* I wait for 5 seconds
And I enter a valid Category
And I enter a valid Category
* I wait for 5 seconds
And I press list item number 2
* I wait for 5 seconds
When I click in the image
When I click in the image
#When I click in the image
#And I pinch to zoom in
#Then I go back
	
@product_details
Scenario: Details
Given I wait to see the home
When I open the navigation menu
* I wait for 3 seconds
And I enter a valid Category
And I enter a valid Category
* I wait for 2 seconds
And I press list item number 2
* I wait for 3 seconds
Then I should see the currency
* I wait for 2 seconds
And I should see the specifications