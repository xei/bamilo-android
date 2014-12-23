@Calabash_Tests @sort @f_pre_1.9
Feature: Catalog - Sorting

Background: 
* I call the variables
* I select the country

Scenario: Test sorting filters
* I wait to see the home
* I wait for 2 seconds
* I click on the search bar
* I write a valid result on the search bar
* I wait for 2 seconds
* I press list item number 1
* I wait for 2 seconds 
* I press Got it
* I wait for 2 seconds
* I should see the filter popularity
* I swipe left moving with 10 steps
* I wait for 2 seconds
* I should see the filter new in
* I swipe left moving with 10 steps
* I wait for 2 seconds
* I should see the filter price up
* I swipe left moving with 10 steps
* I wait for 2 seconds
* I should see the filter price down
* I swipe left moving with 10 steps
* I wait for 2 seconds
* I should see the filter name
* I swipe left moving with 10 steps
* I wait for 2 seconds
* I should see the filter brand
* I swipe left moving with 10 steps
# best ratings left