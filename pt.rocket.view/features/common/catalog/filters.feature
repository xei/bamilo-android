@Calabash_Tests @filters
Feature: Filters

Background: 
Given I call the variables

@filters_a
Scenario: Open the filters
And I select the country
And I wait for 5 seconds
When I open the navigation menu
And I enter Categories
* I wait for 3 seconds
And I enter a valid Category
* I wait for 3 seconds
And I press list item number 1
* I wait for 3 seconds
When I click in Filter
Then I should see the filters

@filters_b
Scenario: Apply filters
When I open the navigation menu
And I enter Categories
* I wait for 3 seconds
And I enter a valid Category
* I wait for 3 seconds
And I press list item number 1
* I wait for 3 seconds
When I click in Filter
And I press list item number 1
And I press list item number 2
And I press Done
And I press Done

@filters_c
Scenario: Remove All Filters
When I open the navigation menu
And I enter Categories
* I wait for 3 seconds
And I enter a valid Category
* I wait for 3 seconds
And I press list item number 1
* I wait for 3 seconds
When I click in Filter
And I press list item number 1
And I press list item number 2
And I press Done
* I wait for 3 seconds
And I press Done
And I click in Filter
And I press on clear all
And I press Done

@filters_d
Scenario: Remove a filter
When I open the navigation menu
And I enter Categories
* I wait for 3 seconds
And I enter a valid Category
* I wait for 3 seconds
And I press list item number 1
* I wait for 3 seconds
When I click in Filter
And I press list item number 1
And I press list item number 2
And I press Done
* I wait for 3 seconds
And I press Done
When I click in Filter
And I press list item number 1
And I press list item number 2
And I press Done
* I wait for 3 seconds
And I press Done

@filters_e
Scenario: Close the filter screen
When I open the navigation menu
And I enter Categories
* I wait for 3 seconds
And I enter a valid Category
* I wait for 3 seconds
And I press list item number 1
* I wait for 3 seconds
When I click in Filter
And I press Done
Then I should not see the filter screen

