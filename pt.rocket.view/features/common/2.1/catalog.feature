@Calabash_Tests @f_2.1 @scroll_up_button
Feature: 2.1 Features - Catalog

Background: 
Given I call the variables

Scenario: Choose Country
Given I select the country

Scenario:
Given I wait to see the home
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
* I wait for 3 seconds
And I press Got it
And I swipe up moving with 10 steps
And I click on scroll up button
