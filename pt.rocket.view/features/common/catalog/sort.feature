@Calabash_Tests @sort
Feature: Sorting Feature

Background: 
Given I call the variables
And I select the country
And I wait for 10 seconds

Scenario: Test sorting filters
When I click on the search bar
And I enter a valid search
* I wait for 2 seconds 
And I click on search icon
Then I should see the filter popularity
When I swipe left moving with 10 steps
Then I should see the filter new in
When I swipe left moving with 10 steps
Then I should see the filter price up
When I swipe left moving with 10 steps
Then I should see the filter price down
When I swipe left moving with 10 steps
Then I should see the filter name
When I swipe left moving with 10 steps
Then I should see the filter brand
When I swipe left moving with 10 steps
Then I should see the filter best rating
When I swipe left moving with 10 steps