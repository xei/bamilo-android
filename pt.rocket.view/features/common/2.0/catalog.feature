@Calabash_Tests @catalog2 @f_2.0
Feature: 2.0 Features - Catalog

Background: 
* I call the variables

Scenario: Choose Country
* I select the country

Scenario: Change view on catalog
* I wait to see the home
* I open the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I should see the Catalog
* I press the button to change the view

Scenario: Check the star icon in each item
* I wait to see the home
* I open the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I press Got it
* I wait for 2 seconds
* I press grid item number 2
* I wait for 2 seconds
* I press Got it
* I wait for 2 seconds
* I click on the favorite icon
* I wait for 1 seconds
* I should see the item added message
* I wait for 2 seconds
* I click on the favorite icon
* I wait for 1 seconds
* I should see the item removed message