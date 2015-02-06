@Calabash_Tests @search @f_pre_1.9
Feature: Catalog - Search

Background: 
Given I call the variables

@search_i
Scenario: I search an invalid product
* I select the country
* I wait for 2 seconds 
* I wait to see the home
* I wait for 2 seconds 
* I click on search 
* I wait for 2 seconds 
* I enter a invalid search
   
@search_s
Scenario: I search a valid product
* I wait to see the home
* I click on the search bar
* I wait for 2 seconds 
* I write a valid result on the search bar
* I wait for 5 seconds
* I press list item number 1
* I wait for 2 seconds 
* I press Got it
* I wait for 2 seconds 
* I press grid item number 1
* I wait for 2 seconds 
* I should see the add to cart button