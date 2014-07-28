@Calabash_Tests @catalog2 @f_2.0
Feature: 2.0 Features - Catalog

Background: 
Given I call the variables

Scenario: Choose Country
When I select the country

Scenario: Change view on catalog
Given I wait to see the home
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
* I wait for 3 seconds
Then I should see the Catalog
And I press the button to change the view

Scenario: Check the star icon in each item
Given I wait to see the home
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
* I wait for 3 seconds
And I press list item number 2
* I wait for 3 seconds
And I press Got it
And I click on the favorite icon
Then I should see the item added message
When I click on the favorite icon
Then I should see the item removed message