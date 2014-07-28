@Calabash_Tests @search2 @f_2.0
Feature: 2.0 Features - Search

Background: 
Given I call the variables

Scenario: Choose Country
Given I select the country

Scenario: Search bar
Given I wait to see the home
When I click on search
And I enter a valid search on the text field


