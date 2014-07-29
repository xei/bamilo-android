@Calabash_Tests @search @f_pre_1.9
Feature: Catalog - Search

Background: 
Given I call the variables

@search_i
Scenario: I search an invalid product
Given I select the country
And I wait to see the home
When I click on search 
And I enter a invalid search
   
@search_s
Scenario: I search a valid product
Given I wait to see the home
When I click on the search bar
And I write a valid result on the search bar
And I wait for 5 seconds
And I press list item number 1
* I wait for 2 seconds 
And I press Got it
And I press list item number 1
Then I should see the add to cart button