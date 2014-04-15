@tips @Calabash_Tests
Feature: Tips

	Background: 
    Given I call the variables
    
    Scenario: See the tips
	And I select the country
	And I wait for 5 seconds
    When I open the navigation menu
    And I enter Categories
	And I enter a valid Category
	And I press list item number 1
	And I press list item number 4
	* I wait for 3 seconds
	Then I should see the first tip
	When I swipe left moving with 10 steps
	Then I should see the second tip
	And I should see the got it button
	
	Scenario: Close the tips
    When I open the navigation menu
    And I enter Categories
	And I enter a valid Category
	And I press list item number 1
	And I press list item number 4
	* I wait for 3 seconds
	When I press Got it
	* I wait for 2 seconds
	Then I should not see the tips
	