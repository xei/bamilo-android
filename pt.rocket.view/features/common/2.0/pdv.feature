@Calabash_Tests @pdv2 @f_2.0
Feature: 2.0 Features - PDV

Background: 
* I call the variables

Scenario: Choose Country
* I select the country

Scenario: Check the favorite icon on PDV
* I wait to see the home
* I open the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I press Got it
* I wait for 2 seconds
* I press grid item number 1
* I wait for 3 seconds
* I press Got it
* I wait for 2 seconds
* I click on the favorite icon
* I should see the item added message
* I wait for 2 seconds
* I click on the favorite icon
* I wait for 1 seconds
* I should see the item removed message

Scenario: Check the search icon on PDV
* I wait to see the home
* I open the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I press grid item number 2
* I wait for 2 seconds
* I should see the search icon