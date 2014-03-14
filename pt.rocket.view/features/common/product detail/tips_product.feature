@tips_product
Feature: Tips

	Background: 
    Given I call the variables
    When I open the navigation menu
    And I enter Categories
	And I enter a valid Category
	And I press list item number 1
	And I press list item number 4
	* I wait for 3 seconds
	
	@tip_product_first_time
	Scenario: See the tip with fresh install
	Then I should see the first tip
	When I swipe left moving with 10 steps
	Then I should see the second tip
	And I should see the got it button
	#Scenario: Close the tips
	When I press Got it
	* I wait for 2 seconds
	Then I should not see the tips
	
	@tip_product_second_time
	Scenario: Check the tip after closed one time
	Then I should not see the tips