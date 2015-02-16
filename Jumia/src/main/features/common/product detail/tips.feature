@tips @Calabash_Tests @f_pre_1.9
Feature: PDV - Tips

Background: 
* I call the variables

@tip_product_first_time
Scenario: See the tips
* I select the country
* I wait to see the home
* I open the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 5 seconds
* I press Got it
* I wait for 3 seconds
* I press grid item number 2
* I wait for 3 seconds
* I should see the first tip
* I swipe left moving with 10 steps
* I wait for 2 seconds
* I should see the second tip
* I wait for 2 seconds
* I should see the got it button

@tip_product_second_time
Scenario: Close the tips
* I wait to see the home
* I open the navigation menu
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I enter a valid Category
* I wait for 2 seconds
* I press grid item number 2
* I wait for 3 seconds
* I press Got it
* I wait for 2 seconds
* I should not see the tips
	