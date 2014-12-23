@Calabash_Tests @overview @f_pre_1.9
Feature: PDV - Check the product detail view

Background: 
* I call the variables

@product_full
Scenario: Image Fullscreen
* I select the country
* I wait for 2 seconds
* I wait to see the home
* I wait for 2 seconds
* I open the navigation menu
* I wait for 5 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 5 seconds
* I press Got it
* I wait for 2 seconds
* I press grid item number 2
* I wait for 5 seconds
* I press Got it
* I wait for 2 seconds
* I click in the image
* I wait for 2 seconds
* I should not see the currency
* I wait for 2 seconds
* I go back
	
@product_zoom
Scenario: Zoom Image
* I wait to see the home
* I open the navigation menu
* I wait for 5 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 5 seconds
* I press grid item number 2
* I wait for 5 seconds
* I click in the image
#* I wait for 2 seconds
#* I click in the image
#* I click in the image
#* I pinch to zoom in
#* I go back
	
@product_details
Scenario: Details
* I wait to see the home
* I open the navigation menu
* I wait for 3 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I should see the currency
* I wait for 2 seconds
* I press grid item number 2
* I wait for 3 seconds
#* I should see the currency
* I scroll down
* I wait for 2 seconds
* I should see the specifications