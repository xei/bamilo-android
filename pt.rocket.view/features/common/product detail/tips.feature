@tips @Calabash_Tests
Feature: Tips

Background: 
Given I call the variables

@tip_product_first_time
Scenario: See the tips
Given I select the country
And I wait to see the home
When I open the navigation menu
And I enter a valid Category
And I enter a valid Category
* I wait for 5 seconds
And I press list item number 2
* I wait for 3 seconds
Then I should see the first tip
When I swipe left moving with 10 steps
Then I should see the second tip
And I should see the got it button

@tip_product_second_time
Scenario: Close the tips
Given I wait to see the home
When I open the navigation menu
* I wait for 1 seconds
And I enter a valid Category
And I enter a valid Category
And I press list item number 2
* I wait for 7 seconds
When I press Got it
* I wait for 2 seconds
Then I should not see the tips
	