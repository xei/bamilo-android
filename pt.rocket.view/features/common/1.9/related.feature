@Calabash_Tests @related @f_1.9
Feature: 1.9 Features - Realted Items

Background: 
Given I call the variables

@related_situation
Scenario: Situation
Given I select the country
And I wait to see the home
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
And I press list item number 1
* I wait for 3 seconds
And I press list item number 2
* I wait for 5 seconds
And I press Got it
Then I should see the related items

@related_navigation
Scenario: Navigation through the products
Given I wait to see the home
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
And I press list item number 1
* I wait for 3 seconds
And I press list item number 2
* I wait for 5 seconds
And I swipe up moving with 10 steps
And I swipe up moving with 10 steps
And I swipe up moving with 10 steps
And I swipe up moving with 10 steps
And I swipe up moving with 10 steps
And I swipe up moving with 10 steps
And I swipe left on botton moving with 10 steps
	
@related_image
Scenario: Click on related item
Given I wait to see the home
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
And I press list item number 1
* I wait for 3 seconds
And I press list item number 2
* I wait for 5 seconds
And I swipe up moving with 10 steps
And I swipe up moving with 10 steps
And I swipe up moving with 10 steps
And I swipe up moving with 10 steps
And I swipe up moving with 10 steps
And I swipe up moving with 10 steps
And I press on a related item

@related_search
Scenario: Open a product by search
Given I wait to see the home
When I click on the search bar
And I write a valid result on the search bar
And I wait for 5 seconds
And I press list item number 1
#Then I click on search icon
And I wait for 5 seconds	
And I press list item number 1
And I wait for 5 seconds
Then I should see the related items

@related_home
Scenario: Open a product on home
Given I wait to see the home
When I press on a related item
Then I should not see the related items
	
