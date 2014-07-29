@Calabash_Tests @filters @f_pre_1.9
Feature: Catalog - Filters

Background: 
Given I call the variables

@filters_a
Scenario: Open the filters
Given I select the country
And I wait to see the home
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
* I wait for 3 seconds
Then I press Got it
When I click in Filter
Then I should see the filters

@filters_b
Scenario: Apply filters
Given I wait to see the home
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
* I wait for 3 seconds
When I click in Filter
* I wait for 2 seconds
And I press list item number 1
* I wait for 2 seconds
And I press list item number 1
* I wait for 2 seconds
And I press Done
#And I press Done

@filters_c
Scenario: Remove All Filters
Given I wait to see the home
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
* I wait for 3 seconds
When I click in Filter
* I wait for 1 seconds
And I press list item number 1
* I wait for 1 seconds
And I press list item number 1
* I wait for 1 seconds
And I press Done
* I wait for 3 seconds
And I press Done
* I wait for 1 seconds
And I click in Filter
* I wait for 1 seconds
And I press on clear all
* I wait for 1 seconds
And I press Done

@filters_d
Scenario: Remove a filter
Given I wait to see the home
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
* I wait for 3 seconds
When I click in Filter
And I press list item number 1
* I wait for 2 seconds
And I press list item number 1
* I wait for 2 seconds
And I press Done
* I wait for 2 seconds
And I press Done
* I wait for 2 seconds
When I click in Filter
And I press list item number 1
And I press list item number 1
And I press Done
* I wait for 3 seconds
#And I press Done

@filters_e
Scenario: Close the filter screen
Given I wait to see the home
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
* I wait for 5 seconds
When I click in Filter
And I press Done
#Then I should not see the filter screen

