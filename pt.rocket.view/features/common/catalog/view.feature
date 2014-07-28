@Calabash_Tests @catalog3
Feature: Catalog

Background: 
Given I call the variables

@catalog
Scenario: Catalogue view
Given I select the country
And I wait to see the home
And I open the navigation menu
And I enter a valid Category
And I enter a valid Category
* I wait for 3 seconds
Then I should see the Catalog
	
@infinite_scroll
Scenario: Scroll down to the end of the page
And I wait to see the home
And I open the navigation menu
And I enter a valid Category
And I enter a valid Category
Then I should see the Catalog
When I swipe up moving with 1 steps
When I swipe up moving with 1 steps
When I swipe up moving with 1 steps
When I swipe up moving with 1 steps
#Then I should see the loading items message
	
	