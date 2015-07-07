@Calabash_Tests @catalog3 @f_pre_1.9
Feature: Catalog - View

Background: 
* I call the variables

@catalog
Scenario: Catalog view
* I select the country
* I wait for 2 seconds
* I wait to see the home
* I open the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I should see the Catalog
	
@infinite_scroll
Scenario: Scroll down to the end of the page
* I wait to see the home
* I open the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I press Got it
* I wait for 2 seconds
* I should see the Catalog
* I wait for 1 seconds
* I swipe up moving with 1 steps
* I swipe up moving with 1 steps
* I swipe up moving with 1 steps
* I swipe up moving with 1 steps
#* I should see the loading items message
	
	