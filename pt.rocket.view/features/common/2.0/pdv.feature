@Calabash_Tests @pdv2 @f_2.0
Feature: 2.0 Features - PDV

Background: 
Given I call the variables

Scenario: Choose Country
When I select the country

Scenario: Check the favorite icon on PDV
Given I wait to see the home
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
* I wait for 3 seconds
And I press list item number 2
* I wait for 3 seconds
And I press Got it
#And I press list item number 2
#* I wait for 2 seconds
#And I press Got it
And I click on the favorite icon
Then I should see the item added message
When I click on the favorite icon
Then I should see the item removed message

Scenario: Check the search icon on PDV
Given I wait to see the home
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
* I wait for 3 seconds
And I press list item number 2
#* I wait for 3 seconds
#And I press list item number 2
* I wait for 2 seconds
Then I should see the search icon