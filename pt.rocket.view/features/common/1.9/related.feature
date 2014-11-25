@Calabash_Tests @related @f_1.9
Feature: 1.9 Features - Realted Items

Background: 
* I call the variables

@related_situation
Scenario: Situation
* I select the country
* I wait to see the home
* I open the navigation menu
* I should see the menu sections
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I press Got it
* I wait for 3 seconds
* I press grid item number 2
* I wait for 5 seconds
* I press Got it
* I scroll down
* I should see the related items

@related_navigation
Scenario: Navigation through the products
* I wait to see the home
* I open the navigation menu
* I should see the menu sections
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I press grid item number 2
* I wait for 5 seconds
* I swipe up moving with 10 steps
* I swipe up moving with 10 steps
* I swipe up moving with 10 steps
* I swipe up moving with 10 steps
* I swipe up moving with 10 steps
* I swipe up moving with 10 steps
* I swipe left on botton moving with 10 steps
	
@related_image
Scenario: Click on related item
* I wait to see the home
* I open the navigation menu
* I should see the menu sections
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I press grid item number 2
* I wait for 5 seconds
* I swipe up moving with 10 steps
* I swipe up moving with 10 steps
* I swipe up moving with 10 steps
* I swipe up moving with 10 steps
* I swipe up moving with 10 steps
* I swipe up moving with 10 steps
* I press on a related item

@related_search
Scenario: Open a product by search
* I wait to see the home
* I click on the search bar
* I write a valid result on the search bar
* I wait for 5 seconds
* I press list item number 1
#Then I click on search icon
* I wait for 5 seconds	
* I press grid item number 2
* I wait for 5 seconds
* I scroll down
* I should see the related items

@related_home
Scenario: Open a product on home
* I wait to see the home
* I press on a related item
#Then I should not see the related items
	
