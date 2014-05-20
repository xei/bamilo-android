@Calabash_Tests @search_undefined
Feature: Search Undefined

Background: 
Given I call the variables

Scenario: Search an invalid product
When I select the country
* I wait for 5 seconds

And I click on the search bar
And I write a invalid result on the search bar
And I click on search image
* I wait for 3 seconds
Then I should see the no results text
And I should see the search tips