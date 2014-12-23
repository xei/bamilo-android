@Calabash_Tests @filters @f_pre_1.9
Feature: Catalog - Filters

Background: 
* I call the variables

@filters_a
Scenario: Open the filters
* I select the country
* I wait for 2 seconds
* I wait to see the home
* I open the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I press Got it
* I wait for 2 seconds
* I click in Filter
* I wait for 2 seconds
* I should see the filters

@filters_b
Scenario: Apply filters
* I wait to see the home
* I wait for 2 seconds
* I open the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I click in Filter
* I wait for 2 seconds
* I press list item number 1
* I wait for 2 seconds
* I press list item number 1
* I wait for 2 seconds
* I press Done

@filters_c
Scenario: Remove All Filters
* I wait to see the home
* I open the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I click in Filter
* I wait for 2 seconds
* I press list item number 1
* I wait for 2 seconds
* I press list item number 1
* I wait for 2 seconds
* I press Done
* I wait for 3 seconds
* I press Done
* I wait for 1 seconds
* I click in Filter
* I wait for 1 seconds
* I press on clear all
* I wait for 1 seconds
* I press Done

@filters_d
Scenario: Remove a filter
* I wait to see the home
* I wait for 2 seconds
* I open the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 3 seconds
* I click in Filter
* I press list item number 1
* I wait for 2 seconds
* I press list item number 1
* I wait for 2 seconds
* I press Done
* I wait for 2 seconds
* I press Done
* I wait for 2 seconds
* I click in Filter
* I press list item number 1
* I wait for 2 seconds
* I press list item number 1
* I press Done

@filters_e
Scenario: Close the filter screen
* I wait to see the home
* I open the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 5 seconds
* I click in Filter
* I press Done
