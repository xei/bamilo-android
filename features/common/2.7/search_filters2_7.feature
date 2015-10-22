@search_filters @Calabash_Tests @f_2.7
Feature: Search with Filters 2.7

Background:
* I call the variables

@search_a
Scenario: See the search bar on the home screen
* I select the country
* I wait to see the home
* I wait for 1 seconds
* I click on search
* I wait for 1 seconds
* I enter a valid search
* I press list item number 1
* I wait for 4 seconds
* I press Got it
* I should see the product with query
