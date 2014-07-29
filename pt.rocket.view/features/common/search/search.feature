@Calabash_Tests @search_home  @f_pre_1.9
Feature: Search feature on home screen

Background: 
Given I call the variables

@search_home_a
Scenario: See the search bar on the home screen
Given I select the country
And I wait to see the home
Then I click on the search bar

@search_home_b
Scenario: Write a valid result and see the search suggestions layer3
Given I wait to see the home
When I click on the search bar
And I write a valid result on the search bar
Then I press list item number 1

#@search_home_c
#Scenario: Check recently searched
#Given I wait to see the home
#When I click on the search bar
#* I wait for 5 seconds
#Then I press list item number 1




