@Calabash_Tests @search_home
Feature: Search feature on home screen

Background: 
Given I call the variables

@search_home_a
Scenario: See the search bar on the home screen
And I select the country
And I wait for 5 seconds
Then I should see the search bar

@search_home_b
Scenario: Write a valid result and see the search suggestions layer3
When I click on the search bar
And I write a valid result on the search bar
Then I press list item number 1

@search_home_c
Scenario: Check recently searched
When I click on the search bar
* I wait for 5 seconds
Then I press list item number 1




