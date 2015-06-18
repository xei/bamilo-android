@Calabash_Tests @search_home  @f_pre_1.9
Feature: Search feature on home screen

Background: 
* I call the variables

@search_home_a
Scenario: See the search bar on the home screen
* I select the country
* I wait for 3 seconds
* I wait to see the home
* I wait for 2 seconds
* I click on the search bar

@search_home_b
Scenario: Write a valid result and see the search suggestions layer
* I wait to see the home
* I wait for 2 seconds
* I click on the search bar
* I wait for 2 seconds
* I write a valid result on the search bar
* I wait for 2 seconds
* I press list item number 1




