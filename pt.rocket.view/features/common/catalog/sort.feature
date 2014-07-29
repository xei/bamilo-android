@Calabash_Tests @sort @f_pre_1.9
Feature: Catalog - Sorting

Background: 
Given I call the variables
And I select the country

Scenario: Test sorting filters
Given I wait to see the home
When I click on the search bar
And I write a valid result on the search bar
And I wait for 5 seconds
And I press list item number 1
* I wait for 2 seconds 
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